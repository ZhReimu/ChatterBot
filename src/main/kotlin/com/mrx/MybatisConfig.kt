package com.mrx

import com.alibaba.druid.pool.DruidDataSource
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory

/**
 * 使用代码配置 Mybatis
 */
object MybatisConfig {

    private val ds = DruidDataSource().apply {
        name = "SQLITE"
        url = "jdbc:sqlite:corpus.sqlite"
        driverClassName = "org.sqlite.JDBC"
        initialSize = 5
        minIdle = 5
        maxActive = 10
        maxWait = 10000
        isTestWhileIdle = true
        validationQuery = "SELECT 1"
    }

    private val transactionFactory = JdbcTransactionFactory()

    private val environment = Environment("dev", transactionFactory, ds)

    fun <T> getSqlSessionFactory(mapperClazz: Class<T>): SqlSessionFactory {
        val configuration = Configuration(environment)
        configuration.addMapper(mapperClazz)
        return SqlSessionFactoryBuilder().build(configuration)
    }

}