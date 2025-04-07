package dev.kamilbak.shurl

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

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
		val dbFileName = "shurl.db"
		val didDbExist = File(dbFileName).exists()
		driver = JdbcSqliteDriver("jdbc:sqlite:$dbFileName")
		if (didDbExist.not()) {
			Database.Schema.create(driver)
		}
		val database = Database(driver)
		configureFrontend()
		configureRedirections(database)
	} finally {
		driver?.close()
	}
}
