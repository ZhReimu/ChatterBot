package com.mrx.model

import kotlin.random.Random

/**
 * 问题 实体类
 * @property id Int 问题 id
 * @property question String 问题内容
 * @property answer ArrayList<String> 回答队列
 * @property rad Random 随机数工具
 */
open class Question {

    var id: Int = -1

    lateinit var question: String

    lateinit var answer: ArrayList<String>

    private val rad = Random(System.currentTimeMillis())

    /**
     * 随机取出一个回答
     * @return String 该问题的回答
     */
    fun getRandomAnswer(): String {
        return if (answer.isNotEmpty()) {
            answer[rad.nextInt(0, answer.size)]
        } else {
            "不知道你说的什么寄吧捏"
        }
    }

    override fun toString(): String {
        return "Question(id=$id, question='$question', answer=$answer)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Question
        // 如果 Question 内容相同, 就认为是同一个 Question
        if (question != other.question) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + question.hashCode()
        result = 31 * result + answer.hashCode()
        return result
    }

}
