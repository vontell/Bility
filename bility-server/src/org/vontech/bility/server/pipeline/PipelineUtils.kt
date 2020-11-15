package org.vontech.bility.server.pipeline

enum class ProjectSourceType {
    GIT, LOCALZIP
}

data class Project(val sourceType: ProjectSourceType,
                   val source: String,
                   val name: String,
                   val packageName: String,
                   val appModule: String,
                   val entryFolder: String,
                   val location: String)