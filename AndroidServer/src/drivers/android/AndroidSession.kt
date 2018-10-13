package org.vontech.androidserver.drivers.android

import org.vontech.algorithms.hci.getAccessibilityStateFromLiteralInterface
import org.vontech.algorithms.personas.Monkey
import org.vontech.algorithms.personas.Person
import org.vontech.algorithms.rulebased.loggers.WCAG2IssuerLogger
import org.vontech.algorithms.rulebased.loggers.WCAGLevel
import org.vontech.androidserver.logger
import org.vontech.core.interaction.InputInteractionType
import org.vontech.core.interaction.UserAction
import org.vontech.core.interfaces.LiteralInterace
import org.vontech.core.interfaces.emptyPerceptifer
import org.vontech.core.server.StartupEvent
import java.util.*

class AndroidSession(val startEvent: StartupEvent) {

    var person: Person = Monkey("Bobby", Random(2018))
    var collectedInterfaces: MutableList<LiteralInterace> = ArrayList()

    var actionToTake: UserAction = UserAction(InputInteractionType.NONE, emptyPerceptifer())
    var wcagLogger: WCAG2IssuerLogger = WCAG2IssuerLogger(WCAGLevel.A)

    fun giveNewLiteralInterface(literalInterace: LiteralInterace) {

        collectedInterfaces.add(literalInterace)
        person.updateInternalKnowledge(literalInterace)

    }

    /**
     * Decides on the next action to generate based on all
     * known information, and also processes this
     */
    fun generateAndSaveNextAction() {

        val latest = collectedInterfaces.last()

        // First, log issues
        wcagLogger.logStaticIssues(latest)
        if (wcagLogger.issueList.isNotEmpty()) {
            logger?.info("LIST OF WCAG 2.0 ISSUES ----------------")
            var passCount = 0
            for (issue in wcagLogger.issueList) {
                if (issue.passes) {
                    passCount++
                }
                else {
                    logger?.info("\t --- Percept Information:")
                    for (percept in issue.perceptifer.percepts!!) {
                        logger?.info("\t\t(R) $percept")
                    }
                    for (percept in issue.perceptifer.virtualPercepts!!) {
                        logger?.info("\t\t(V) $percept")
                    }
                    logger?.info("\t Issue Information:")
                    logger?.info("\t\t Identifier: ${issue.identifier}")
                    logger?.info("\t\t Description: ${issue.shortDescription}")
                    logger?.info("\t\t Explanation: ${issue.instanceExplanation}")
                    logger?.info("\t\t Suggestion: ${issue.suggestionExplanation}")
                    logger?.info("\t\t WCAG Details: ${issue.extras}")
                }
            }
            logger?.info("-------------------------------------- (note that $passCount pass events were found) ")
        }
        wcagLogger.clear()

        logger?.info("LATEST INTERFACE INFORMATION ---------")
        logger?.info("Metadata: ${latest.metadata}")
        var count = 1
        for (perceptifer in latest.perceptifers) {
            logger?.info("Perceptifer $count (${perceptifer.id}):")
            for (percept in perceptifer.percepts!!) {
                logger?.info("\t(R) $percept")
            }
            for (percept in perceptifer.virtualPercepts!!) {
                logger?.info("\t(V) $percept")
            }
            count++
        }
        logger?.info("--------------------------------------")
        logger?.info("DECIDING NEXT ACTION...")
        val nextAction = person.reactToNewUserInterface(latest)
        logger?.info("DECIDED ON ${nextAction.type} on ${nextAction.perceptifer}")

        actionToTake = nextAction

        getAccessibilityStateFromLiteralInterface(latest)

    }

    fun getNextAction(): UserAction {
        val popped = actionToTake.copy()
        actionToTake = UserAction(InputInteractionType.NONE, emptyPerceptifer())
        return popped
    }

    fun compileResults() {

    }

}