package org.vontech.bility.core.config

import org.vontech.bility.core.algorithms.automatons.Automaton
import org.vontech.bility.core.algorithms.automatons.AutomatonState
import org.vontech.bility.core.algorithms.rulebased.loggers.UiIssuerLogger
import org.vontech.bility.core.interaction.InputInteractionType
import org.vontech.bility.core.interaction.UserAction
import org.vontech.bility.core.interfaces.CondensedState
import org.vontech.bility.core.interfaces.Percept
import org.vontech.bility.core.interfaces.Perceptifer

/**
 * Defines a specification for interacting with and navigating
 * through a user interface. However, this is an underdetermined
 * specification which allows for lenient navigation throughout a
 * user interface. For example, consider the following scenario:
 *
 * ~~~~~~~
 * We would like to check an application for accessibility compliance.
 * Verifying the accessibility of an application requires the following:
 *  1. Checking static issues for interface
 *  2. Dynamically navigating through the application
 *  3. Making sure that dynamic accessibility issues do not arise.
 *
 *  Since checking for accessibility in dynamic / navigating situations
 *  is sometimes dependent on the mode of interaction, we cannot explicitly
 *  state the interaction method and objects in all cases. For example, we can
 *  indicate that moving to state 1 to state 2 can be done by clicking
 *  perceptifer X, but if the user has no motor skills and must use speech, they
 *  may need to interact through perceptifer Y. If the user does not indicate
 *  that perceptifer Y is a valid path to state 2, our system still makes a best
 *  effort to reach state 2 using different perceptifers.
 *
 *  Therefore, we verify accessibility by allowing the user to indicate the following:
 *
 *  1) The issues / issue types they would like to detect
 *  2) The states that can be reached within the user interface
 *  3) The known interaction modes to navigate between states
 *
 *  Given this information, we navigate through the user interface, tracking
 *  accessibility issues within the defined information, and sometimes
 *  providing a "best effort" in navigation when information is not available.
 *
 *  Here is a JSON example:
 *
 *  {
 *      loggers: [
 *          "WCAG 2.0",
 *          "AQuA",
 *          "Silver Essentials"
 *      ],
 *      states: [
 *          {
 *              name: "main page",
 *              start: true
 *              has: [
 *                  {
 *                      type: "TEXT",
 *                      information: "Written by",
 *                      hasAtLeast: 1
 *                  },
 *                  {
 *                      type: "BUTTON",
 *                      information: "View article",
 *                      hasAtLeast: 1
 *                  },
 *                  {
 *                      type: "BUTTON",
 *                      information: "View profile",
 *                      hasExactly: 1,
 *                      name: "profileButton",
 *                  }
 *              ],
 *              transitions: {
 *                  interaction: {
 *                      type: "CLICK",
 *                      subject: "profileButton",
 *                      destination: "profile page"
 *                  }
 *              }
 *          },
 *          {
 *              name: "profile page",
 *              ...
 *          }
 *      ]
 *  }
 *
 * ~~~~~~~
 */
class Specification {

    private var automaton: Automaton<SpecificationState, SpecificationStateTransition>? = null

    fun testFor(vararg loggers: UiIssuerLogger): Specification {
        return this
    }

    fun startState(state: SpecificationState): Specification {
        automaton = Automaton(AutomatonState(state))
        return this
    }

}

enum class SpecificationType {
    AT_LEAST,
    NO_MORE_THAN,
    EXACTLY
}

/**
 * A state is defined by the count of a certain number of
 * Perceptifers within a literal interface. For instance,
 * a login page may be defined as at least 1 input box,
 * some buttons, and a single background image.
 */
class SpecificationState {

    private val selectorSet: MutableSet<Triple<PerceptiferSelector, Int, SpecificationType>> = mutableSetOf()

    fun hasAtLeast(count: Int, selector: PerceptiferSelector): SpecificationState {
        selectorSet.add(Triple(selector, count, SpecificationType.AT_LEAST))
        return this
    }

    fun hasNoMoreThan(count: Int, selector: PerceptiferSelector): SpecificationState {
        selectorSet.add(Triple(selector, count, SpecificationType.NO_MORE_THAN))
        return this
    }

    fun hasExactly(count: Int, selector: PerceptiferSelector): SpecificationState {
        selectorSet.add(Triple(selector, count, SpecificationType.EXACTLY))
        return this
    }

    fun hasZero(selector: PerceptiferSelector): SpecificationState {
        return hasExactly(0, selector)
    }

    fun hasSome(selector: PerceptiferSelector): SpecificationState {
        return hasAtLeast(1, selector)
    }

    fun selectStates(automaton: Automaton<CondensedState, UserAction>): Map<Triple<PerceptiferSelector, Int, SpecificationType>, Set<AutomatonState<CondensedState>>> {

        // TODO: Make this a better type later...
        val resultsMapping = mutableMapOf<Triple<PerceptiferSelector, Int, SpecificationType>, Set<AutomatonState<CondensedState>>>()

        selectorSet.forEach { spec ->

            val (selector, count, type) = spec

            val foundStates = mutableSetOf<AutomatonState<CondensedState>>()

            automaton.states.forEach { state ->

                val foundPerceptifers = mutableSetOf<Perceptifer>()
                state.state.literalInterace.perceptifers.forEach { perceptifer ->
                    if (selector.selects(perceptifer)) {
                        foundPerceptifers.add(perceptifer)
                    }
                }

                val c = foundPerceptifers.size
                when(type) {
                    SpecificationType.AT_LEAST -> if (c >= count) { foundStates.add(state) }
                    SpecificationType.NO_MORE_THAN -> if (c <= count) { foundStates.add(state) }
                    SpecificationType.EXACTLY -> if (c == count) { foundStates.add(state) }
                }

            }

            if (foundStates.isNotEmpty()) {
                resultsMapping[spec] = foundStates
            }

        }

        return resultsMapping

    }

}

/**
 * A PerceptiferSelector allows for finding Perceptifers
 * that have the requested percepts, and not the blacklisted
 * percepts
 */
class PerceptiferSelector(
        val has: List<Percept>,
        val omits: List<Percept> = listOf()) {

    fun selects(perceptifer: Perceptifer): Boolean {

        // The perceptifer cannot have any of the omit percepts...
        omits.forEach {
            val typeMatches = perceptifer.getPerceptsOfType(it.type)
            if (typeMatches.any {p -> p.information == it.information}) {
                return false
            }
        }

        // And the perceptifer must have at least one of the has percepts
        has.forEach {
            val typeMatches = perceptifer.getPerceptsOfType(it.type)
            if (typeMatches.any {p -> p.information == it.information}) {
                return true
            }
        }

        return false

    }

}

/**
 * A transition is an interaction type on a state
 */

class SpecificationStateTransition(
        val inputType: InputInteractionType
)