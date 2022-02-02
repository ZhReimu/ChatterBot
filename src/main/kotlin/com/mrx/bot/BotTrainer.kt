@file:Suppress("unused")

package com.mrx.bot

import com.mrx.MybatisConfig
import com.mrx.mapper.QuestionMapper
import com.mrx.model.Question
import java.io.File

object BotTrainer {

    private val sf = MybatisConfig.getSqlSessionFactory(QuestionMapper::class.java)

    private val questions = ArrayList<Question>()

    /**
     * 根据语料库训练?数据库 其实就是将语料库格式化存放到数据库中
     * 顺便说一下 小黄鸡 的 xiaohuangji50w_nofenci.conv 语料库属实整不动了,
     * 这个方法的时间复杂度直接报表, 处理了 38w 条 Answer 后,
     * 每 1w 条记录需要花费将近 10 分钟, 太哈人了,
     * 总共大概有 130w 条回复
     * @param file String 语料库所在路径
     */
    fun trainCorpusFile(file: String) {
        var count = 0
        val br = File(file).inputStream().bufferedReader()
        var session = sf.openSession(false)
        var qDao = session.getMapper(QuestionMapper::class.java)

        while (true) {
            val line = br.readLine() ?: break
            // 读取文件, 序列化为对象
            val ques = Question().apply {
                val t = line.split("\t")
                question = t[0]
                answer = arrayListOf(t[1].replace("\"", "'"))
            }
            // 判断是否在数据库中出现过重复的 question
            if (session == null) {
                session = sf.openSession(false)
                qDao = session.getMapper(QuestionMapper::class.java)
            }
            val quesInDB = qDao.findAnswerByQuestion(ques.question)
            if (quesInDB != null) {
                // 如果出现过, 那就只为其插入 answer
                qDao.addAnswer(quesInDB.id, ques.answer[0])
            } else {
                var isFound = false
                // 否则, 判断是否在 内存 中出现过 question
                // 如果是,遍历 缓存队列, 找出 question
                questions.forEach {
                    if (it == ques) {
                        // 为其添加 answer
                        it.answer.addAll(ques.answer)
                        isFound = true
                    }
                }
                // 如果没有出现过, 那就直接加入缓存
                if (!isFound) {
                    this.questions.add(ques)
                }
            }

            if (count++ % 10000 == 0) {
                // 如果之前有事务未处理, 那就先提交一下
                session?.let {
                    it.commit()
                    it.close()
                    session = null
                }
                this.questions.saveData()
                println("已处理 $count 条数据")
            }
        }
        // 如果之前有事务未处理, 那就先提交一下, Sqlite 似乎不支持同时打开多个 SqlSession ?
        session?.let {
            it.commit()
            it.close()
            session = null
        }
        this.questions.saveData(true)
        println("成功加载 $count 条语料")
    }

    /**
     * 将数据写入数据库
     * @receiver ArrayList<Question> 要写入的数据队列
     * @param isLast Boolean 是否为最后一趟数据
     */
    private fun ArrayList<Question>.saveData(isLast: Boolean = false) {
        val session = sf.openSession(false)
        val qDao = session.getMapper(QuestionMapper::class.java)
        this.forEach {
            // 先插 Question, 若插入成功将会更新 Question 的 id 字段
            qDao.addQuestion(it)
            // 再插 Answer, 每个 Answer 的 qid 都对应上边更新了的 id
            it.answer.forEach { q ->
                qDao.addAnswer(it.id, q)
            }
        }
        session.commit()
        session.close()
        if (isLast) {
            println("最后 ${this.size} 条数据处理完毕")
        }
        this.clear()
    }
}