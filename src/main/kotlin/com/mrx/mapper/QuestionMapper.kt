@file:Suppress("unused")

package com.mrx.mapper

import com.mrx.model.Question
import org.apache.ibatis.annotations.*
import org.apache.ibatis.binding.MapperMethod
import org.apache.ibatis.mapping.FetchType

interface QuestionMapper {
    /**
     * 插入一条 Question
     * @param question Question 要插入的问题
     */
    @Insert(
        "INSERT INTO question(Q) values(#{question})"
    )
    @Options(
        useGeneratedKeys = true,
        keyProperty = "id"
    )
    fun addQuestion(question: Question)

    /**
     * 插入一条 Answer
     * @param id Int 该回答是属于哪个 Question 的
     * @param answer String 回答内容
     */
    @Insert(
        "INSERT INTO answer(qid, A) values(#{id}, #{answer})"
    )
    fun addAnswer(@Param("id") id: Int, @Param("answer") answer: String)

    /**
     * 使用 Question 内容查找可能的 Answer
     * @param q String 问题内容
     * @return Question? 找到的 Question 对象, 如果没找到会返回 null
     */
    @Select("SELECT * FROM question WHERE Q = #{q}")
    @Results(
        Result(property = "id", column = "id", id = true),
        Result(property = "question", column = "Q"),
        Result(
            property = "answer", javaType = List::class, column = "id",
            many = Many(select = "com.mrx.mapper.QuestionMapper.findAnswerById", fetchType = FetchType.EAGER)
        )
    )
    fun findAnswerByQuestion(@Param("q") q: String): Question?

    /**
     * 使用 Question id 查找 Answer
     * @param id Int 要查找的 id
     * @return List<String> Answer 可能有多个数据
     */
    @Select("SELECT A FROM answer WHERE qid = #{id}")
    fun findAnswerById(@Param("id") id: Int): List<String>

    /*
    * 下边的 批量插入 没弄好, 懒得删了
    */

    @InsertProvider(type = BatchProvider::class, method = "batchInsertQuestion")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Deprecated(message = "没弄好, 懒得管了")
    fun bathAddQuestion(list: List<Question>)

    @InsertProvider(type = BatchProvider::class, method = "batchInsertAnswer")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Deprecated(message = "没弄好, 懒得管了")
    fun bathAddAnswer(list: List<Question>)
    
    class BatchProvider {

        @Deprecated(message = "没弄好, 懒得管了")
        fun batchInsertQuestion(obj: MapperMethod.ParamMap<String>): String {
            val list = obj["list"] as List<*>
            val sb = StringBuilder("INSERT INTO question(Q) VALUES ")
            val listSize = list.size
            list.forEachIndexed { index, q ->
                val ques = q as Question
                sb.append("('${ques.question}')")
                if (index < listSize - 1) {
                    sb.append(",")
                }
            }
            return sb.toString()
        }

        @Deprecated(message = "没弄好, 懒得管了")
        fun batchInsertAnswer(obj: MapperMethod.ParamMap<String>): String {
            val list = obj["list"] as List<*>
            val sb = StringBuilder("INSERT INTO answer(qid, A) VALUES ")
            val listSize = list.size
            list.forEachIndexed { index, a ->
                val ans = a as Question
                sb.append("""("${ans.id}", "${ans.answer[0]}")""")
                if (index < listSize - 1) {
                    sb.append(",")
                }
            }
            return sb.toString()
        }
    }
}