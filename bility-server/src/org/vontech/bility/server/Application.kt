package org.vontech.bility.server

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
import io.ktor.http.content.*
import io.ktor.routing.*
import org.vontech.bility.server.routing.internalRoutes
import org.vontech.bility.server.routing.publicRoutes
import org.vontech.bility.server.routing.surfacedRoutes
import org.vontech.bility.core.constants.FILE_DB
import java.io.File


/**
 *  Welcome to the Bility Web Server!
 *
 *  This server handles all communication from Bility tests and the frontend
 *
 *  - RESTful API for the following features
 *      - Getting Bility Server info
 *      - Uploading and storing a project zip file or git repo
 *      - Receiving test events from a device (Android, iOS, Web, etc...)
 *      - GETs for test results (in real time)
 *  - Computation features
 *      - Unzipping project files
 *      - Downloading project files over git
 *      - Starting builds and specific Bility tests for project
 *      - Running AI on test events
 *          - i.e. receiving a request with view information and path to screenshot,
 *            and then doing OCR and computations using AIs
 *      - Downloading data from the database (such as configs or past runs)
 *      - Uploading test results and other info to database
 *
 */

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(CORS) // TODO: Frontend will be with the backend later
    {
        anyHost()
    }
    install(ContentNegotiation) {
//        jackson {
//            enable(SerializationFeature.INDENT_OUTPUT)
//        }
        gson {
            setPrettyPrinting()
        }
    }
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(Application::class.java.classLoader, "templates")
    }

    logger = log

    routing {
        route("/") {
            surfacedRoutes()
        }
        route("/api") {
            publicRoutes()
        }
        route("/internal") {
            internalRoutes()
        }
//        static("/static") {
//            resources("static")
//        }
        static("/screens") {
            staticRootFolder = File(FILE_DB)
            files("screens")
        }
    }

    log.info(thisContainer.toString())

}