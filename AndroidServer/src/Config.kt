package org.vontech.androidserver

import org.slf4j.Logger
import org.vontech.androidserver.drivers.android.AndroidSession
import org.vontech.core.types.AndroidAppTestConfig
import org.vontech.core.types.ContainerInfo

const val databaseName: String = "bility"
const val databaseHost: String = "localhost"
const val databasePort: Int = 27017

const val projectSaveLocation: String = "projectupload/project/"

val thisContainer: ContainerInfo = ContainerInfo("0.0.1")

var logger: Logger? = null

// TODO: Remove the default config
//var testConfig: AndroidAppTestConfig? = AndroidAppTestConfig("org.vontech.internalbilitytester")
var testConfig: AndroidAppTestConfig? = AndroidAppTestConfig("com.danielkim.soundrecorder")


// TODO: INSTEAD OF GLOBAL VARIABLES, CREATE A CONTEXT
var androidSession: AndroidSession? = null
var latestGraph: String? = null