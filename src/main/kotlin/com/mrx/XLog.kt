@file:Suppress("UNUSED")

package com.mrx

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator
import kotlin.reflect.KClass

object XLog {

    init {
        val builder = ConfigurationFactory.newConfigurationBuilder()
        val console = builder.newAppender("stdout", "Console")
        val std = builder.newLayout("PatternLayout").apply {
            addAttribute(
                "pattern",
                "%highlight{[%p] %-d{yyyy-MM-dd HH:mm:ss} --> %l%n[massage] %m%n}{FATAL=red, ERROR=red, WARN=yellow, INFO=cyan, DEBUG=cyan,TRACE=blue}"
            )
        }
        console.add(std)
        builder.add(console)
        builder.add(builder.newRootLogger(Level.WARN).apply { add(builder.newAppenderRef("stdout")) })
        builder.add(builder.newLogger("com.mrx", Level.INFO))
        Configurator.initialize(builder.build())
    }

    fun getLogger(name: String): Logger = LogManager.getLogger(name)

    fun getLogger(clazz: Class<*>): Logger = LogManager.getLogger(clazz.name)

    fun getLogger(clazz: KClass<*>): Logger = getLogger(clazz.java)
}