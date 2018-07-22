package org.vontech.androidserver.routing

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.vontech.androidserver.thisContainer

fun Route.surfacedRoutes() {

    get("/information") {
        call.respond(thisContainer)
    }

    get("/") {
        call.respond(thisContainer)
    }

}