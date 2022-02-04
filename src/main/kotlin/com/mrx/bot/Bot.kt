package com.mrx.bot

import com.mrx.MybatisConfig
import com.mrx.XLog
import com.mrx.mapper.QuestionMapper
import com.mrx.model.Question
import java.io.BufferedReader
import java.io.InputStreamReader

class Bot(private val name: String) {

    private val sf = MybatisConfig.getSqlSessionFactory(QuestionMapper::class.java)

    private val logger = XLog.getLogger(this::class)

    private val questionHistory = ArrayList<Question>()

    private val defaultQuestion = "不知道捏".toQuestion()

    /**
     * 使用控制台进行聊天
     */
    fun chat() {
        val br = BufferedReader(InputStreamReader(System.`in`))
        while (true) {
            print("YOU: ")
            val q = br.readLine()
            if (q == "exit") {
                break
            }
            println("$name: ${respond(q.toQuestion())}")
        }
    }

    /**
     * 对指定的问题查找回答
     * @param question Question 要查找的问题
     * @return String 找到的回答
     */
    fun respond(question: Question): String {
        if (question in questionHistory) {
            return (questionHistory findQuestionByQuestion question).getRandomAnswer()
        }
        val session = sf.openSession()
        val qDao = session.getMapper(QuestionMapper::class.java)
        val res = qDao.findAnswerByQuestion(question.question).also {
            logger.debug(it)
        } ?: return "不知道捏"
        session.close()
        questionHistory.add(res)
        return res.getRandomAnswer()
    }

    private fun String.toQuestion() = Question().also { it.question = this }

    private infix fun ArrayList<Question>.findQuestionByQuestion(question: Question): Question {
        this.forEach {
            if (it == question) {
                return it
            }
        }
        return defaultQuestion
    }

}
