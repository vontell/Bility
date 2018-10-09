package org.vontech.algorithms.rulebased.loggers

import org.vontech.core.interfaces.PerceptParser
import org.vontech.core.interfaces.PerceptType
import org.vontech.core.interfaces.Perceptifer
import org.vontech.constants.WCAGConstants
import org.vontech.core.interfaces.LiteralInterace

enum class WCAGLevel {
    A, AA, AAA
}

data class WCAGExtras(
        val level: WCAGLevel,
        val link: String
)

class WCAG2IssuerLogger(val wcagLevel: WCAGLevel) : IssuerLogger() {

    val WCAG2_URL = "https://www.w3.org/TR/WCAG20/"

    override fun getDescription(): LoggerDescription {
        return LoggerDescription(
                "WCAG 2.0 - Level $wcagLevel",
                "Logs issues that violate Level A satisfaction of WCAG 2.0",
                "Logs issues that violate Level A satisfaction of WCAG 2.0, " +
                        "details of which can be found at <a href=\"$WCAG2_URL\">$WCAG2_URL</a>"
        )
    }

    override fun logStaticIssues(literalInterace: LiteralInterace) {
        literalInterace.perceptifers.forEach { logIssuesLevelA(it) }
    }

    private fun logIssuesLevelA(p: Perceptifer) {
        logNonTextContentTextAlternatives(p)
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
    private fun logNonTextContentTextAlternatives(perceptifer: Perceptifer) {

        val textInfos = perceptifer.getPerceptsOfType(PerceptType.TEXT)
        val screenReaderInfos = perceptifer.getPerceptsOfType(PerceptType.VIRTUAL_SCREEN_READER_CONTENT)

        // Build the base Issue
        val builder = IssuerBuilder()
        builder.initialize(WCAGConstants.P111_NAME, WCAGConstants.P111_SHORT,
                           WCAGConstants.P111_LONG, perceptifer)
                .extras(WCAGExtras(WCAGConstants.P111_LEVEL, WCAGConstants.P111_LINK))

        var hadEmptyText = false

        // First, if it was purely a container, ignore
        if (wasInvisible(perceptifer)) {
            builder
                    .passes(true)
                    .explanation(WCAGConstants.P111_INV_EXPLANATION)
                    .suggest(WCAGConstants.SUGGEST_NONE)
            log(builder.build())
        }
        else if (textInfos.isNotEmpty()) {
            val hasNonEmptyString = textInfos.any { PerceptParser.fromText(it).isNotBlank() }
            if (hasNonEmptyString) {
                builder
                        .passes(true)
                        .explanation(WCAGConstants.P111_TEXT_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                log(builder.build())
            } else {
                // If there was empty text, we better check the other percepts
                hadEmptyText = true
            }
        }
        else {

            // If it was not text, then first check if it at least had screen reader attributes
            if (screenReaderInfos.isNotEmpty()) {
                builder
                        .passes(true)
                        .explanation(WCAGConstants.P111_SCREEN_READER_AVAIL_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                log(builder.build())
            } else {
                // TODO: Only pass if this is an exception. Otherwise, fail
                builder
                        .passes(false)
                        .explanation(WCAGConstants.P111_SCREEN_READER_GONE_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                log(builder.build())
            }

        }

    }

    private fun wasInvisible(perceptifer: Perceptifer): Boolean {

        return perceptifer.percepts!!.any {
            it.type == PerceptType.INVISIBLE && PerceptParser.fromInvisible(it)
        }

    }

}