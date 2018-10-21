package org.vontech.algorithms.personas

import org.vontech.algorithms.automatons.Automaton
import org.vontech.algorithms.automatons.AutomatonState
import org.vontech.algorithms.automatons.AutomatonTransition
import org.vontech.algorithms.rulebased.loggers.WCAG2IssuerLogger
import org.vontech.algorithms.rulebased.loggers.WCAGLevel
import org.vontech.constants.FILE_DB
import org.vontech.core.interaction.InputInteractionType
import org.vontech.core.interaction.KeyPress
import org.vontech.core.interaction.UserAction
import org.vontech.core.interaction.generateRandomSwipe
import org.vontech.core.interfaces.*
import org.vontech.utils.cast
import org.vontech.utils.random
import java.util.*
import kotlin.collections.HashMap

/**
 * A representation of a user which clicks randomly through a user interface
 * (which is commonly called a Monkey in user-interface language).
 * NOTE: This is not a true monkey... if so, it's actually quite a smart monkey.
 * Instead of randomly clicking, she/he does so much more - swiping, using a
 * keyboard, only interacting with actual elements, and trying not to repeat
 * actions that it has already done.
 * @author Aaron Vontell
 * @created August 12th, 2018
 * @updated October 16th, 2018
 */
class Monkey(nickname: String, rand: Random = Random()): Person(nickname, rand) {

    override val baseType: String = "Monkey"
    var actionCount: Int = 0


    private val CLICK_BOUND = 0.60
    private val SWIPE_BOUND = CLICK_BOUND + 0.41
    private val BACK_BOUND = SWIPE_BOUND + 0.0001
    private val LONGCLICK_BOUND = 1.0

    lateinit var automaton: Automaton<CondensedState, UserAction>
    var wcagLogger: WCAG2IssuerLogger = WCAG2IssuerLogger(WCAGLevel.A)

    /**
     * A monkey employees the following internal states / goals.
     *  exploring - Simply navigating through the user interface, finding
     *              different states. Explore until all edges have been possibly
     *              satisfied.
     */
    var state = "exploring"


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
            //this.lastActionTaken!!.provideContext(automaton.currentState.state.hashResults)
            automaton.addTransition(AutomatonTransition(this.lastActionTaken!!), AutomatonState(newState))
        } else {
            automaton = Automaton(AutomatonState(newState))
            automaton.setImageFunction {
                "$FILE_DB/upload-${it.state.literalInterace.metadata.id}.png"
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

        if (actionCount > 200 ) {
            automaton.writeDotFile()
            automaton.dotFileToPng()
            automaton.displayAutomatonImage()
            println("FINISHED WRITING DOT FILE")
            val action = UserAction(InputInteractionType.QUIT, emptyPerceptifer())
            action.provideContext(automaton.currentState.state.hashResults)
            lastActionTaken = action
            println("Generating accessibility report...")
            val result = wcagLogger.getFullAccessibilityReport(automaton)
            println(result)
            startFinishedAnalysis()
            return action
        }

        // TODO: Organize elements in a way that makes searching easy
        val clickable = literalInterace.perceptifers.filter {
            it.virtualPercepts!!.any {
                it.type == PerceptType.VIRTUALLY_CLICKABLE && it.information as Boolean } }

        val swipeable = literalInterace.perceptifers.filter {
            it.percepts!!.any {
                it.type == PerceptType.SCROLL_PROGRESS } }

        val backButton = literalInterace.perceptifers.singleOrNull {
            it.percepts!!.any {
                it.type == PerceptType.PHYSICAL_BUTTON &&
                        it.information.cast<PhysicalButton>().name == "BACK" } }

        val decider = rand.nextFloat()

        var action = if (decider < CLICK_BOUND && clickable.isNotEmpty()) {
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

        // Add some potential edges to the automaton
        clickable.forEach {
            val potentialAction = AutomatonTransition(UserAction(InputInteractionType.CLICK, it, getMidpoint(it)))
            potentialAction.label.provideContext(automaton.currentState.state.hashResults)
            automaton.addTransition(potentialAction, null)
        }
        swipeable.forEach {
            val potentialAction = AutomatonTransition(UserAction(InputInteractionType.SWIPE, it, generateRandomSwipe(it)))
            potentialAction.label.provideContext(automaton.currentState.state.hashResults)
            automaton.addTransition(potentialAction, null)
        }

        // For every screen, should try to navigate with the keyboard
        KeyPress.values().forEach {
//            if (it == KeyPress.ENTER) {
//                clickable.forEach {
//                    val potentialAction = AutomatonTransition(UserAction(InputInteractionType.KEYPRESS, it, KeyPress.ENTER))
//                    potentialAction.label.provideContext(automaton.currentState.state.hashResults)
//                    potentialAction.label.overrideString("label=KEYPRESS of ENTER on $it")
//                    automaton.addTransition(potentialAction, null)
//                }
//            } else {
                val potentialAction = AutomatonTransition(UserAction(InputInteractionType.KEYPRESS, EmptyPerceptifer, it))
                potentialAction.label.provideContext(automaton.currentState.state.hashResults)
                potentialAction.label.overrideString("label=KEYPRESS of ${it.name}")
                automaton.addTransition(potentialAction, null)
//            }
        }

        val statesWithUnexplored = automaton.statesWithUnexploredEdges()
        println("Currently have ${statesWithUnexplored.size} states with unexplored edges")

        action.provideContext(automaton.currentState.state.hashResults)

        // But wait... if this action has already been taken, take an old action instead
        if (automaton.hasExplored(AutomatonTransition(action)) && automaton.getUnexplored() != null) {
            action = automaton.getUnexplored()!!.label
            println("~~~~~~ OPTED FOR UNSEEN ACTION")
        }

        lastActionTaken = action

        return action

    }

    fun startFinishedAnalysis() {
        println("Starting finish analysis")

        val keyboardOnly = automaton //TODO: MAKE A DEEP COPY!!!
        keyboardOnly.transitions.values.forEach {
            val transitionEntry = it
            val newTransitionMap = HashMap<AutomatonTransition<UserAction>, HashSet<AutomatonState<CondensedState>>>()
            it.keys.forEach {
                if (it.label.type == InputInteractionType.KEYPRESS) {
                    newTransitionMap.put(it, transitionEntry[it]!!)
                }
            }
            it.clear()
            it.putAll(newTransitionMap)
        }

        automaton.writeDotFile("$FILE_DB/autoKeysOnly.dot")
        automaton.dotFileToPng("$FILE_DB/autoKeysOnly.dot", "$FILE_DB/autoKeysOnly.png")
        automaton.displayAutomatonImage("$FILE_DB/autoKeysOnly.png")

    }

}