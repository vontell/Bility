package org.vontech.core.config

import org.vontech.algorithms.automatons.Automaton
import org.vontech.core.interaction.UserAction
import org.vontech.core.interfaces.CondensedState


/**
 * The test proctor is the entity used to configure and communicate
 * the following settings:
 * - Which types of tests should be run?
 * - Which specific tests should be run or blacklisted?
 * - What should the output format be?
 * - How does each tester decide to terminate testing?
 */
class Proctor {

    val shouldTerminate: MutableSet<(Automaton<CondensedState, UserAction>) -> Boolean> = mutableSetOf()

    fun shouldTerminateOnCondition(condition: SpecificationState) {
        shouldTerminate.add {
            condition.existsIn(automaton = it)
        }
    }

}