package com.mrx.model

import kotlin.random.Random

open class Question {

    var id: Int = -1

    lateinit var question: String

    lateinit var answer: ArrayList<String>

    private val rad = Random(System.currentTimeMillis())

    fun getRandomAnswer(): String {
        return if (answer.isNotEmpty()) {
            answer[rad.nextInt(0, answer.size)]
        } else {
            "不知道捏"
        }
    }

    override fun toString(): String {
        return "Question(id=$id, question='$question', answer=$answer)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Question

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
