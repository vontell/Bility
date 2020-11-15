package org.vontech.bility.server.drivers.android

import org.vontech.bility.core.algorithms.personas.Monkey
import org.vontech.bility.core.algorithms.personas.Person
import org.vontech.bility.core.algorithms.rulebased.loggers.WCAG2IssuerLogger
import org.vontech.bility.core.algorithms.rulebased.loggers.WCAGLevel
import org.vontech.bility.server.logger
import org.vontech.bility.core.interaction.InputInteractionType
import org.vontech.bility.core.interaction.UserAction
import org.vontech.bility.core.interfaces.LiteralInterace
import org.vontech.bility.core.interfaces.emptyPerceptifer
import org.vontech.bility.core.server.StartupEvent
import java.io.File
import java.util.*

class AndroidSession(val startEvent: StartupEvent) {

    var person: Person = Monkey("Bobby", Random(2018))

    var collectedInterfaces: MutableList<LiteralInterace> = ArrayList()

    var actionToTake: UserAction = UserAction(InputInteractionType.NONE, emptyPerceptifer())

    val log: SessionLogger = SessionLogger()

    fun giveNewLiteralInterface(literalInterace: LiteralInterace) {

        collectedInterfaces.add(literalInterace)
        person.updateInternalKnowledge(literalInterace)

    }

    fun giveNewScreenshot(screenshot: File) {
        //println("We have the screenshot")
    }

    /**
     * Decides on the next action to generate based on all
     * known information, and also processes this
     */
    fun generateAndSaveNextAction() {

        val latest = collectedInterfaces.last()

        val nextAction = person.reactToNewUserInterface(latest)
        logger?.info("DECIDED ON $nextAction")

        actionToTake = nextAction

    }

    fun getNextAction(): UserAction {
        val popped = actionToTake
        actionToTake = UserAction(InputInteractionType.NONE, emptyPerceptifer())
        return popped
    }

    fun compileResults() {

    }

}

data class SessionLog(
    val type: String,
    val message: String,
    val date: Date
)

class SessionLogger {

    var logs: MutableList<SessionLog> = mutableListOf()

    fun log(message: String) {
        logs.add(SessionLog("NONE", message, Date()))
    }

    fun log(type: String, message: String) {
        logs.add(SessionLog(type, message, Date()))
    }

}