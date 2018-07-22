package org.vontech.androidserver.pipeline

enum class ProjectSourceType {
    GIT, LOCALZIP
}

data class Project(val sourceType: ProjectSourceType,
                   val source: String,
                   val name: String,
                   val entryFolder: String,
                   val location: String)