package org.vontech

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.freemarker.FreeMarker
import io.ktor.jackson.*
import org.vontech.drivers.android.AndroidDriver

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
			call.respondText("HELLO WORLD FROM ANDROID!", contentType = ContentType.Text.Plain)
		}

	}

	// Run some quick tests
	this.log.debug("Is ANDROID_HOME set? ${AndroidDriver().isAndroidHomeSet()}")
	this.log.debug("Is Android SDK available? ${AndroidDriver().isSdkAvailable()}")

}
