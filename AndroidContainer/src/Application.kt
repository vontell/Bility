package org.vontech.androidserver

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.routing.*
import org.vontech.androidserver.drivers.android.AndroidDriver


fun getContainerInfo(): Map<String, Any> {
    return mapOf<String, Any>("version" to "0.0.1")
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.DevelopmentEngine.main(args)
fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    routing {

        get("/containerInformation") {
            call.respond(getContainerInfo())
        }

    }

    //println(AndroidDriver().isAndroidHomeSet())
    //println(AndroidDriver().isSdkAvailable())

}
