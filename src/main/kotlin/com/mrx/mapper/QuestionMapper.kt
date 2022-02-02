@file:Suppress("unused")

package com.mrx.mapper

import com.mrx.model.Question
import org.apache.ibatis.annotations.*
import org.apache.ibatis.binding.MapperMethod
import org.apache.ibatis.mapping.FetchType

interface QuestionMapper {
    @Insert(
        "INSERT INTO question(Q) values(#{question})"
    )
    @Options(
        useGeneratedKeys = true,
        keyProperty = "id"
    )
    fun addQuestion(question: Question)

    @Insert(
        "INSERT INTO answer(qid, A) values(#{id}, #{answer})"
    )
    fun addAnswer(@Param("id") id: Int, @Param("answer") answer: String)

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

    @Select("SELECT A FROM answer WHERE qid = #{id}")
    fun findAnswerById(@Param("id") id: Int): List<String>

    @InsertProvider(type = BatchProvider::class, method = "batchInsertQuestion")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun bathAddQuestion(list: List<Question>)

    @InsertProvider(type = BatchProvider::class, method = "batchInsertAnswer")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun bathAddAnswer(list: List<Question>)

    class BatchProvider {

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