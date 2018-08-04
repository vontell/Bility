package org.vontech.androidserver.pipeline

enum class ProjectType {
    ANDROID
}

data class PipelineConfig(val source: Project, val type: ProjectType? = null)

fun generateMissingConfig(config: PipelineConfig): PipelineConfig {
    return config.copy(type = ProjectType.ANDROID)
}