package org.vontech.bility.server.routing

import io.ktor.application.call
import io.ktor.content.*
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.vontech.bility.core.algorithms.personas.Monkey
import org.vontech.bility.server.androidSession
import org.vontech.bility.server.drivers.android.AndroidSession
import org.vontech.bility.server.latestGraph
import org.vontech.bility.server.logger
import org.vontech.bility.server.testConfig
import org.vontech.bility.core.constants.FILE_DB
import org.vontech.bility.core.interfaces.LiteralInterace
import org.vontech.bility.core.server.StartupEvent
import org.vontech.bility.core.utils.cast
import java.io.File

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
            //logger?.info("METADATA: ${face.metadata}")
            //logger?.info("PERCEPTIFERS: ${face.perceptifers}")
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
            var size = "SMALL"
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "literalId") {
                            literalId = part.value
                        }
                        if (part.name == "sizeTag") {
                            size = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        val ext = File(part.originalFileName).extension
                        val file = if (size == "SMALL") {
                            File("$FILE_DB/screens", "upload-$literalId.$ext")
                        } else {
                            File("$FILE_DB/screens", "hires-$literalId.$ext")
                        }
                        logger?.info("$FILE_DB/screens")
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

    route("/getFrontendReport") {
        get {
            if (androidSession != null) {
                val automaton = (androidSession?.person as Monkey).automaton.getStringForGraphVizWeb()
                val lastAction = (androidSession?.person as Monkey).lastActionTaken
                val numUnexplored = (androidSession?.person as Monkey).automaton.statesWithUnexploredEdges().size
                val issues = (androidSession?.person as Monkey).askAboutCurrentIssues()
                val toReport = FrontendReportInfo(automaton, lastAction, numUnexplored, issues)
                println("REPORT HAS ${toReport.issueReport!!.dynamicIssues.size}")
                call.respond(toReport)
            } else {
                call.respond(FrontendReportInfo(null, null, null, null))
            }


        }
    }

}