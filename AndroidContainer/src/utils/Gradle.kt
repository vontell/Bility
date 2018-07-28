package org.vontech.androidserver.utils

import org.vontech.androidserver.logger
import org.vontech.androidserver.pipeline.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

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

    fun runConnectedTest(testsRegex: String, module: String, flavor: String) {
        // This post is pretty much the only resource on this:
        // https://stackoverflow.com/questions/22505533/how-to-run-only-one-test-class-on-gradle
        var executionString = "${this.gradlePath} -D:$module:test.single=\"$testsRegex\" :$module:$flavor -b ${this.project!!.location + this.project!!.entryFolder}build.gradle"
        //var executionString = this.gradlePath + "app:connectedDebugAndroidTest -b " + this.project!!.location + this.project!!.entryFolder + "build.gradle"
        CommandRunner.executeCommand(executionString, true)
    }

    // https://www.mkyong.com/gradle/gradle-display-project-dependency/
    private fun getDependenciesResult(module: String, config: String): String {
        val executionString = "${this.gradlePath} -q $module:dependencies -b ${this.project!!.location + this.project!!.entryFolder}build.gradle --configuration $config"
        return CommandRunner.executeCommand(executionString)
    }

    fun hasDependency(dependency: String, module: String, config: String): Boolean {
        return getDependenciesResult(module, config).contains(" $dependency")
    }

    fun injectBilityTester(module: String) {

        // Inject all required dependencies for the project
        val repoDeclaration = """
            repositories {
                maven {
                    credentials {
                        username 'admin'
                        password 'password'
                    }
                    url 'http://localhost:8146/artifactory/libs-release-local'
                }
            }
        """.trimIndent()
        val buildscriptDeclaration = "buildscript {\n${repoDeclaration.prependIndent("    ")}\n}"
        val allprojectsDeclaration = "allprojects {\n${repoDeclaration.prependIndent("    ")}\n}"
        val finalToWriteTopLevel = "\n\n$buildscriptDeclaration\n\n$allprojectsDeclaration\n"
        val finalToWriteModuleLevel = "dependencies {\n    // The custom lib!\n    androidTestImplementation(group: 'org.vontech', name: 'bilitytester', version: '1.0.0', ext: 'aar')\n}"

        // 1) Inject maven repo info into top-level build.gradle
        val topLevelGradle = this.project!!.location + this.project!!.entryFolder + "build.gradle"
        Files.write(Paths.get(topLevelGradle), finalToWriteTopLevel.toByteArray(), StandardOpenOption.APPEND)

        // 2) Inject library dependency into module-to-test build.gradle
        val moduleLevelGradle = this.project!!.location + this.project!!.entryFolder + module + "/build.gradle"
        Files.write(Paths.get(moduleLevelGradle), finalToWriteModuleLevel.toByteArray(), StandardOpenOption.APPEND)

        // 3) Copy test file into androidTest folder of module
        val androidTestTopFolder = this.project!!.location + this.project!!.entryFolder +  module + "/src/androidTest/java"
        val testPackage = "/internalbilitytester"
        val newTestFile = File("$androidTestTopFolder$testPackage/BilityTest.java")
        val toCopyTestFile = File("defaultfiles/BilityTest.java")

        newTestFile.parentFile.mkdirs()
        newTestFile.delete()
        newTestFile.createNewFile()
        CommandRunner.copyFileUsingStream(toCopyTestFile, newTestFile)

        logger?.info("INJECTED TEST DEPENDENCIES")


    }

}