package org.vontech.androidserver

import java.util.*

data class ContainerInfo(val version: String)

data class AndroidAppTestConfig (
        val packageName: String,
        val timeout: Int = 3000,
        val numRuns: Int = 3,
        val seed: Int = Random().nextInt(),
        val maxActions: Int = 14,
        val id: Long = Random().nextLong()
)