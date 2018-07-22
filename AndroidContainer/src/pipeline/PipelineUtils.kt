package org.vontech.androidserver.pipeline

enum class ProjectSourceType {
    GIT, LOCALZIP
}

data class ProjectSource(val sourceType: ProjectSourceType, val location: String)