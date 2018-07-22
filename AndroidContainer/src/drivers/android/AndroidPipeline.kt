package org.vontech.androidserver.drivers.android

import org.vontech.androidserver.pipeline.Pipeline
import org.vontech.androidserver.pipeline.PipelineConfig
import org.vontech.androidserver.pipeline.PipelineStepResult
import org.vontech.androidserver.utils.Gradle
import java.io.File

class AndroidPipeline(override var pipelineConfig: PipelineConfig) : Pipeline {

    lateinit var gradle: Gradle

    /**
     * This method does all setup required to run our tools on the
     * given application. This involves the following steps:
     * 1) Remove any existing local.properties that exists in the project
     * 2) Making sure that the project builds
     */
    override fun setupProject(): PipelineStepResult {
        gradle = Gradle(this.pipelineConfig.source)

        gradle.replaceGradleWrapperProperties()
        gradle.build()

        return PipelineStepResult("SETUP", true)
    }

    override fun teardownProject(): PipelineStepResult {

        val dirToDelete = File(pipelineConfig.source.location)
        //dirToDelete.deleteRecursively()
        //File(dirToDelete).deleteOnExit()

        return PipelineStepResult("TEARDOWN", !dirToDelete.exists())

    }


}