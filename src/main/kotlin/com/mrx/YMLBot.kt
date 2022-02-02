package com.mrx

import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object YMLBot {

    class Bot private constructor(
        val name: String,
        private val conversations: List<Conversations>
    ) {

        private companion object {
            private const val DEFAULT_ANSWER = "不知道捏"
        }

        fun chat() {
            val br = BufferedReader(InputStreamReader(System.`in`))
            while (true) {
                print("YOU: ")
                val question = br.readLine()
                println("$name : ${respond(question)}")
            }
        }

        fun respond(question: String): String {
            conversations.forEach {
                it.questions.forEach { q ->
                    if (q.question.equals(question, false)) {
                        return q.answer
                    }
                }
            }
            return DEFAULT_ANSWER
        }

        object Builder {

            private var name: String = "null"

            private val conversations: ArrayList<Conversations> = ArrayList()

            fun name(name: String): Builder {
                this.name = name
                return this
            }

            fun addCorpusPath(filePath: String): Builder {
                File(filePath).listFiles()!!.forEach {
                    if (it.name.endsWith("yml")) {
                        val conversation = Yaml().loadAs(it.inputStream(), Conversations::class.java)
                        this.conversations.add(conversation)
                        println("加载 -> $it")
                    }
                }
                val size = this.conversations.stream().mapToInt { it.questions.size }.sum()
                println("成功加载 $size 条语料")
                return this
            }

            fun build(): Bot = Bot(name, conversations)
        }

        data class Conversations(
            var categories: String = "null",
            var conversations: List<List<String>> = emptyList()
        ) {

            data class Question(private val conversation: List<String>) {
                val question: String = conversation[0]
                val answer: String = conversation[1]

                override fun toString(): String {
                    return "Question(question='$question', answer='$answer')"
                }

            }

            val questions by lazy {
                ArrayList<Question>().apply {
                    conversations.forEach {
                        add(Question(it))
                    }
                }
            }

            override fun toString(): String {
                return "Conversations(categories=$categories, conversations=$conversations)"
            }

        }

    }

    @JvmStatic
    fun main(args: Array<String>) {
        val bot = Bot.Builder
            .name("test")
            .addCorpusPath("E:\\Jet Brains\\IntelliJ IDEA\\YAMLChatBot\\src\\main\\resources")
            .build()
        bot.chat()
    }
}