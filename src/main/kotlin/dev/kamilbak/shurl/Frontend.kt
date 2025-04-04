package dev.kamilbak.shurl

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.configureFrontend() {
	routing {
		get("/") {
			call.respondHtml {
				head {
					title(System.getenv("SHURL_NAME") ?: "ShURL")
					style {
						unsafe {
							raw(
								"""
								html {
								  text-align: center;
								}
								form {
									margin-top: 1em;
									margin-bottom: 1em;
								}
								""".trimIndent()
							)
						}
					}
				}
				body {
					h1 { +"ShURL" }
					form(action = "create", method = FormMethod.post) {
						input(type = InputType.url, name = "destination") {
							required = true
							placeholder = "Destination URL"
						}
						submitInput()
					}
					when (call.queryParameters["error"]) {
						"redirect" -> {
							text("There was an error during redirection")
							br()
							b {
								text("Ensure that URL you used is correct")
							}
						}

						"create" -> {
							text("There was an error while creating your redirection")
							br()
							b {
								text("Try again in a minute")
							}
						}

						null -> Unit
						else -> text("There was an unknown error. Contact the site administrator")
					}
					call.queryParameters["success"].takeUnless { it.isNullOrBlank() }?.let {
						text("Successfully created redirection")
						br()
						val redirection = with(call.request.origin){
							val port = when{
								scheme == "http" && serverPort == 80 -> ""
								scheme == "https" && serverPort == 443 -> ""
								else -> ":${serverPort}"
							}
							"$scheme://$serverHost$port/$it"
						}
						a(href = redirection){
							text(redirection)
						}
						button {
							onClick = "navigator.clipboard.writeText('$redirection')"
							text("Copy")
						}
					}
				}
			}
		}
	}
}
