package com.example.dao

import com.example.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

import io.ktor.server.config.*
import java.io.*

import com.zaxxer.hikari.*

object DatabaseSingleton {
    private fun createHikariDataSource(
        url: String,
        driver: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
    fun init(config: ApplicationConfig) {
        //val driverClassName = "org.h2.Driver"
        val driverClassName
        = config.property("storage.driverClassName").getString()

        // val jdbcURL = "jdbc:h2:file:./build/db"
        val jdbcURL
        = config.property("storage.jdbcURL").getString() +
          (config.propertyOrNull("storage.dbFilePath")?.getString()?.let {
              File(it).canonicalFile.absolutePath
          } ?: "")

        // val database = Database.connect(jdbcURL, driverClassName)
        val database = Database.connect(createHikariDataSource(
            url = jdbcURL, driver = driverClassName
        ))
        transaction(database) {
            // Statements here
            SchemaUtils.create(Articles)
        }
    }
    
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}