package org.vontech.androidserver.routing

import io.ktor.application.*
import io.ktor.content.PartData
import io.ktor.content.forEachPart
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.network.util.ioCoroutineDispatcher
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.request.receiveParameters
import io.ktor.response.respond

import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.yield
import kotlinx.html.body
import kotlinx.html.h1
import org.vontech.androidserver.pipeline.PipelineConfig
import org.vontech.androidserver.pipeline.PipelineRunner
import org.vontech.androidserver.pipeline.ProjectSourceType
import org.vontech.androidserver.pipeline.generateMissingConfig
import org.vontech.androidserver.utils.CommandRunner
import java.io.File
import java.io.InputStream
import java.io.OutputStream


fun Route.publicRoutes() {

    route("/upload") {

        get {
            call.respond(FreeMarkerContent("uploadForm.ftl", null, "e"))
        }

    }

    route("/uploadZip") {

        post {
            val multipart = call.receiveMultipart()
            var title = ""
            var zipFile: File? = null
            multipart.forEachPart { part ->
                if (part is PartData.FormItem) {
                    if (part.name == "projectName") {
                        title = part.value
                    }
                } else if (part is PartData.FileItem) {
                    val ext = File(part.originalFileName).extension
                    val file = File(
                            "projectuploads",
                            "upload-${System.currentTimeMillis()}-${title.hashCode()}.$ext"
                    )

                    part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
                    zipFile = file
                }

                part.dispose()
            }
            call.respondHtml {
                body {
                    h1 { +"Upload complete!!"}
                }
            }

        }

    }

    route("/gitDownload") {
        post {

            val parameters = call.receiveParameters()
            val name = parameters["projectName"]
            val git = parameters["gitPath"]

            val commandResult = CommandRunner.gitClone(git!!, "projectuploads/project/")

            call.respondHtml {
                body {
                    h1 { +"Upload complete!!"}
                }
            }

            var projectConfig = PipelineConfig("projectuploads/project/", ProjectSourceType.GIT)
            projectConfig = generateMissingConfig(projectConfig)

            val runner = PipelineRunner(projectConfig)
            runner.startRunner()

        }
    }

}

suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 4 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = ioCoroutineDispatcher
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}