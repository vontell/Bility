package org.vontech.androidserver.pipeline

import org.vontech.androidserver.drivers.android.AndroidPipeline
import org.vontech.androidserver.logger
import org.vontech.androidserver.projectSaveLocation
import org.vontech.androidserver.utils.CommandRunner
import java.io.File

class PipelineRunner(private val pipelineConfig: PipelineConfig) {

    /** The pipeline that we are running **/
    private lateinit var pipeline: Pipeline

    init {
        if (pipelineConfig.type == ProjectType.ANDROID) {
            this.pipeline = AndroidPipeline(pipelineConfig)
        }
    }

    private fun downloadProject(): PipelineStepResult {

        when (pipelineConfig.source.sourceType) {
            ProjectSourceType.GIT -> this.cloneProject()
            ProjectSourceType.LOCALZIP -> this.unzipProject()
        }

        // Return success if download / extraction was a success
        val saveLoc = File(projectSaveLocation)
        return PipelineStepResult("DOWNLOAD", saveLoc.isDirectory && saveLoc.list().isNotEmpty())

    }

    fun startRunner() {

        logger?.info(this.pipelineConfig.toString())
        logger?.info("STARTED RUNNER")

        // 1) First, download the project, if required
        val downloadStep = this.downloadProject()
        logger?.info(downloadStep.toString())

        // 2) Setup the project with any required dependencies
        val setupStep = this.pipeline.setupProject()
        logger?.info(setupStep.toString())

        // Last) Do any required cleanup
        val teardownStep = this.pipeline.teardownProject()
        logger?.info(teardownStep.toString())

    }


    // Helper functions --------------------------------------------

    private fun unzipProject() {
        CommandRunner.unzip(pipelineConfig.source.source, pipelineConfig.source.location)
        File(pipelineConfig.source.source).delete()
    }

    private fun cloneProject() {
        CommandRunner.gitClone(pipelineConfig.source.source, pipelineConfig.source.location)
    }

}