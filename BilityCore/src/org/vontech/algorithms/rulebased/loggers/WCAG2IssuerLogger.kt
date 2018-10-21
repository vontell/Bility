package org.vontech.algorithms.rulebased.loggers

import org.vontech.algorithms.automatons.Automaton
import org.vontech.constants.WCAGConstants
import org.vontech.core.interaction.UserAction
import org.vontech.core.interfaces.*
import java.text.SimpleDateFormat

enum class WCAGLevel {
    A, AA, AAA
}

data class WCAGExtras(
        val level: WCAGLevel,
        val link: String
)

class WCAG2IssuerLogger(val wcagLevel: WCAGLevel) : UiIssuerLogger() {

    val WCAG2_URL = "https://www.w3.org/TR/WCAG20/"

    override fun getDescription(): LoggerDescription {
        return LoggerDescription(
                "WCAG 2.0 - Level $wcagLevel",
                "Logs issues that violate Level A satisfaction of WCAG 2.0",
                "Logs issues that violate Level A satisfaction of WCAG 2.0, " +
                        "details of which can be found at <a href=\"$WCAG2_URL\">$WCAG2_URL</a>"
        )
    }

    private fun getAllStaticIssues(p: Perceptifer): MutableList<StaticIssue> {
        val staticIssues = mutableListOf<StaticIssue>()
        logNonTextContentTextAlternatives(p)?.let { staticIssues.add(it) }
        return staticIssues
    }

    /**
     * Logs errors for the given perceptifer if Principle 1.1.1 of WCAG 2.0 is not met
     * The criteria for meeting this are as follows:
     *      1.1.1 Non-text Content: All non-text content that is presented to the
     *      user has a text alternative that serves the equivalent purpose, except
     *      for the situations listed below. (Level A)
     * Here is some of our interpretation / process for detecting this:
     *      - If this has text, this passes
     *      - Otherwise, it must have content readable by a screen reader, except
     *        for the following exceptions:
     *          - If this is a button / control, it has a name
     *          - If time-based media, text-content as a description
     *          - If sensory experience, text-content as a description
     *          - If CAPTCHA, describe what this is, and provide alternatives to CAPTCHA
     *            not based on vision
     *          - If decoration, does not need screen reader content
     */
    private fun logNonTextContentTextAlternatives(perceptifer: Perceptifer): StaticIssue? {

        val textInfos = perceptifer.getPerceptsOfType(PerceptType.TEXT)
        val screenReaderInfos = perceptifer.getPerceptsOfType(PerceptType.VIRTUAL_SCREEN_READER_CONTENT)

        // Build the base Issue
        val builder = IssuerBuilder()
        builder.initialize(WCAGConstants.P111_NAME, WCAGConstants.P111_SHORT,
                           WCAGConstants.P111_LONG)
                .extras(WCAGExtras(WCAGConstants.P111_LEVEL, WCAGConstants.P111_LINK))

        // Add this perceptifer
        builder.addPerceptifers(mutableListOf(perceptifer))

        var hadEmptyText = false

        // First, if it was purely a container, ignore
        if (wasInvisible(perceptifer)) {
            builder
                    .passes(true)
                    .explanation(WCAGConstants.P111_INV_EXPLANATION)
                    .suggest(WCAGConstants.SUGGEST_NONE)
            return builder.buildStaticIssue()
        }
        else if (textInfos.isNotEmpty()) {
            val hasNonEmptyString = textInfos.any { PerceptParser.fromText(it).isNotBlank() }
            if (hasNonEmptyString) {
                builder
                        .passes(true)
                        .explanation(WCAGConstants.P111_TEXT_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                return builder.buildStaticIssue()
            } else {
                // TODO: If there was empty text, we better check the other percepts
                hadEmptyText = true
            }
        }
        else {

            // If it was not text, then first check if it at least had screen reader attributes
            return if (screenReaderInfos.isNotEmpty()) {
                builder
                        .passes(true)
                        .explanation(WCAGConstants.P111_SCREEN_READER_AVAIL_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                builder.buildStaticIssue()
            } else {
                // TODO: Only pass if this is an exception. Otherwise, fail
                builder
                        .passes(false)
                        .explanation(WCAGConstants.P111_SCREEN_READER_GONE_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                builder.buildStaticIssue()
            }

        }

        return null //this should never be reached, but for some reason that is not detected

    }

    private fun wasInvisible(perceptifer: Perceptifer): Boolean {

        return perceptifer.percepts!!.any {
            it.type == PerceptType.INVISIBLE && PerceptParser.fromInvisible(it)
        }

    }

    override fun getFullAccessibilityReport(automaton: Automaton<CondensedState, UserAction>): String {

        // For each state, log accessibility issues of that state
        // i.e. for each state, and for each unique has (across the entirety of the automaton,
        // pick a representative perceptifer and detect issues. Keep a count of hash occurrences)

        val hashToStaticIssues = HashMap<Int, MutableList<StaticIssue>>()

        // In the case of accessibility, we want to trim the edges that are floating and have
        // already been visited
        automaton.trimEmptyEdgesIfDetermined()


        automaton.states.forEach {
            val analysisResults = it.state.hashResults
            analysisResults.hashesToIds.keys.forEach {
                // If the hash has not been assessed for accessibility yet, do that
                if (!hashToStaticIssues.containsKey(it)) {
                    val ids = analysisResults.hashesToIds[it]!!
                    val representative = analysisResults.idsToPerceptifers[ids.first()]!!
                    val rest = ids.subList(1, ids.count()).map { analysisResults.idsToPerceptifers[it]!! }
                    val staticIssues = getAllStaticIssues(representative)
                    // Then add all other perceptifers to this
                    staticIssues.forEach {
                        it.perceptifers.addAll(rest)
                    }
                    hashToStaticIssues[it] = staticIssues
                }
                // Otherwise, simply tack on new perceptifers
                // TODO: This may overcount
                else {
                    val hash = it
                    hashToStaticIssues[it]!!.forEach {
                        it.perceptifers.addAll(analysisResults.hashesToIds[hash]!!.map { analysisResults.idsToPerceptifers[it]!! })
                    }
                }
            }
        }

        // For each transition, log dynamic issues


        // Now generate report
        // First, log issues

        val allStaticIssues = hashToStaticIssues.values.flatten()

        val builder = StringBuilder()
        builder.appendln("WCAG 2.0 Accessibility Report - Completed at ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")}")
        if (allStaticIssues.isNotEmpty()) {
            builder.appendln("Unique Static Accessibility Issues")
            var passCount = 0
            for (issue in allStaticIssues) {
                if (issue.passes) {
                    passCount++
                }
                else {
                    builder.appendln("--------------------------------------")
                    builder.appendln("\t Issue Information:")
                    builder.appendln("\t\t Identifier: ${issue.identifier}")
                    builder.appendln("\t\t Description: ${issue.shortDescription}")
                    builder.appendln("\t\t Explanation: ${issue.instanceExplanation}")
                    builder.appendln("\t\t Suggestion: ${issue.suggestionExplanation}")
                    builder.appendln("\t\t WCAG Details: ${issue.extras}")
                    builder.appendln("\t\t Total Violations: ${issue.perceptifers.size}")
                    builder.appendln("\t Example:")
                    for (percept in issue.perceptifers.first().percepts!!) {
                        builder.appendln("\t\t(R) $percept")
                    }
                    for (percept in issue.perceptifers.first().virtualPercepts!!) {
                        builder.appendln("\t\t(V) $percept")
                    }
                }
            }
            builder.appendln("-------------------------------------- (note that $passCount pass events were found) ")
        }

        return builder.toString()

//        logger?.info("LATEST INTERFACE INFORMATION ---------")
//        logger?.info("Metadata: ${latest.metadata}")
//        var count = 1
//        for (perceptifer in latest.perceptifers) {
//            logger?.info("Perceptifer $count (${perceptifer.id}):")
//            for (percept in perceptifer.percepts!!) {
//                logger?.info("\t(R) $percept")
//            }
//            for (percept in perceptifer.virtualPercepts!!) {
//                logger?.info("\t(V) $percept")
//            }
//            count++
//        }
//        logger?.info("--------------------------------------")

    }

}