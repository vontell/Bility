package org.vontech.androidserver

import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
//import io.ktor.jackson.jackson
import io.ktor.routing.*
import org.vontech.androidserver.routing.internalRoutes
import org.vontech.androidserver.routing.publicRoutes
import org.vontech.androidserver.routing.surfacedRoutes


/**
 *  Welcome to the Android Container Web Server!
 *
 *  This server handles all communication from the Android container to any
 *  other services (excluding the TCP port sending Minicap info). The server
 *  provides the following functionality
 *
 *  - RESTful API for the following features
 *      - Getting Android Container info (available SDKs, start information, etc)
 *      - Uploading and storing a project zip file or git repo
 *      - Receiving Android test events from the emulator (over rest)
 *      - GETs for test results (in real time)
 *  - Computation features
 *      - Unzipping project files
 *      - Downloading project files over git
 *      - Modifying project settings to handle:
 *          - Test dependencies
 *          - Android permissions (i.e. INTERNET)
 *          - Properties for connecting to the SDK
 *      - Receiving Android test events
 *      - Starting build and specific Bility tests for project
 *      - Running AI on Android test events
 *          - i.e. receiving a request with view information and path to screenshot,
 *            and then doing OCR and computations using AIs
 *      - Downloading data from the database (such as configs or past runs)
 *      - Uploading test results and other info to database
 *
 * The package setup to handle these components is as follows:
 *  org.vontech.androidserver
 *      ai - All artificial intelligence - related code
 *      drivers - Code for interacting with specific frameworks (i.e. Android, React Native, Cordova, etc)
 *          android - Code for interacting with Android SDK, build system, etc
 *      pipeline - Code for handling the pipeline of starting projects
 *      routing - All HTTP/WebSocket-specific code
 *          utils - General utils for HTTP-related stuff (i.e. take in a zip file)
 *      services - Code for dealing with outside services
 *          database - Code for interacting with the external database
 *          minicap - Any minicap-related code
 *      utils - General utils for use across the project
 *          commands - A collection of classes for running commands on the host system (i.e. Git)
 *          container - Any classes related to the container (info, shutting down, etc)
 *          files - File manipulation
 *
 */

fun main(args: Array<String>): Unit = io.ktor.server.netty.DevelopmentEngine.main(args)

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
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
    }

    log.info(thisContainer.toString())

}