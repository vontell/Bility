package org.vontech.algorithms.personas

import org.vontech.algorithms.personas.utils.PersonaContext
import org.vontech.core.interaction.InputInteractionType
import org.vontech.core.interaction.UserAction
import org.vontech.core.interfaces.LiteralInterace
import org.vontech.core.interfaces.emptyPerceptifer
import java.util.*

/**
 * A file containing code for representing a base persona. Special personas
 * should subclass or use this information, and then extend upon it.
 * @author Aaron Vontell
 * @created August 12th, 2018
 * @updated August 12th, 2018
 */
open class Person(val nickname: String, val rand: Random = Random()) {

    open val baseType = "Person"

    var lastActionTaken: UserAction? = null

    open fun getDescription(): String {
        return "I am $nickname, a human, and that is all. I do not do anything with user interfaces."
    }

    /**
     * Methods for the User Interface Interaction Loops
     * Just as a recap, recall the steps:
     *  1) The user is setup with memory and context
     *  2) A literal interface is given to the user
     *  3) Percepts are received by the user depending on setup
     *     and channel filters.
     *  4) The user processes these parcepts to build an
     *     internal model of the user interface, as well as
     *     change their context.
     *  5) The user decides to take an action, most often onto
     *     the user interface.
     */

    /**
     * There are actually two initial setup processes:
     *  1) An initial context that drives the user, and defines base
     *     behavior.
     *  2)
     */

    public lateinit var context: PersonaContext


    open fun reactToNewUserInterface(literalInterace: LiteralInterace): UserAction {

        // The base Person does nothing with the user interface
        val action = UserAction(InputInteractionType.NONE, emptyPerceptifer())
        lastActionTaken = action
        return action

    }

    open fun updateInternalKnowledge(literalInterace: LiteralInterace) {
        // The default does nothing to update their internal knowledge
    }

}