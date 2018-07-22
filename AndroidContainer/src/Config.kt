package org.vontech.androidserver

import org.slf4j.Logger

const val databaseName: String = "bility"
const val databaseHost: String = "localhost"
const val databasePort: Int = 27017

const val projectSaveLocation: String = "projectupload/project/"

val thisContainer: ContainerInfo = ContainerInfo("0.0.1")

var logger: Logger? = null