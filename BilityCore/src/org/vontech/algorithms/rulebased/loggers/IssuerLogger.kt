package org.vontech.algorithms.rulebased.loggers

import org.vontech.core.interfaces.LiteralInterace
import org.vontech.core.interfaces.Perceptifer

/**
 * An extendable class which manages the logging of issues found within
 * a user interface. Loggers can be attached to a persona, and each logger
 * will run a series of tests.
 * @author Aaron Vontell
 * @created September 17th, 2018
 * @updated September 17th, 2018
 */
abstract class IssuerLogger {

    abstract fun getDescription(): LoggerDescription
    abstract fun logStaticIssues(literalInterace: LiteralInterace)

    /**
     * The list of issues logged by this logger
     */
    val issueList = mutableListOf<Issue>()

    fun log(issue: Issue) {
        issueList.add(issue)
    }

    fun clear() {
        issueList.clear()
    }

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
 * A representation of an issue that can be logged by the logger
 */
data class Issue(
        val identifier: String,
        val shortDescription: String,
        val longDescription: String,
        val instanceExplanation: String,
        val suggestionExplanation: String,
        val passes: Boolean,
        val extras: Any,
        val perceptifer: Perceptifer
)

class IssuerBuilder {

    private var identifier: String? = null
    private var shortDescription: String? = null
    private var longDescription: String? = null
    private var instanceExplanation: String? = null
    private var suggestionExplanation: String? = null
    private var pass: Boolean = true
    private var extras: Any? = null
    private var perceptifer: Perceptifer? = null

    fun initialize(identifier: String,
                   shortDescription: String,
                   longDescription: String,
                   perceptifer: Perceptifer): IssuerBuilder {
        this.identifier = identifier
        this.shortDescription = shortDescription
        this.longDescription = longDescription
        this.perceptifer = perceptifer
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

    fun build(): Issue {

        if (listOf(identifier, shortDescription, longDescription, instanceExplanation,
                  suggestionExplanation, pass, extras, perceptifer).any { it == null }) {
            throw RuntimeException("All properties of an issue must be set")
        }

        return Issue(
                identifier!!,
                shortDescription!!,
                longDescription!!,
                instanceExplanation!!,
                suggestionExplanation!!,
                pass,
                extras!!,
                perceptifer!!
        )
    }

}