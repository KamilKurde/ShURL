package dev.kamilbak.shurl

import asia.hombre.keccak.api.SHAKE128
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.komputing.kbase58.encodeToBase58String
import org.slf4j.MarkerFactory
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue

fun Application.configureRedirections(database: Database) {
	val tag = MarkerFactory.getMarker("redirections")
	routing {
		var lastCreation = TimeSource.Monotonic.markNow() - 1.minutes
		post("/create") {
			if (lastCreation.plus(10.seconds).hasNotPassedNow())
				return@post call.respond(HttpStatusCode.TooManyRequests)

			val destination = call.receiveParameters()["destination"]
				?: return@post call.respondRedirect("/?error=create", permanent = false)

			database.redirectionsQueries.find(destination).executeAsOneOrNull()?.let { key ->
				return@post call.respondRedirect("/?success=$key", permanent = true)
			}

			try {
				val (key, took) = measureTimedValue { database.redirectionsQueries.createRedirection(destination) }

				call.respondRedirect("/?success=$key", permanent = true)

				log.info(
					tag,
					"""Successfully created redirection "$destination" <- "$key" in just $took for ${call.originAddress}"""
				)
				lastCreation = TimeSource.Monotonic.markNow()
			} catch (e: Exception) {
				call.respondRedirect("/?error=create", permanent = false)

				log.error(tag, """Failed to create redirection for "$destination"""")
			}
		}
		get("/{key}") {
			val key = call.parameters["key"] ?: ""
			val (destination, took) = measureTimedValue {
				database.redirectionsQueries.resolve(key).executeAsOneOrNull()
			}

			if (destination != null) {
				call.respondRedirect(destination, permanent = true)

				log.info(
					tag,
					"""Successfully redirected "$key" -> "$destination" in just $took for ${call.originAddress}"""
				)
			} else {
				call.respondRedirect("/?error=redirect", permanent = false)

				log.error(tag, """Failed to redirect "$key"""")
			}
		}
	}
}

private fun RedirectionsQueries.createRedirection(destination: String): String {
	val array = destination.toByteArray()

	var current: String
	var iteration = 1

	do {
		current = SHAKE128(iteration++).digest(array).encodeToBase58String()
	} while (resolve(current).executeAsOneOrNull() != null)

	add(current, destination)

	return current
}

private val RoutingCall.originAddress: String
	get() = with(request.origin) {
		if (remoteHost == remoteAddress) {
			remoteHost
		} else {
			"$remoteHost ($remoteAddress)"
		}
	}
