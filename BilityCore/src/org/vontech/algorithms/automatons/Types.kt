package org.vontech.algorithms.automatons

import java.io.*
import java.lang.RuntimeException

/**
 * A collection of types for Automatons
 * @author Aaron Vontell
 * @date August 6th, 2018
 */

/**
 * Represents a generic state within an automaton
 * @param state The representation of this Automaton state
 */
data class AutomatonState<S> (
    val state: S
)

/**
 * Represents an action that can be taken to transition
 * from one AutomatonState.
 * @param label An identifier for this transition (for
 * instance, the action taken). If not action is required,
 * it is recommended that a new object reference is used.
 */
data class AutomatonTransition<T> (
    val label: T
)

/**
 * Represents an exception when trying to transition from a state when
 * the given transition is invalid.
 */
class StateTransitionException(override val message: String): RuntimeException()

/**
 * An automaton is a generic automaton that can be used in representing state
 * within the Bility platform. NOTE: This is a nondeterministic
 * automaton.
 */
class Automaton<S, T>(private val startState: AutomatonState<S>) {

    // Setup start state and automaton properties
    var currentState: AutomatonState<S> = startState

    val states = mutableSetOf<AutomatonState<S>>()
    val acceptStates = mutableSetOf<AutomatonState<S>>()

    private var imageGetter: ((AutomatonState<S>) -> String)? = null

    // Transitions are maps from one state to other states along transitions
    val transitions = hashMapOf<AutomatonState<S>, HashMap<AutomatonTransition<T>, HashSet<AutomatonState<S>>>>()

    init {
        addState(currentState)
    }

    /**
     * Adds a state to this Automaton, with no connection or transitions to other states
     * @param state The AutomatonState to add to this automaton
     * @return true if this state did not exist and was added successfully, false if the state was already included
     */
    fun addState(state: AutomatonState<S>): Boolean {
        if (state !in transitions) {
            transitions[state] = hashMapOf()
        }
        return states.add(state)
    }

    /**
     * Sets that `transition` should cause a change in state from `from` to `to`.
     * @param from The start state for this transition
     * @param transition The transition to move between to and from
     * @param to The result state for this transition. If null, creates an empty edge
     * @return true if this transition is new, else false
     */
    fun addTransition(from: AutomatonState<S>, transition: AutomatonTransition<T>, to: AutomatonState<S>?): Boolean {

        // First, add states and transitions to the automaton
        states.add(from)
        if (to != null) {
            states.add(to)
        }

        // If starting state not here, add it
        if (from !in transitions.keys) {
            transitions[from] = hashMapOf()
        }

        // Now, if transition is not here, add it
        if (transition !in transitions[from]!!) {
            transitions[from]!![transition] = hashSetOf()
        }

        // Finally, add the finish state to the transition, if not null
        if (to != null) {
            transitions[from]!![transition]!!.add(to)
        }

        return true

    }

    /**
     * Sets a transition from the current state to the desired state `to`
     * @param transition The transition to move between the current state and `to`
     * @param to The result state for this transition
     */
    fun addTransition(transition: AutomatonTransition<T>, to: AutomatonState<S>?): Boolean {
        val result = addTransition(currentState, transition, to)
        if (to != null) {
            currentState = to
        }
        return result
    }

    /**
     * Resets the automaton by setting the current state to the original start state
     */
    fun reset() {
        currentState = startState
    }

    /**
     * Transition from the current state to a destination state given the
     * desired transition. Returns
     */
    fun transition(transition: AutomatonTransition<T>): AutomatonState<S> {
        if (transition !in transitions[currentState]!!) {
            throw StateTransitionException("Attempted to transition from $currentState along $transition, but no transition was found.")
        } else {
            currentState = transitions[currentState]!![transition]!!.toList()[0]
            return currentState
        }
    }

    fun getUnexplored(): AutomatonTransition<T>? {
        println("Checking for unexplored...")
        if (currentState in transitions) {
            transitions[currentState]!!.keys.forEach {
                if (transitions[currentState]!![it]!!.isEmpty()) {
                    return it
                }
            }
        }

        return null
    }

    fun hasExplored(transition: AutomatonTransition<T>): Boolean {
        val possibleStates = transitions[currentState]?.get(transition)
        println("Has explored? ${possibleStates != null}")
        return possibleStates != null
    }

    fun statesWithUnexploredEdges(): List<AutomatonState<S>> {

        return transitions.keys.filter {
            transitions[it]!!.values.any {
                it.isEmpty()
            }
        }

    }

    fun getStringForGraphViz(): String {
        var hangingEdgeCount = 0
        val dotFile = StringBuilder()
        dotFile.append("digraph {\n\t")                    // Define a digraph
        dotFile.append("rankdir=LR;\n\t")                  // Draw the graph from left to right
        dotFile.append("node [shape = doublecircle]; \"")    // Draw a double circle around start state
        dotFile.append(this.startState.state.toString())
        dotFile.append("\";\n\tnode [shape = circle];\n\t")      // All the rest should be circles
        for (state in transitions.keys) {
            val actionMappings = transitions[state]
            if (actionMappings != null) {
                for (actionMap in actionMappings) {
                    val action = actionMap.key
                    val newStates = actionMap.value
                    for (newState in newStates) {
                        dotFile.append("\"")
                        dotFile.append(state.state.toString())               // Create a new state mapping going from here...
                        dotFile.append("\" -> \"")
                        dotFile.append(newState.state.toString())            // .... to here ....
                        dotFile.append("\" [ label = \"")                // through this action
                        dotFile.append(action.label.toString())
                        dotFile.append("\" ];\n\t")
                    }

                    // If newStates was empty, these are hanging edges - add appropriately
                    if (newStates.isEmpty()) {
                        dotFile.append("secret_node_$hangingEdgeCount [style=invis];\n\t\"")
                        dotFile.append(state.state.toString())
                        dotFile.append("\" -> \"")
                        dotFile.append("secret_node_$hangingEdgeCount")
                        dotFile.append("\" [ label = \"")                // through this action
                        dotFile.append(action.toString())
                        dotFile.append("\" style=dashed, color=grey];\n\t")
                        hangingEdgeCount++
                    }

                }
            }
        }

        // Before finishing, if an image converter is given, add those to the end
        if (this.imageGetter != null) {
            this.states.forEach {
                dotFile.append("\"${it.state}\" [image=\"${this.imageGetter!!(it)}\" label=\"\" shape=\"none\"];\n\t")
            }
        }

        var finalString = dotFile.toString()
        finalString = finalString.trim()
        finalString += "\n}"
        return finalString
    }

    fun getStringForGraphVizWeb(): String {
        var hangingEdgeCount = 0
        val dotFile = StringBuilder()
        dotFile.append("digraph {\n\t")                    // Define a digraph
        dotFile.append("rankdir=LR;\n\t")                  // Draw the graph from left to right
        dotFile.append("node [shape = doublecircle]; \"")    // Draw a double circle around start state
        dotFile.append(this.startState.state.toString())
        dotFile.append("\";\n\tnode [shape = circle];\n\t")      // All the rest should be circles
        for (state in transitions.keys) {
            val actionMappings = transitions[state]
            if (actionMappings != null) {
                for (actionMap in actionMappings) {
                    val action = actionMap.key
                    val newStates = actionMap.value
                    for (newState in newStates) {
                        dotFile.append("\"")
                        dotFile.append(state.state.toString())               // Create a new state mapping going from here...
                        dotFile.append("\" -> \"")
                        dotFile.append(newState.state.toString())            // .... to here ....
                        dotFile.append("\" [ label = \"")                // through this action
                        dotFile.append(action.toString())
                        dotFile.append("\" ];\n\t")
                    }

                    // If newStates was empty, these are hanging edges - add appropriately
                    if (newStates.isEmpty()) {
                        dotFile.append("secret_node_$hangingEdgeCount [style=invis];\n\t\"")
                        dotFile.append(state.toString())
                        dotFile.append("\" -> \"")
                        dotFile.append("secret_node_$hangingEdgeCount")
                        dotFile.append("\" [ label = \"")                // through this action
                        dotFile.append(action.toString())
                        dotFile.append("\" style=dashed, color=grey];\n\t")
                        hangingEdgeCount++
                    }

                }
            }
        }

        // Before finishing, if an image converter is given, add those to the end
//        if (this.imageGetter != null) {
//            this.states.forEach {
//                dotFile.append("\"$it\" [image=\"${this.imageGetter!!(it)}\" label=\"\" shape=\"none\"];\n\t")
//            }
//        }

        var finalString = dotFile.toString()
        finalString = finalString.trim()
        finalString += "\n}"
        return finalString
    }

    fun trimEmptyEdgesIfDetermined() {
        transitions.values.forEach {
            for (stateSet in it.values) {
                if (stateSet.any { it.state != null }) {
                    stateSet.removeIf { it.state == null }
                }
            }
        }
    }

    /**
     * Takes in a function which given a state, returns a path to an image
     * to display within the node, instead of a label
     */
    fun setImageFunction(imageGetter: (AutomatonState<S>) -> String) {
        this.imageGetter = imageGetter
    }

    fun writeDotFile() {
        try {
            val file = File("/Users/vontell/Documents/BilityBuildSystem/AndroidServer/fileDB/auto.dot")
            file.createNewFile()
            val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file), "utf-8"))
            writer.write(getStringForGraphViz())
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun dotFileToPng() {
        try {
            val rt = Runtime.getRuntime()
            val pr = rt.exec("dot -Tpng /Users/vontell/Documents/BilityBuildSystem/AndroidServer/fileDB/auto.dot -o /Users/vontell/Documents/BilityBuildSystem/AndroidServer/fileDB/auto.png".split(" ").toTypedArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun displayAutomatonImage() {
        try {
            val rt = Runtime.getRuntime()
            val pr = rt.exec("open /Users/vontell/Documents/BilityBuildSystem/AndroidServer/fileDB/auto.png".split(" ").toTypedArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Creates the domain representation of this automation, for using in path
     * planning and solving
     */
//    fun createDomainPDDL(): Domain {
//
//        val problem = ProblemFactory()
//
//    }

}

