package org.vontech.androidserver.pipeline

enum class ProjectType {
    ANDROID
}

data class PipelineConfig(val directoryLocation: String,
                          val projectSourceType: ProjectSourceType,
                          val projectSourceLocation: String? = null,
                          val type: ProjectType? = null)

fun generateMissingConfig(config: PipelineConfig): PipelineConfig {

    return config.copy(type = ProjectType.ANDROID)

}