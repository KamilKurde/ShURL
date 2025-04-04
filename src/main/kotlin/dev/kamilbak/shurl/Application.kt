package dev.kamilbak.shurl

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
	embeddedServer(
		Netty,
		port = System.getenv("SHURL_PORT")?.toIntOrNull() ?: 8080,
		host = System.getenv("SHURL_HOST") ?: "0.0.0.0",
		module = Application::module
	)
		.start(wait = true)
}

fun Application.module() {
	var driver : JdbcSqliteDriver? = null
	try {
		driver = JdbcSqliteDriver("jdbc:sqlite:shurl.db")
		val database = Database(driver)
		configureFrontend()
		configureRedirections(database)
	} finally {
		driver?.close()
	}
}
