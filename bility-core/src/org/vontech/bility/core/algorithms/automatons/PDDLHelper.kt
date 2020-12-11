package org.vontech.bility.core.algorithms.automatons

/**
 * A collection of tools for converting automatons into PDDL domains and
 * problems for the purpose of path planning.
 *
 * An automaton is converted into PDDL by defining the domain and problem
 * in the following ways:
 *  Domain
 *      - A domain has a few types:
 *          - state - represents a state of the automaton
 *          - transition - represents a transition of the user interface
 *          - modality - represents the core modality of a transition (i.e. swipe, click, etc)
 *     - Predicates
 *          - (is-in-state ?s - state) : true if the automaton is in that state
 *          - (can-use-modality ?m - modality) : true if the user is able to complete that action
 *          - (uses-modality ?t - transition ?m - modality) : true if that transition uses that modality
 *          - (can-move ?start - state ?t - transition ?end - state) : true if two states can be navigated through the given transition
 *     - Durative Actions
 *          - (move
 *
 *
 *
 * @author Aaron Vontell
 * @date October 20th, 2018
 */

fun <S, T> createBaseDomain(automaton: Automaton<S, T>, domainName: String): String {

    val builder = StringBuilder()

    // First, create the domain and it's requirements
    builder.append("(define (domain $domainName)\n")                            // Create the domain with a name
    builder.append("\t(:requirements :strips :typing :durative-actions :duration-inequalities :fluents :timed-initial-literals)\n")     // Add domain requirements
    builder.append("\t(:types\n\t state transition modality - object)\n")       // Create base types

    // Now create the predicates for these types
    builder.append("\t(:predicates\n\t ")
    builder.append("(is-in-state ?s - state)\n\t ")                             // If true, in state ?s
    builder.append("(can-use-modality ?m - modality)\n\t ")                     // If true, user can use modality ?m
    builder.append("(uses-modality ?t - transition ?m - modality)\n\t ")        // If true, ?t uses the modality ?m
    builder.append("(can-move ?start - state ?t - transition ?end - state))\n")  // If true, indicates a transition

    // Now create the a function for transition time
    builder.append("\t(:functions\n\t ")
    builder.append("(transition-time ?t - transition))\n\t ")                     // Time to take transition ?t

    // Finally, create the durative action for traveling
    builder.append("""(:durative-action move
     :parameters (?start - state ?t - transition ?m - modality ?end - state)
     :duration (= ?duration (transition-time ?t))
     :condition (and
                   (is-in-state ?start)
                   (can-move ?start ?t ?end)
                   (uses-modality ?t ?m)
                   (can-use-modality ?m)
                )
     :effect (and
                (not (is-in-state ?start))
                (is-in-state ?end)
             ))""")

    builder.append("\n)")

    val resultingDomain = builder.toString()
    println(resultingDomain)
    return resultingDomain

}

fun <S, T> createProblem(automaton: Automaton<S, T>,
                         domainName: String,
                         problemName: String,
                         allowTransition: (AutomatonTransition<T>) -> Boolean): String {

    val builder = StringBuilder()

    // First, define name
    builder.append("(define (problem $problemName) (:domain $domainName)\n\t")

    // Define all state objects
    builder.append("(:objects ")
    automaton.states.forEach {
        builder.append("${it.hashCode()} ")
    }
    builder.append("- state\n")

    // Define all transitions
    builder.append("\t          ")
    automaton.transitions.values.forEach {
        val actualTransitions = it.keys
        actualTransitions.forEach {
            builder.append("${it.hashCode()} ")
        }
    }
    builder.append("- transition\n")

    // Define all possible modalities
    builder.append("\t          ")


    val resultingProblem = builder.toString()
    println(resultingProblem)
    return resultingProblem

}