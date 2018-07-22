package org.vontech.androidserver.utils

import org.vontech.androidserver.logger
import org.vontech.androidserver.pipeline.Project
import java.io.File

/**
 * A class which provides functions for interacting with gradle easily
 * @author Aaron Vontell
 */
class Gradle(var project: Project? = null) {

    private var gradlePath: String

    init {
        if (project != null) {
            this.gradlePath = "./" + project!!.location + "gradlew "
        } else {
            gradlePath = "gradle "
        }
    }

    fun replaceGradleWrapperProperties() {

        val wrapperPropsFile = File(this.project!!.location + "gradle/wrapper/gradle-wrapper.properties")
        val correctPropsFile = File("defaultfiles/gradle-wrapper.properties")

        wrapperPropsFile.delete()
        wrapperPropsFile.createNewFile()
        CommandRunner.copyFileUsingStream(correctPropsFile, wrapperPropsFile)

    }

    fun build() {
        logger?.info("STARTING GRADLE BUILD")
        val executionString = this.gradlePath + "build -b " + this.project!!.location + "build.gradle --debug --stacktrace"
        println(executionString)
        println(CommandRunner.executeCommand(executionString, true))
        logger?.info("GRADLE BUILD FINISHED")
    }

}