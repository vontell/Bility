package org.vontech.core.config

import org.vontech.algorithms.automatons.Automaton
import org.vontech.algorithms.automatons.AutomatonState
import org.vontech.algorithms.rulebased.loggers.UiIssuerLogger
import org.vontech.core.interaction.InputInteractionType
import org.vontech.core.interfaces.Percept

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

/**
 * A state is defined by the count of a certain number of
 * Perceptifers within a literal interface. For instance,
 * a login page may be defined as at least 1 input box,
 * some buttons, and a single background image.
 */
class SpecificationState {

    fun hasAtLeast(count: Int, selector: PerceptiferSelector) {

    }

    fun hasNoMoreThan(count: Int, selector: PerceptiferSelector) {

    }

    fun hasExactly(count: Int, selector: PerceptiferSelector) {

    }

    fun hasZero(selector: PerceptiferSelector) {
        hasExactly(0, selector)
    }

    fun hasSome(selector: PerceptiferSelector) {
        hasAtLeast(1, selector)
    }

}

/**
 * A PerceptiferSelector allows for finding Perceptifers
 * that have the requested percepts, and not the blacklisted
 * percepts
 */
class PerceptiferSelector(
        val has: List<Percept>,
        val omits: List<Percept> = listOf()
)

/**
 * A transition is an interaction type on a state
 */

class SpecificationStateTransition(
        val inputType: InputInteractionType
)