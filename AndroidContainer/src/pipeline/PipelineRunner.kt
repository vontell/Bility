package org.vontech.androidserver.pipeline

import org.vontech.androidserver.drivers.android.AndroidPipeline

class PipelineRunner(val pipelineConfig: PipelineConfig) {

    private lateinit var pipeline: AndroidPipeline

    init {
        if (pipelineConfig.type == ProjectType.ANDROID) {
            this.pipeline = AndroidPipeline()
        }
    }

    fun startRunner() {
        println("STARTED RUNNER")
        println(this.pipelineConfig)
    }

}