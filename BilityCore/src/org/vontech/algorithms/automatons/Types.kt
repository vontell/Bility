package org.vontech.algorithms.automatons

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
data class AutomatonState<T> (
    val state: T
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
 * within the Bility platform. NOTE: At the moment, this is a deterministic
 * automaton.
 */
class Automaton<S, T>(private val startState: AutomatonState<S>) {

    // Setup start state and automaton properties
    var currentState: AutomatonState<S> = startState

    private val states = mutableSetOf<AutomatonState<S>>()
    private val acceptStates = mutableSetOf<AutomatonState<S>>()
    private val alphabet = mutableSetOf<AutomatonTransition<T>>()

    // Transitions are maps from one state to other states along transitions
    private val transitions = mutableMapOf<AutomatonState<S>, MutableMap<AutomatonTransition<T>, MutableSet<AutomatonState<S>>>>()

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
            transitions[state] = mutableMapOf()
        }
        return states.add(state)
    }

    /**
     * Adds a transition to this Automatons alphabet, unattached to any states
     * @param transition The AutomatonTransition to add to this automaton
     * @return true if this transition did not exist and was added successfully, false if the transition was
     *         already included
     */
    fun addLetter(transition: AutomatonTransition<T>): Boolean {
        return alphabet.add(transition)
    }

    /**
     * Sets that `transition` should cause a change in state from `from` to `to`.
     * @param from The start state for this transition
     * @param transition The transition to move between to and from
     * @param to The result state for this transition
     * @return true if this transition is new, else false
     */
    fun addTransition(from: AutomatonState<S>, transition: AutomatonTransition<T>, to: AutomatonState<S>): Boolean {

        // First, add states and transitions to the automaton
        states.add(from)
        states.add(to)
        alphabet.add(transition)

        return if (from in transitions) {
            if (transition in transitions[from]!!) {
                transitions[from]!![transition]!!.add(to)
            } else {
                transitions[from]!![transition] = mutableSetOf(to)
                true
            }
        } else {
            transitions[from] = mutableMapOf(transition to mutableSetOf(to))
            true
        }

    }

    /**
     * Sets a transition from the current state to the desired state `to`
     * @param transition The transition to move between the current state and `to`
     * @param to The result state for this transition
     */
    fun addTransition(transition: AutomatonTransition<T>, to: AutomatonState<S>): Boolean {
        return addTransition(currentState, transition, to)
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

}

