package org.vontech.bility.core.algorithms.rulebased.loggers

import org.vontech.bility.core.algorithms.automatons.Automaton
import org.vontech.bility.core.interaction.UserAction
import org.vontech.bility.core.interfaces.CondensedState
import org.vontech.bility.core.interfaces.Perceptifer

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
    abstract fun getAccessibilityReportAsString(automaton: Automaton<CondensedState, UserAction>): String
    abstract fun getAccessibilityReportAsJson(automaton: Automaton<CondensedState, UserAction>): IssueReport

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
        val mappings: List<DynamicMapping>?
)

data class DynamicMapping(
        val startState: String?,
        val transition: UserAction?,
        val endState: String?
)

data class IssueReport(
        val staticIssues: List<StaticIssue>,
        val dynamicIssues: List<DynamicIssue>
)


class IssuerBuilder {

    private var identifier: String? = null
    private var shortDescription: String? = null
    private var longDescription: String? = null
    private var instanceExplanation: String? = null
    private var suggestionExplanation: String? = null
    private var mappings: MutableList<DynamicMapping> = mutableListOf()
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

    fun addDynamicMapping(startState: String?, transition: UserAction?, endState: String?): IssuerBuilder {
        this.mappings.add(DynamicMapping(startState, transition, endState))
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
            throw RuntimeException("All required properties of an issue must be set")
        }

        return DynamicIssue(
                identifier!!,
                shortDescription!!,
                longDescription!!,
                instanceExplanation!!,
                suggestionExplanation!!,
                pass,
                extras!!,
                mappings
        )

    }

}