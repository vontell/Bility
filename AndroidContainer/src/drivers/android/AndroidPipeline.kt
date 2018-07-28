package org.vontech.androidserver.drivers.android

import org.vontech.androidserver.logger
import org.vontech.androidserver.pipeline.Pipeline
import org.vontech.androidserver.pipeline.PipelineConfig
import org.vontech.androidserver.pipeline.PipelineStepResult
import org.vontech.androidserver.utils.Gradle
import java.io.File

class AndroidPipeline(override var pipelineConfig: PipelineConfig) : Pipeline {

    private lateinit var gradle: Gradle
    private lateinit var driver: AndroidDriver

    /**
     * This method does all setup required to run our tools on the
     * given application. This involves the following steps:
     * 1) Remove any existing local.properties that exists in the project
     * 2) Making sure that the project builds
     */
    override fun setupProject(): PipelineStepResult {

        // First, get references to Gradle and the Android Driver
        gradle = Gradle(this.pipelineConfig.source)
        driver = AndroidDriver()

        // Next, start the ADB server for connecting to and managing devices
        driver.startADBServer()

        // Start the emulator, if not already started
        if (!driver.isEmulatorReady()) {
            // TODO: Start with LOGCAT options
            logger?.info("Emulator was not started; starting up now")
            driver.startEmulator("Nexus_5X_API_26_x86")
            driver.waitForEmulatorOnline()
        }
        logger?.info("Emulator status: ${driver.isEmulatorReady()}")
        logger?.info("Emulators currently running: ${driver.getRunningEmulatorNames()}")

        // Reset the emulator (just in case), and send to home
        driver.wipeEmulator()
        Thread.sleep(1000)
        driver.emulatorGoHome()
        Thread.sleep(2000)

        // Now, start setting up the project files.
        // 1) Use correct Gradle files
        // 2) Inject Bility dependencies and test files
        // 3) Inject permissions
        gradle.replaceGradleWrapperProperties()
        gradle.injectBilityTester(pipelineConfig.source.appModule)
        val projectDirectory = pipelineConfig.source.location + pipelineConfig.source.entryFolder
        driver.addPermissionIfMissing(projectDirectory, pipelineConfig.source.appModule, AndroidDriver.PERMISSION_INTERNET)

        // Build and install the application
        gradle.installDebug(true)

        // Grant permissions
        // TODO: Allow the user to provide a list of permissions to grant
        driver.grantEmulatorPermission(pipelineConfig.source.packageName, AndroidDriver.PERMISSION_SYSTEM_ALERT_WINDOW)

        driver.emulatorGoHome()
        Thread.sleep(2000)

        // FINALLY, RUN TESTS!!! This is where the magic happens!
        gradle.runConnectedTest("*.BilityTest", pipelineConfig.source.appModule, "connectedDebugAndroidTest")

        return PipelineStepResult("SETUP", true)
    }

    override fun teardownProject(): PipelineStepResult {

        // First kill all emulators currently running
        driver.killAllEmulators()

        // Then kill the ADB server
        driver.killADBServer()

        // Then delete the project
        val dirToDelete = File(pipelineConfig.source.location)
        dirToDelete.deleteRecursively()
        //File(dirToDelete).deleteOnExit()

        return PipelineStepResult("TEARDOWN", !dirToDelete.exists())

    }


}