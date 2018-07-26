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
            this.gradlePath = "./" + project!!.location + project!!.entryFolder + "gradlew "
        } else {
            gradlePath = "gradle "
        }
    }

    fun replaceGradleWrapperProperties() {

        val wrapperPropsFile = File(this.project!!.location + this.project!!.entryFolder + "gradle/wrapper/gradle-wrapper.properties")
        val correctPropsFile = File("defaultfiles/gradle-wrapper.properties")

        println(wrapperPropsFile.absolutePath)
        wrapperPropsFile.delete()
        wrapperPropsFile.createNewFile()
        CommandRunner.copyFileUsingStream(correctPropsFile, wrapperPropsFile)

        logger?.info("REPLACED GRADLE WRAPPER PROPERTIES")

    }

    fun build() {
        logger?.info("STARTING GRADLE BUILD")
        val executionString = this.gradlePath + "build -b " + this.project!!.location + this.project!!.entryFolder + "build.gradle --debug --stacktrace"
        println(executionString)
        CommandRunner.executeCommand(executionString, true)
        logger?.info("FINISHED GRADLE BUILD")
    }

    fun installDebug(showInfo: Boolean) {
        logger?.info("STARTING GRADLE INSTALL DEBUG")
        var executionString = this.gradlePath + "installDebug -b " + this.project!!.location + this.project!!.entryFolder + "build.gradle"
        if (showInfo) {
            executionString += " --info"
        }
        CommandRunner.executeCommand(executionString, true)
        logger?.info("FINISHED GRADLE INSTALL DEBUG")
    }

    fun connectedDebugAndroidTest(testsRegex: String) {
        // This post is pretty much the only resource on this:
        // https://stackoverflow.com/questions/22505533/how-to-run-only-one-test-class-on-gradle
        val module = "app"
        var executionString = "${this.gradlePath} -D:$module:test.single=\"$testsRegex\" :$module:connectedDebugAndroidTest -b ${this.project!!.location + this.project!!.entryFolder}build.gradle"
        //var executionString = this.gradlePath + "app:connectedDebugAndroidTest -b " + this.project!!.location + this.project!!.entryFolder + "build.gradle"
        logger?.info(executionString)
        CommandRunner.executeCommand(executionString, true)
    }

}