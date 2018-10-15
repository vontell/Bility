package org.vontech.algorithms.personas

import org.vontech.algorithms.automatons.Automaton
import org.vontech.algorithms.automatons.AutomatonState
import org.vontech.algorithms.automatons.AutomatonTransition
import org.vontech.algorithms.rulebased.loggers.WCAG2IssuerLogger
import org.vontech.algorithms.rulebased.loggers.WCAGLevel
import org.vontech.core.interaction.InputInteractionType
import org.vontech.core.interaction.UserAction
import org.vontech.core.interaction.generateRandomSwipe
import org.vontech.core.interfaces.*
import org.vontech.utils.cast
import org.vontech.utils.random
import java.util.*

/**
 * A representation of a user which clicks randomly through a user interface
 * (which is commonly called a Monkey in user-interface language).
 * @author Aaron Vontell
 * @created August 12th, 2018
 * @updated August 12th, 2018
 */
class Monkey(nickname: String, rand: Random = Random()): Person(nickname, rand) {

    override val baseType: String = "Monkey"
    var actionCount: Int = 0

    private val CLICK_BOUND = 0.30
    private val SWIPE_BOUND = CLICK_BOUND + 0.60
    private val BACK_BOUND = SWIPE_BOUND + 0.0001
    private val LONGCLICK_BOUND = 1.0

    lateinit var automaton: Automaton<CondensedState, UserAction>
    var wcagLogger: WCAG2IssuerLogger = WCAG2IssuerLogger(WCAGLevel.A)


    override fun getDescription(): String {
        return "I am $nickname, a Monkey! I will randomly click through a user interface!"
    }

    /**
     * A monkey is somewhat smart in that it will keep track of its
     * dynamic state - in other words, this monkey has perfect memory
     */
    override fun updateInternalKnowledge(literalInterace: LiteralInterace) {

        // Generate a new fuzzy state
        val newState = CondensedState(literalInterace)

        // Update any memory based on this newState
        // ...

        // Add this state to the automation, based on the last decision made
        if (this.lastActionTaken != null) {
            automaton.addTransition(AutomatonTransition(this.lastActionTaken!!), AutomatonState(newState))
        } else {
            automaton = Automaton(AutomatonState(newState))
            automaton.setImageFunction {
                "/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-${it.state.literalInterace.metadata.id}.png"
            }
        }

    }

    override fun reactToNewUserInterface(literalInterace: LiteralInterace): UserAction {

        // The monkey interacts with the user interface with these probabilities
        // CLICK - 0.05
        // SWIPE - 0.70
        // BACK - 0.0001
        // NONE - REMAINING
        //
        // It will randomly pick one of these actions, and then randomly select
        // an item to perform that action on

        if (actionCount > 15) {
            automaton.writeDotFile()
            automaton.dotFileToPng()
            println("FINISHED WRITING DOT FILE")
            val action = UserAction(InputInteractionType.QUIT, emptyPerceptifer())
            lastActionTaken = action
            println("Generating accessibility report...")
            val result = wcagLogger.getFullAccessibilityReport(automaton)
            println(result)
            return action
        }

        // TODO: Organize elements in a way that makes searching easy
        println("Getting clickables")
        val clickable = literalInterace.perceptifers.filter {
            it.virtualPercepts!!.any {
                it.type == PerceptType.VIRTUALLY_CLICKABLE && it.information as Boolean } }
        println("Found ${clickable.size} clickable perceptifers")

        println("Getting swipeables")
        val swipeable = literalInterace.perceptifers.filter {
            it.percepts!!.any {
                it.type == PerceptType.SCROLL_PROGRESS } }
        println("Found ${swipeable.size} swipeable perceptifers")

        println("Get back button")
        val backButton = literalInterace.perceptifers.singleOrNull {
            it.percepts!!.any {
                it.type == PerceptType.PHYSICAL_BUTTON &&
                        it.information.cast<PhysicalButton>().name == "BACK" } }
        if (backButton != null) {println("Found 1 back button")}

        val decider = rand.nextFloat()

        val action = if (decider < CLICK_BOUND && clickable.isNotEmpty()) {
            actionCount++
            val subject = clickable.random(rand)!!
            UserAction(InputInteractionType.CLICK, subject, getMidpoint(subject))
        } else if (decider < SWIPE_BOUND && swipeable.isNotEmpty()) {
            actionCount++
            val subject = swipeable.random(rand)!!
            UserAction(InputInteractionType.SWIPE, subject, generateRandomSwipe(subject))
        } else if (decider < BACK_BOUND && backButton != null) {
            actionCount++
            UserAction(InputInteractionType.PHYSICAL_BUTTON, backButton)
        } else {
            UserAction(InputInteractionType.NONE, emptyPerceptifer())
        }
        lastActionTaken = action

        // Add some potential edges to the automaton
        if (clickable.isNotEmpty()) {
            val potentialAction = AutomatonTransition(UserAction(InputInteractionType.CLICK, emptyPerceptifer()))
            automaton.addTransition(potentialAction, null)
        }
        if (swipeable.isNotEmpty()) {
            val potentialAction = AutomatonTransition(UserAction(InputInteractionType.SWIPE, emptyPerceptifer()))
            automaton.addTransition(potentialAction, null)
        }

        return action

    }

}