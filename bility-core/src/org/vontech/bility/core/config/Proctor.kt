package org.vontech.bility.core.config

import org.vontech.bility.core.algorithms.automatons.Automaton
import org.vontech.bility.core.interaction.UserAction
import org.vontech.bility.core.interfaces.CondensedState


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

    }

}