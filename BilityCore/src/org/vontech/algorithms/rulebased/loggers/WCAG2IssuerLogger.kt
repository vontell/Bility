package org.vontech.algorithms.rulebased.loggers

import org.vontech.algorithms.automatons.Automaton
import org.vontech.algorithms.hci.blendColors
import org.vontech.algorithms.hci.getContrast
import org.vontech.constants.GoogleAccessibilityScannerConstants
import org.vontech.constants.WCAGConstants
import org.vontech.core.interaction.InputInteractionType
import org.vontech.core.interaction.KeyPress
import org.vontech.core.interaction.UserAction
import org.vontech.core.interfaces.*
import org.vontech.utils.cast
import java.text.SimpleDateFormat
import java.util.*

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

    private fun getAllIndividualStaticIssues(p: Perceptifer): MutableList<StaticIssue> {
        val staticIssues = mutableListOf<StaticIssue>()
        logNonTextContentTextAlternatives(p)?.let { staticIssues.add(it) }
        logMinimumTouchTargetSize(p)?.let { staticIssues.add(it) }
        logMinimumContrast(p).let { staticIssues.addAll(it) }
        return staticIssues
    }

    private fun getAllScreenStaticIssues(literalInterace: LiteralInterace): MutableList<StaticIssue> {
        val staticIssues = mutableListOf<StaticIssue>()
        logSameScreenReaderContent(literalInterace)?.let { staticIssues.addAll(it) }
        return staticIssues
    }

    private fun getAllDynamicIssues(automaton: Automaton<CondensedState, UserAction>): MutableList<DynamicIssue> {

        val dynamicIssues = logKeyboard(automaton)
        dynamicIssues.addAll(logChangeOnRequest(automaton))
        dynamicIssues.addAll(logNoKeyboardTrap(automaton))
        dynamicIssues.addAll(logOnFocus(automaton))
        return dynamicIssues
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
        val mediaInfos = perceptifer.getPerceptsOfType(PerceptType.MEDIA_TYPE)

        val isImage = mediaInfos.any {PerceptParser.fromMediaType(it) == MediaType.IMAGE}

        // Build the base Issue
        val builder = IssuerBuilder()
        builder.initialize(WCAGConstants.P111_NAME, WCAGConstants.P111_SHORT,
                           WCAGConstants.P111_LONG)
                .extras(WCAGExtras(WCAGConstants.P111_LEVEL, WCAGConstants.P111_LINK))

        // Add this perceptifer
        builder.addPerceptifers(mutableListOf(perceptifer))

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
            } else if (screenReaderInfos.isEmpty()) {
                builder
                        .passes(false)
                        .explanation(WCAGConstants.P111_SCREEN_READER_GONE_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                return builder.buildStaticIssue()
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
            } else if (isImage) {
                builder
                        .passes(false)
                        .explanation(WCAGConstants.P111_IMG_FAIL_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                builder.buildStaticIssue()
            } else {
                // TODO: Only pass if this is an exception. Otherwise, fail
                builder
                        .passes(true)
                        .explanation(WCAGConstants.P111_SCREEN_READER_GONE_EXPLANATION)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                builder.buildStaticIssue()
            }

        }

        return null //this should never be reached, but for some reason that is not detected

    }


    val CONTRAST_AA_NORMAL_TEXT = 4.5
    val CONTRAST_AA_LARGE_TEXT = 3
    val CONTRAST_AAA_NORMAL_TEXT = 7
    val CONTRAST_AAA_LARGE_TEXT = 4.5
    val CONTRAST_LARGE_SIZE_CUTOFF = 18
    val CONTRAST_LARGE_BOLD_SIZE_CUTOFF = 14

    private fun logMinimumContrast(perceptifer: Perceptifer): List<StaticIssue> {

        // First, get text color
        // NOTE: If there are multiple types of text in one rich text, they should be
        // separate perceptifers
        val foreground = perceptifer.getPerceptsOfType(PerceptType.TEXT_COLOR).firstOrNull()
        val background = perceptifer.getPerceptsOfType(PerceptType.BACKGROUND_COLOR).firstOrNull()
        val fontSize = perceptifer.getPerceptsOfType(PerceptType.FONT_SIZE).firstOrNull()
        val fontStyle = perceptifer.getPerceptsOfType(PerceptType.FONT_STYLE).firstOrNull()

        // Test for each level of WCAG 2.0 compliance
        val issues = mutableListOf<StaticIssue>()
        val sdf = listOf(foreground, background, fontSize, fontStyle)
        if (foreground != null ) {
            println("CONTRAST: $sdf")
        }
        if (listOf(foreground, background, fontSize, fontStyle).none { it == null }) {
            var foregroundColor = PerceptParser.fromColor(foreground!!)
            val backgroundColor = PerceptParser.fromColor(background!!)

            // Blend the foreground and background to get the true foreground with alpha
            foregroundColor = Color(blendColors(listOf(backgroundColor.color.toLong(), foregroundColor.color.toLong())).toInt())

            val contrast = getContrast(foregroundColor.color.toLong(), backgroundColor.color.toLong())
            println("FOUND CONTRAST OF $contrast")

            // If small text, see if fails
            val fontSizePx = PerceptParser.fromFontSize(fontSize!!)
            val isBold = PerceptParser.fromFontStyle(fontStyle!!) == FontStyle.BOLD
            val isLarge = fontSizePx >= CONTRAST_LARGE_SIZE_CUTOFF && !isBold || fontSizePx > CONTRAST_LARGE_BOLD_SIZE_CUTOFF && isBold

            if (isLarge) {

                if (contrast < CONTRAST_AA_LARGE_TEXT) {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P143_NAME, WCAGConstants.P143_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P143_LEVEL, WCAGConstants.P143_LINK))
                            .passes(false)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - for WCAG 2.0 Principle 1.4.3 compliance, a contrast of $CONTRAST_AA_LARGE_TEXT:1 is needed for compliance on large text.")
                            .suggest("Increase the contrast between the background and foreground text on this text object to at least $CONTRAST_AA_LARGE_TEXT:1.")
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                } else {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P143_NAME, WCAGConstants.P143_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P143_LEVEL, WCAGConstants.P143_LINK))
                            .passes(true)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - this satisfies WCAG 2.0 Principle 1.4.3 compliance, which requires a contrast of $CONTRAST_AA_LARGE_TEXT:1 for large text.")
                            .suggest(WCAGConstants.SUGGEST_NONE)
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                }

                if (contrast < CONTRAST_AAA_LARGE_TEXT) {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P146_NAME, WCAGConstants.P146_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P146_LEVEL, WCAGConstants.P146_LINK))
                            .passes(false)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - for WCAG 2.0 Principle 1.4.6 compliance, a contrast of $CONTRAST_AAA_LARGE_TEXT:1 is needed for compliance on large text.")
                            .suggest("Increase the contrast between the background and foreground text on this text object to at least $CONTRAST_AAA_LARGE_TEXT:1.")
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                } else {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P146_NAME, WCAGConstants.P146_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P146_LEVEL, WCAGConstants.P146_LINK))
                            .passes(true)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - this satisfies WCAG 2.0 Principle 1.4.6 compliance, which requires a contrast of $CONTRAST_AAA_LARGE_TEXT:1 for large text.")
                            .suggest(WCAGConstants.SUGGEST_NONE)
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                }

            } else {

                if (contrast < CONTRAST_AA_NORMAL_TEXT) {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P143_NAME, WCAGConstants.P143_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P143_LEVEL, WCAGConstants.P143_LINK))
                            .passes(false)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - for WCAG 2.0 Principle 1.4.3 compliance, a contrast of $CONTRAST_AA_NORMAL_TEXT:1 is needed for compliance on normal text.")
                            .suggest("Increase the contrast between the background and foreground text on this text object to at least $CONTRAST_AA_NORMAL_TEXT:1.")
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                } else {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P143_NAME, WCAGConstants.P143_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P143_LEVEL, WCAGConstants.P143_LINK))
                            .passes(true)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - this satisfies WCAG 2.0 Principle 1.4.3 compliance, which requires a contrast of $CONTRAST_AA_NORMAL_TEXT:1 for normal text.")
                            .suggest(WCAGConstants.SUGGEST_NONE)
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                }

                if (contrast < CONTRAST_AAA_NORMAL_TEXT) {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P146_NAME, WCAGConstants.P146_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P146_LEVEL, WCAGConstants.P146_LINK))
                            .passes(false)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - for WCAG 2.0 Principle 1.4.6 compliance, a contrast of $CONTRAST_AAA_NORMAL_TEXT:1 is needed for compliance on normal text.")
                            .suggest("Increase the contrast between the background and foreground text on this text object to at least $CONTRAST_AAA_NORMAL_TEXT:1.")
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                } else {
                    val builder = IssuerBuilder()
                    builder.initialize(WCAGConstants.P146_NAME, WCAGConstants.P146_SHORT,
                            WCAGConstants.P143_LONG)
                            .extras(WCAGExtras(WCAGConstants.P146_LEVEL, WCAGConstants.P146_LINK))
                            .passes(true)
                            .explanation("This text object's foreground color (#${foregroundColor.colorHex}) has a contrast against the background (#${backgroundColor.colorHex}) of $contrast:1 - this satisifes WCAG 2.0 Principle 1.4.6 compliance, which requires a contrast of $CONTRAST_AAA_NORMAL_TEXT:1 for normal text.")
                            .suggest(WCAGConstants.SUGGEST_NONE)
                            .addPerceptifers(mutableListOf(perceptifer))
                    issues.add(builder.buildStaticIssue())
                }

            }

        }

        return issues
    }

    private fun logSameScreenReaderContent(literalInterace: LiteralInterace): List<StaticIssue> {

        val contentToPerceptifers = HashMap<String, MutableList<Perceptifer>>().withDefault { mutableListOf() }

        // First collect all screen reader contents
        literalInterace.perceptifers.forEach {
            val screenReaderContent = it.getPerceptsOfType(PerceptType.VIRTUAL_SCREEN_READER_CONTENT).firstOrNull()
            if (screenReaderContent != null) {
                val content = PerceptParser.fromScreenReaderContent(screenReaderContent)
                contentToPerceptifers[content]!!.add(it)
            }
        }

        // For each content, check if there are multiple percepts with that information. If so, report an issue
        val issues = mutableListOf<StaticIssue>()
        contentToPerceptifers.forEach { t, u ->
            if (u.size > 1) {
                val builder = IssuerBuilder()
                builder.initialize(GoogleAccessibilityScannerConstants.DUP_CONTENT_NAME, GoogleAccessibilityScannerConstants.DUP_CONTENT_SHORT,
                        GoogleAccessibilityScannerConstants.DUP_CONTENT_LONG)
                        .extras(WCAGExtras(GoogleAccessibilityScannerConstants.DUP_CONTENT_LEVEL, GoogleAccessibilityScannerConstants.DUP_CONTENT_LINK))
                        .passes(false)
                        .explanation(GoogleAccessibilityScannerConstants.DUP_CONTENT_EXPLANATION)
                        .suggest(GoogleAccessibilityScannerConstants.DUP_CONTENT_SUGGESTION)
                        .addPerceptifers(u)
                issues.add(builder.buildStaticIssue())
            } else {
                val builder = IssuerBuilder()
                builder.initialize(GoogleAccessibilityScannerConstants.DUP_CONTENT_NAME, GoogleAccessibilityScannerConstants.DUP_CONTENT_SHORT,
                        GoogleAccessibilityScannerConstants.DUP_CONTENT_LONG)
                        .extras(WCAGExtras(GoogleAccessibilityScannerConstants.DUP_CONTENT_LEVEL, GoogleAccessibilityScannerConstants.DUP_CONTENT_LINK))
                        .passes(true)
                        .explanation(GoogleAccessibilityScannerConstants.DUP_CONTENT_EXPLANATION_GOOD)
                        .suggest(WCAGConstants.SUGGEST_NONE)
                        .addPerceptifers(u)
                issues.add(builder.buildStaticIssue())
            }
        }

        return issues

    }


    private fun logMinimumTouchTargetSize(perceptifer: Perceptifer): StaticIssue? {

        // Check if this element is interactive
        val interactive = perceptifer.getPerceptsOfType(PerceptType.VIRTUALLY_CLICKABLE).isNotEmpty()

        if (interactive) {

            val builder = IssuerBuilder()
            builder.initialize(WCAGConstants.P255_NAME, WCAGConstants.P255_SHORT,
                    WCAGConstants.P255_LONG)
                    .extras(WCAGExtras(WCAGConstants.P255_LEVEL, WCAGConstants.P255_LINK))

            val size = perceptifer.getPerceptsOfType(PerceptType.SIZE).firstOrNull()
            if (size != null) {
                val realSize = PerceptParser.fromSize(size)
                return if (realSize.height < 44 || realSize.width < 44) {
                    builder
                        .passes(false)
                        .explanation("This interactive element had a height of ${realSize.height} and a width of ${realSize.width}.")
                        .suggest(WCAGConstants.P255_SUGGESTION)
                        .addPerceptifers(mutableListOf(perceptifer))
                    builder.buildStaticIssue()
                } else {
                    builder
                        .passes(true)
                        .explanation("This interactive element had a height of ${realSize.height} and a width of ${realSize.width}.")
                        .suggest(WCAGConstants.SUGGEST_NONE)
                        .addPerceptifers(mutableListOf(perceptifer))
                    builder.buildStaticIssue()
                }
            }
        }

        return null

    }


    /**
     * Logs any issues resulting from a change in focus causing the context of the application to change
     * drastically. This is detected by the following process:
     *  - Remove all edges that are not focus changing - i.e. remove all but keypress tab
     *  - Ensure that the following is true:
     *      - All edges are self edges
     *       OR
     *      - If an edge is not a self edge, the only difference between the two states are in who has focus
     */
    private fun logOnFocus(automaton: Automaton<CondensedState, UserAction>): MutableList<DynamicIssue> {
        val onFocusIssues = mutableListOf<DynamicIssue>()
        automaton.transitions.forEach {
            val startState = it.key
            it.value.forEach {
                val transition = it.key
                // An issue arises when one of the destination states is not the start state
                // and when that destination state only has a difference in virtual focus
                if (transition.label.type == InputInteractionType.KEYPRESS &&
                        transition.label.parameters!!.cast<KeyPress>() == KeyPress.TAB) {
                    val badStates = it.value.filter {
                        false
                        //it != startState
                        //it.state. // TODO: Allow hash to take in percepts to track as a parameters, then re-evaluate these states without virtual percept
                                        // TODO: OORRRRR given two literal interfaces, determine if they are a change in context (should be easy?)
                    }
                    badStates.forEach {
                        val issue = IssuerBuilder()
                                .initialize(WCAGConstants.P321_NAME, WCAGConstants.P321_SHORT, WCAGConstants.P321_LONG)
                                .explanation(WCAGConstants.P321_EXPLANATION)
                                .addDynamicMapping(startState.state.literalInterace.metadata.id, transition.label, it.state.literalInterace.metadata.id)
                                .extras(WCAGExtras(WCAGConstants.P321_LEVEL, WCAGConstants.P321_LINK))
                                .passes(false)
                                .suggest(WCAGConstants.SUGGEST_NONE)
                                .buildDynamicIssue()
                        onFocusIssues.add(issue)
                    }
                }
            }
        }
        return onFocusIssues
    }

    /**
     * Logs issues results from the inability to access a state solely through the keyboard. This
     * is detected through the following process:
     *  - Remove all edges that are not keypresses
     *  - Ensure that the following is true:
     *      - All start -> destinations possible without keyboard input are still available with
     *        only keypress edges
     *          EXCEPT
     *      - If the state satisfies one of the WCAG conditions
     */
    private fun logKeyboard(automaton: Automaton<CondensedState, UserAction>): MutableList<DynamicIssue> {

        // First, get two copies of states and all of their incoming edges
        val originalStateToIncoming = automaton.getStatesAndIncomingEdges()
        val newStateToIncoming = automaton.getStatesAndIncomingEdges()

        // Remove all edges from one copy if that transition is not a keypress
        newStateToIncoming.forEach {
            it.value.removeIf { it.label.type != InputInteractionType.KEYPRESS }
        }

        // For each state, make sure that the filtered result has incoming edges for
        // each state, unless the original also did not. Also, if a state is not reachable but is
        // only unreachable due to the fact that an element now has focus, ignore that state.
        val keyboardIssues = mutableListOf<DynamicIssue>()
        newStateToIncoming.forEach {
            if (it.value.size == 0) {
                if (originalStateToIncoming[it.key]!!.size != 0) {

                    // First, ignore this state if the only difference between it and all it's resulting keyboard
                    // states is just a focus.
                    val state = it.key
                    val isJustUnfocused = automaton.transitions[state]!!.filter {
                        it.key.label.type == InputInteractionType.KEYPRESS
                    }.all {
                        it.value.all {
                            println("DIFFERENCES: ${state.state.differenceBetween(it.state)}")
                            state.state.differenceBetween(it.state).all {it.type == PerceptType.VIRTUAL_FOCUSABLE}
                        }
                    }

                    if (!isJustUnfocused) {
                        val issue = IssuerBuilder()
                                .initialize(WCAGConstants.P211_NAME, WCAGConstants.P211_SHORT, WCAGConstants.P211_LONG)
                                .explanation(WCAGConstants.P211_EXPLANATION)
                                .addDynamicMapping(null, null, it.key.state.literalInterace.metadata.id)
                                .extras(WCAGExtras(WCAGConstants.P211_LEVEL, WCAGConstants.P211_LINK))
                                .passes(false)
                                .suggest(WCAGConstants.SUGGEST_NONE)
                                .buildDynamicIssue()
                        keyboardIssues.add(issue)
                    }
                }
            }
        }

        // If no keyboard issues were found, report a pass
        if (keyboardIssues.isEmpty()) {
            val issue = IssuerBuilder()
                    .initialize(WCAGConstants.P211_NAME, WCAGConstants.P211_SHORT, WCAGConstants.P211_LONG)
                    .explanation(WCAGConstants.P211_PASS_EXPLANATION)
                    .extras(WCAGExtras(WCAGConstants.P211_LEVEL, WCAGConstants.P211_LINK))
                    .passes(true)
                    .suggest(WCAGConstants.SUGGEST_NONE)
                    .buildDynamicIssue()
            keyboardIssues.add(issue)
        }

        return keyboardIssues
    }

    /**
     * Logs any issues where use of the keyboard causes the user to become trapped in some state. This is
     * detected through the following:
     *  - Same procedure as <code>logKeyboard</code>
     */
    private fun logNoKeyboardTrap(automaton: Automaton<CondensedState, UserAction>): MutableList<DynamicIssue> {
        return mutableListOf()
    }

    /**
     * Logs any issues arising from a change in state when no user action was taken. This is detected
     * through the following:
     *  - Check that all NONE edges are self-edges
     */
    private fun logChangeOnRequest(automaton: Automaton<CondensedState, UserAction>): List<DynamicIssue> {

        val changeOnRequestIssues = mutableListOf<DynamicIssue>()
        automaton.transitions.forEach {
            val startState = it.key
            it.value.forEach {
                val transition = it.key
                // An issue arises when one of the destination states is not the start state
                if (transition.label.type == InputInteractionType.NONE) {
                    val badStates = it.value.filter { it != startState }
                    badStates.forEach {
                        val issue = IssuerBuilder()
                                .initialize(WCAGConstants.P325_NAME, WCAGConstants.P325_SHORT, WCAGConstants.P325_LONG)
                                .explanation(WCAGConstants.P325_EXPLANATION)
                                .addDynamicMapping(startState.state.literalInterace.metadata.id, transition.label, it.state.literalInterace.metadata.id)
                                .extras(WCAGExtras(WCAGConstants.P325_LEVEL, WCAGConstants.P325_LINK))
                                .passes(false)
                                .suggest(WCAGConstants.SUGGEST_NONE)
                                .buildDynamicIssue()
                        changeOnRequestIssues.add(issue)
                    }
                }
            }
        }
        return changeOnRequestIssues

    }

    private fun wasInvisible(perceptifer: Perceptifer): Boolean {

        return perceptifer.percepts!!.any {
            it.type == PerceptType.INVISIBLE && PerceptParser.fromInvisible(it)
        }

    }

    override fun getAccessibilityReportAsJson(automaton: Automaton<CondensedState, UserAction>): IssueReport {

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
                    val staticIssues = getAllIndividualStaticIssues(representative)
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
        val allStaticIssues = hashToStaticIssues.values.flatten()
        val allDynamicIssues = getAllDynamicIssues(automaton)
        println("Found ${allStaticIssues.size} static issues and ${allDynamicIssues.size} dynamic issues")
        return IssueReport(allStaticIssues, allDynamicIssues)
    }

    override fun getAccessibilityReportAsString(automaton: Automaton<CondensedState, UserAction>): String {

        val issues = getAccessibilityReportAsJson(automaton)
        val allStaticIssues = issues.staticIssues
        val allDynamicIssues = issues.dynamicIssues


        val builder = StringBuilder()
        builder.appendln("WCAG 2.0 Accessibility Report - Completed at ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}")
        builder.appendln("Unique Static Accessibility Issues")
        if (allStaticIssues.isNotEmpty()) {
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
            builder.appendln("-------------------------------------- (note that $passCount static pass events were found) ")
        } else {
            builder.appendln("No static issues found")
        }

        if (allDynamicIssues.isNotEmpty()) {
            builder.appendln("Dynamic Accessibility Issues")
            var passCount = 0
            for (issue in allDynamicIssues) {
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
                    builder.appendln("\t\t Mappings: ${issue.mappings}")
                    builder.appendln("\t\t WCAG Details: ${issue.extras}")
                }
            }
            builder.appendln("-------------------------------------- (note that $passCount dynamic pass events were found) ")
        }

        return builder.toString()


    }

}