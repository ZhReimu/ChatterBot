package com.mrx

import java.util.stream.Collectors

object Test {

    private val logger = XLog.getLogger(this::class)

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = this::class.java.getResourceAsStream("/xiaohuangji.tsv")!!
            .bufferedReader().lines().collect(Collectors.toList())

    }
}