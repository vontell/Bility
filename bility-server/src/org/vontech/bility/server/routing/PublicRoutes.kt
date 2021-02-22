package org.vontech.bility.server.routing

import io.ktor.application.*
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.content.*
import io.ktor.network.util.ioCoroutineDispatcher
import io.ktor.request.receiveMultipart
import io.ktor.request.receiveParameters
import io.ktor.response.respond

import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.html.body
import kotlinx.html.h1
import org.vontech.bility.server.pipeline.*
import org.vontech.bility.server.projectSaveLocation
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
            var projPath = ""
            var packageName = ""
            var appModule = ""
            var zipFile: File? = null
            multipart.forEachPart { part ->
                if (part is PartData.FormItem) {
                    if (part.name == "projectName") {
                        title = part.value
                    }
                    if (part.name == "packageName") {
                        packageName = part.value
                    }
                    if (part.name == "projectPath") {
                        projPath = part.value
                    }
                    if (part.name == "appModule") {
                        appModule = part.value
                    }
                } else if (part is PartData.FileItem) {
                    val ext = File(part.originalFileName).extension
                    val file = File(
                            projectSaveLocation,
                            "upload.$ext"
                    )

                    // Create the file and directory if it does not already exist
                    if (!file.parentFile.exists())
                        file.parentFile.mkdirs()
                    if (!file.exists())
                        file.createNewFile()

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

            val project = Project(ProjectSourceType.LOCALZIP, zipFile!!.absolutePath, title, packageName, appModule, projPath, projectSaveLocation)
            var projectConfig = PipelineConfig(project)
            projectConfig = generateMissingConfig(projectConfig)

            val runner = PipelineRunner(projectConfig)
            runner.startRunner()

        }

    }

    route("/gitDownload") {
        post {

            val parameters = call.receiveParameters()
            val name = parameters["projectName"]
            val projPath = parameters["projectPath"]
            val git = parameters["gitPath"]
            val packageName = parameters["packageName"]
            val appModule = parameters["appModule"]

            call.respondHtml {
                body {
                    h1 { +"Upload complete!!"}
                }
            }

            val project = Project(ProjectSourceType.GIT, git!!,  name!!, packageName!!, appModule!!, projPath!!, projectSaveLocation)
            var projectConfig = PipelineConfig(project)
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
        dispatcher: CoroutineDispatcher = Dispatchers.IO
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