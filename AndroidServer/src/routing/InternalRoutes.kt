package org.vontech.androidserver.routing

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.vontech.androidserver.logger
import org.vontech.androidserver.testConfig
import org.vontech.core.server.StartupEvent

fun Route.internalRoutes() {

    route("/test") {
        get {
            logger?.info("WE CALLED IT")
        }
    }

    route("/getProjectInfo") {
        get {
            call.respond(testConfig!!)
        }
    }

    route("/receiveStartupEvent") {
        post {
            val event: StartupEvent = call.receive()
            logger?.info("STARTUP RECEIVED: $event")
            call.respond(200)
        }
    }

}