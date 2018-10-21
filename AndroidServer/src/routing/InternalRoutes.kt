package org.vontech.androidserver.routing

import io.ktor.application.call
import io.ktor.content.PartData
import io.ktor.content.forEachPart
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.vontech.algorithms.personas.Monkey
import org.vontech.androidserver.androidSession
import org.vontech.androidserver.drivers.android.AndroidSession
import org.vontech.androidserver.latestGraph
import org.vontech.androidserver.logger
import org.vontech.androidserver.testConfig
import org.vontech.core.interfaces.LiteralInterace
import org.vontech.core.server.StartupEvent
import org.vontech.utils.cast
import java.io.File

val FILE_DB = "/Users/vontell/Documents/BilityBuildSystem/AndroidServer/fileDB"

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
            androidSession = AndroidSession(event)
            call.respond(200)
        }
    }

    route("/receiveInterface") {
        post {
            val face: LiteralInterace = call.receive()
            logger?.info("METADATA: ${face.metadata}")
            logger?.info("PERCEPTIFERS: ${face.perceptifers}")
            androidSession!!.giveNewLiteralInterface(face)
            androidSession!!.generateAndSaveNextAction()

            latestGraph = (androidSession!!.person as Monkey).automaton.getStringForGraphVizWeb()

            call.respond(200)
        }
    }

    route("/results") {
        get {
            call.respond(FreeMarkerContent("results.ftl", null))
        }
    }

    route("/popGraphviz") {
        get {
            if (latestGraph != null) {
                call.respond(latestGraph!!)
                latestGraph = null
            } else {
                call.respond(200)
            }

        }
    }

    route("/receiveScreenshot") {
        post {
            val multipart = call.receiveMultipart()
            var literalId = "unknown"
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "literalId") {
                            literalId = part.value
                        }
                        println(part)
                    }
                    is PartData.FileItem -> {
                        val ext = File(part.originalFileName).extension
                        val file = File(FILE_DB, "upload-$literalId.$ext")
                        file.parentFile.mkdirs()
                        file.createNewFile()
                        part.streamProvider().use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }
                        androidSession!!.giveNewScreenshot(file)
                    }
                }

                part.dispose()
            }
        }
    }

    route("/getNextAction") {
        get {
            logger?.info("GETTING A NEW ACTION")
            call.respond(androidSession!!.getNextAction())
        }
    }

}