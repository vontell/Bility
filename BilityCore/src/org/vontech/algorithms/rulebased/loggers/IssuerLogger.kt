package org.vontech.algorithms.rulebased.loggers

import org.vontech.algorithms.automatons.Automaton
import org.vontech.algorithms.automatons.AutomatonState
import org.vontech.algorithms.automatons.AutomatonTransition
import org.vontech.core.interaction.UserAction
import org.vontech.core.interfaces.CondensedState
import org.vontech.core.interfaces.LiteralInterace
import org.vontech.core.interfaces.Perceptifer

// TODO: Possibly make these all generics

/**
 * An extendable class which manages the logging of issues found within
 * a user interface. Loggers can be attached to a persona, and each logger
 * will run a series of tests.
 * @author Aaron Vontell
 * @created September 17th, 2018
 * @updated October 14th, 2018
 */
abstract class UiIssuerLogger {

    abstract fun getDescription(): LoggerDescription
    abstract fun getFullAccessibilityReport(automaton: Automaton<CondensedState, UserAction>): String

}

/**
 * A description for a logger, detailing what sort of issues
 * it will filter and log.
 */
data class LoggerDescription(
        val name: String,
        val shortDescription: String,
        val longDescription: String
)

/**
 * A representation of a static issue that can be logged by the logger
 */
data class StaticIssue(
        val identifier: String,
        val shortDescription: String,
        val longDescription: String,
        val instanceExplanation: String,
        val suggestionExplanation: String,
        val passes: Boolean,
        val extras: Any,
        val perceptifers: MutableList<Perceptifer>
)

/**
 * A representation of a dynamic issue that can be logged by the logger
 */
data class DynamicIssue(
        val identifier: String,
        val shortDescription: String,
        val longDescription: String,
        val instanceExplanation: String,
        val suggestionExplanation: String,
        val passes: Boolean,
        val extras: Any,
        val startState: AutomatonState<CondensedState>?,
        val endState: AutomatonState<CondensedState>?,
        val transition: AutomatonTransition<UserAction>?
)

class IssuerBuilder {

    private var identifier: String? = null
    private var shortDescription: String? = null
    private var longDescription: String? = null
    private var instanceExplanation: String? = null
    private var suggestionExplanation: String? = null
    private var startState: AutomatonState<CondensedState>? = null
    private var endState: AutomatonState<CondensedState>? = null
    private var transition: AutomatonTransition<UserAction>? = null
    private var pass: Boolean = true
    private var extras: Any? = null
    private var perceptifers: MutableList<Perceptifer> = mutableListOf()

    fun initialize(identifier: String,
                   shortDescription: String,
                   longDescription: String): IssuerBuilder {
        this.identifier = identifier
        this.shortDescription = shortDescription
        this.longDescription = longDescription
        return this
    }

    fun explanation(explanation: String): IssuerBuilder {
        this.instanceExplanation = explanation
        return this
    }

    fun passes(pass: Boolean): IssuerBuilder {
        this.pass = pass
        return this
    }

    fun extras(extras: Any): IssuerBuilder {
        this.extras = extras
        return this
    }

    fun suggest(suggestion: String): IssuerBuilder {
        this.suggestionExplanation = suggestion
        return this
    }

    fun addPerceptifers(perceptifers: List<Perceptifer>): IssuerBuilder {
        this.perceptifers.addAll(perceptifers)
        return this
    }

    fun addStartState(state: AutomatonState<CondensedState>): IssuerBuilder {
        this.startState = state
        return this
    }

    fun addEndState(state: AutomatonState<CondensedState>): IssuerBuilder {
        this.endState = state
        return this
    }

    fun addTransition(transition: AutomatonTransition<UserAction>): IssuerBuilder {
        this.transition = transition
        return this
    }

    fun buildStaticIssue(): StaticIssue {

        if (listOf(identifier, shortDescription, longDescription, instanceExplanation,
                  suggestionExplanation, pass, extras).any { it == null } || perceptifers.size == 0) {
            throw RuntimeException("All properties of an issue must be set")
        }

        return StaticIssue(
                identifier!!,
                shortDescription!!,
                longDescription!!,
                instanceExplanation!!,
                suggestionExplanation!!,
                pass,
                extras!!,
                perceptifers
        )

    }

    fun buildDynamicIssue(): DynamicIssue {

        if (listOf(identifier, shortDescription, longDescription, instanceExplanation,
                        suggestionExplanation, pass, extras).any { it == null }) {
            println(listOf(identifier, shortDescription, longDescription, instanceExplanation,
                    suggestionExplanation, pass, extras))
            throw RuntimeException("All required properties of an issue must be set")
        }
        if (listOf(startState, endState, transition).all { it == null }) {
            throw RuntimeException("A dynamic issue must indicate at least the start state, end state, or transition")
        }

        return DynamicIssue(
                identifier!!,
                shortDescription!!,
                longDescription!!,
                instanceExplanation!!,
                suggestionExplanation!!,
                pass,
                extras!!,
                startState,
                endState,
                transition
        )

    }

}