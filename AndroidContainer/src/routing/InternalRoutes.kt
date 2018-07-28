package org.vontech.androidserver.routing

import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.vontech.androidserver.logger

fun Route.internalRoutes() {

    route("/test") {
        get {
            logger?.info("WE CALLED IT")
        }
    }

}