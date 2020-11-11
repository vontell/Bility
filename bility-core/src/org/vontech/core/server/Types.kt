package org.vontech.core.server

//import org.vontech.core.interaction.InputEvent
//import org.vontech.core.interaction.OutputEvent
import org.vontech.core.types.AndroidAppTestConfig
import java.util.*

//data class DeviceEvent (
//    val id: Long = Random().nextLong(),
//    val inputEvent: InputEvent?,
//    val outputEventL: OutputEvent?
//)

data class StartupEvent(
    val id: Long = Random().nextLong(),
    val config: AndroidAppTestConfig,
    val timestamp: String
)