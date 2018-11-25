package org.vontech.algorithms.personas

import org.vontech.algorithms.automatons.Automaton
import org.vontech.algorithms.automatons.AutomatonState
import org.vontech.algorithms.automatons.AutomatonTransition
import org.vontech.algorithms.hci.blendColors
import org.vontech.algorithms.personas.utils.Propagator
import org.vontech.algorithms.rulebased.loggers.IssueReport
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
    var visitHistory = mutableListOf<AutomatonState<CondensedState>>()
    val visitSize = 5

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

        // Generate a new fuzzy state, after doing some internal processing
        understandLiteralInterface(literalInterace)
        val newState = CondensedState(literalInterace)

        // Update any memory based on this newState
        // ...

        // Add this state to the automation, based on the last decision made
        val state = AutomatonState(newState)
        if (this.lastActionTaken != null) {
            automaton.addTransition(AutomatonTransition(this.lastActionTaken!!), state)
        } else {
            automaton = Automaton(state)
            automaton.setImageFunction {
                "http://localhost:8080/screens/upload-${it.state.literalInterace.metadata.id}.png"
            }
        }

        visitHistory.add(state)
        if (visitHistory.size > visitSize) {
            visitHistory.removeAt(0)
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
        println(actionCount)
        if (actionCount > 100 || (actionCount > 100 && automaton.statesWithUnexploredEdges().isEmpty())) {
            println("FINISHED AFTER $actionCount actions with ${automaton.statesWithUnexploredEdges().size} states unexplored")
            automaton.writeDotFile()
            automaton.dotFileToPng()
            automaton.displayAutomatonImage()
            println("FINISHED WRITING DOT FILE")
            val action = UserAction(InputInteractionType.QUIT, emptyPerceptifer())
            action.provideContext(automaton.currentState.state.hashResults)
            lastActionTaken = action
            println("Generating accessibility report...")
            val result = wcagLogger.getAccessibilityReportAsString(automaton)
            println(result)
            //startFinishedAnalysis()
            return action
        }

        // Before anything - if there is an unexplored edge here, take it.
        val unexploredHere = automaton.getUnexploredEdgesFrom(automaton.currentState)
        if (unexploredHere.isNotEmpty()) {
            println("DUDE !!!!!!! WE FOUND AN ACTION RIGHT HERE!")
            val action = unexploredHere.first().label
            action.provideContext(automaton.currentState.state.hashResults)
            lastActionTaken = action
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

        val pathToUnexplored = automaton.getShortestPathToClosestUnexplored(automaton.currentState)

        // But wait... if this action has already been taken, take an old action instead
        if (automaton.hasExplored(AutomatonTransition(action)) && automaton.getUnexplored() != null) {
            action = automaton.getUnexplored()!!.label
            println("~~~~~~ OPTED FOR UNSEEN ACTION")
        }

        // But wait... if there are states with unexplored, we should go back to them!
        else if (pathToUnexplored != null && pathToUnexplored.isNotEmpty()) {
            if (isStuck()) {
                println("$$$$$$$$ GOT STUCK $$$$$$$$")
            } else {
                println("====== OPTED FOR TRAVELING TO OLD STATE!")
                action = pathToUnexplored[0].label
            }
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

        automaton.trimEmptyEdgesIfDetermined()
        automaton.removeEmptyEdges()
        automaton.writeDotFile("$FILE_DB/autoKeysOnly.dot")
        automaton.dotFileToPng("$FILE_DB/autoKeysOnly.dot", "$FILE_DB/autoKeysOnly.png")
        automaton.displayAutomatonImage("$FILE_DB/autoKeysOnly.png")

    }

    fun askAboutCurrentIssues(): IssueReport {
        return wcagLogger.getAccessibilityReportAsJson(automaton)
    }

    private fun isStuck(): Boolean {
        return visitHistory.toSet().size <= 3
    }

    private fun understandLiteralInterface(literalInterace: LiteralInterace) {

        traverseAndDistributeBackgroundColor(literalInterace)

    }

    private fun traverseAndDistributeBackgroundColor(literalInterace: LiteralInterace) {

        // Get mapping of ids to perceptifers
        val idPerceptiferPairings = literalInterace.perceptifers.map {it.id to it}.toMap()

        // Build tree using perceptifers and ids
        val treeRoot = TreeNode(getRoot(idPerceptiferPairings.values).id)
        buildTree(treeRoot, idPerceptiferPairings)

        // Traverse through tree with prop object, setting background
        val prop = Propagator()
        prop.backgroundColorPercept = PerceptBuilder().createBackgroundColorPercept(0xFFFFFF).latestPercept
        propagateBackgroundColor(treeRoot, idPerceptiferPairings, prop)


    }

    private fun propagateBackgroundColor(node: TreeNode<String>, idPerceptiferPairings: Map<String, Perceptifer>, prop: Propagator) {

        val perceptifer = idPerceptiferPairings[node.value]!!
        val theseColors = perceptifer.getPerceptsOfType(PerceptType.BACKGROUND_COLOR)

        // If this node has colors, don't do anything to the Perceptifer - just update the propagated color
        val newProp = prop.duplicate()
        if (theseColors.isNotEmpty()) {

            // First blend the colors
            val oldColor = PerceptParser.fromColor(newProp.backgroundColorPercept!!).color.toLong()
            val newColor = PerceptParser.fromColor(theseColors.toList()[0]).color.toLong()
            val blendedColor = blendColors(listOf(oldColor, newColor))

            newProp.backgroundColorPercept = PerceptBuilder().createBackgroundColorPercept(blendedColor.toInt()).latestPercept
            println(newProp.backgroundColorPercept)

        // Otherwise, set the color to the last prop color
        } else {
            perceptifer.percepts!!.add(prop.backgroundColorPercept!!)
        }

        node.children.forEach {
            propagateBackgroundColor(it, idPerceptiferPairings, newProp)
        }


    }

}