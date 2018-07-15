package org.vontech

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.content.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.jackson.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.DevelopmentEngine.main(args)

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

fun Application.module() {
	install(CallLogging) {
		level = Level.INFO
		filter { call -> call.request.path().startsWith("/") }
	}
	install(Authentication) {
	}
	install(ContentNegotiation) {
		jackson {
			enable(SerializationFeature.INDENT_OUTPUT)
		}
	}
	// Feel free to use FreeMarker or kotlinx.html for html returns
	install(FreeMarker) {
		templateLoader = ClassTemplateLoader(Application::class.java.classLoader, "templates")
	}

	routing {
		get("/") {
			call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
		}

		get("/html-dsl") {
			call.respondHtml {
				body {
					h1 { +"HTML" }
					ul {
						for (n in 1..10) {
							li { +"$n" }
						}
					}
				}
			}
		}

		get("/freemarker") {
			call.respond(FreeMarkerContent("index.ftl", mapOf("name" to "Aaron", "email" to "vontell@mit.edu"), "e"))
		}

		get("/styles.css") {
			call.respondCss {
				body {
					backgroundColor = Color.red
				}
				p {
					fontSize = 2.em
				}
				rule("p.myclass") {
					color = Color.blue
				}
			}
		}

		// Static feature. Try to access `/static/ktor_logo.svg`
		static("/static") {
			resources("static")
		}

		install(StatusPages) {
			exception<AuthenticationException> {  cause ->
				call.respond(HttpStatusCode.Unauthorized)
			}
			exception<AuthorizationException> {  cause ->
				call.respond(HttpStatusCode.Forbidden)
			}
		}

		get("/json/jackson") {
			call.respond(mapOf("hello" to "world"))
		}
	}
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
	style(type = ContentType.Text.CSS.toString()) {
		+CSSBuilder().apply(builder).toString()
	}
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
	this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
	this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
