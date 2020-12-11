package org.vontech.bility.server.pipeline

/**
 * A pipeline object is used by the PipelineRunner to download and build projects.
 * @author Aaron Vontell
 */

data class PipelineStepResult(val event: String, val success: Boolean)

interface Pipeline {

    var pipelineConfig: PipelineConfig

    /**
     * Step 1.
     * Given a configuration, make any appropriate changes to the
     * project in preparation for letting the driver interact with
     * this project.
     */
    fun setupProject(): PipelineStepResult

    /**
     * Step 2.
     * After all results have been saved, processed, etc... Cleanup / delete
     * any local files and state that are part of this pipeline run.
     */
    fun teardownProject(): PipelineStepResult

}