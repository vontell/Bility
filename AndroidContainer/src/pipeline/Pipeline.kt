package org.vontech.androidserver.pipeline

/**
 * A pipeline object is used by the PipelineRunner to download and build projects.
 * @author Aaron Vontell
 */
interface Pipeline {

    /**
     * Step 1.
     * Given information about the source of the project, make sure
     * that the project is within the right location.
     * @param projectSource Information on the project location
     */
    fun downloadProject(projectSource: ProjectSource)

    /**
     * Step 2.
     * Given a configuration, make any appropriate changes to the
     * project in preparation for letting the driver interact with
     * this project.
     */
    fun setupProject(config: PipelineConfig)

    fun teardownProject()

}