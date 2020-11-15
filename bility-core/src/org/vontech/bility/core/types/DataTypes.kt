package org.vontech.bility.core.types

import java.lang.Math.abs
import java.util.*

data class AndroidAppTestConfig (
        val packageName: String,
        val timeout: Int = 3000,
        val numRuns: Int = 3,
        val seed: Int = Random().nextInt(),
        val maxActions: Int = 14,
        val id: Long = abs(Random().nextLong())
)

data class ContainerInfo(val version: String)