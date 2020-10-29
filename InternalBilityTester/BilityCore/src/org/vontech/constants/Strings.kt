package org.vontech.constants

import org.vontech.algorithms.rulebased.loggers.WCAGLevel

/**
 * A collection of strings / constants
 * @author Aaron Vontell
 */

object WCAGConstants {

    const val HOMEPAGE = "https://www.w3.org/TR/WCAG20/"

    const val SUGGEST_NONE = "No changes needed."

    const val P111_NAME = "WCAG 2.0 - 1.1.1 Non-text Content"
    const val P111_SHORT = "Non-text content has text alternatives for screen readers."
    const val P111_LONG = "All non-text content that is presented to the user has a text alternative that serves the equivalent purpose, except for certain situations such as with time-based media ."
    const val P111_LINK = "https://www.w3.org/TR/UNDERSTANDING-WCAG20/text-equiv-all.html"
    val P111_LEVEL = WCAGLevel.A
    const val P111_TEXT_EXPLANATION = "This UI component is composed of text, and can be read by a screen reader."
    const val P111_INV_EXPLANATION = "This UI component is invisible and does not need alternative text."
    const val P111_IMG_FAIL_EXPLANATION = "This UI component is an image, but does not have any screen reader information attached."
    const val P111_SCREEN_READER_AVAIL_EXPLANATION = "This UI component alternative text information that can be read by a screen reader."
    const val P111_SCREEN_READER_GONE_EXPLANATION = "This UI component does not have any attributes that can be read by the screen reader."

    const val P143_NAME = "WCAG 2.0 - 1.4.3 Contrast (Minimum)"
    const val P143_SHORT = "The visual presentation of text and images of text has a contrast ratio of at least 7:1"
    const val P143_LONG = "Text and images of text must meet the required contrast level of 7:1 for WCAG 2.0 against their background. A contrast of 4.5:1 is required of larger text, and text used purely as decoration or as logotypes can be ignored."
    const val P143_LINK = "https://www.w3.org/TR/UNDERSTANDING-WCAG20/visual-audio-contrast-contrast.html"
    val P143_LEVEL = WCAGLevel.AA

    const val P146_NAME = "WCAG 2.0 - 1.4.6 Contrast (Enhanced)"
    const val P146_SHORT = "The visual presentation of text and images of text has a contrast ratio of at least 4.5:1"
    const val P146_LONG = "Text and images of text must meet the required contrast level of 4.5:1 for WCAG 2.0 against their background. A contrast of 3:1 is required of larger text, and text used purely as decoration or as logotypes can be ignored."
    const val P146_LINK = "https://www.w3.org/TR/UNDERSTANDING-WCAG20/visual-audio-contrast-contrast.html"
    val P146_LEVEL = WCAGLevel.AAA


    const val P211_NAME = "WCAG 2.0 - 2.1.1 Keyboard"
    const val P211_SHORT = "All functionality of the content is operable through a keyboard interface."
    const val P211_LONG = "All functionality of the content is operable through a keyboard interface without requiring specific timings for individual keystrokes, except where the underlying function requires input that depends on the path of the user's movement and not just the endpoints."
    const val P211_LINK = "http://www.w3.org/TR/UNDERSTANDING-WCAG20/keyboard-operation-keyboard-operable.html"
    val P211_LEVEL = WCAGLevel.A
    const val P211_EXPLANATION = "The given state is not reachable through any edges when only using the keyboard."
    const val P211_PASS_EXPLANATION = "All found functionality of the application can be completed through keyboard input."

    const val P255_NAME = "WCAG 2.1 - 2.5.5 Target Size"
    const val P255_SHORT = "The size of the target for pointer inputs is at least 44 by 44 pixels."
    const val P255_LONG = "The size of the target for pointer inputs is at least 44 by 44 pixels."
    const val P255_LINK = "https://www.w3.org/WAI/WCAG21/Understanding/target-size.html"
    val P255_LEVEL = WCAGLevel.AAA
    const val P255_SUGGESTION = "Increase the size of this element to at least 44 pixels in height and width. Try increasing padding to maintain a similar look while expanding the click boundaries of this element."

    const val P321_NAME = "WCAG 2.0 - 3.2.1 On Focus"
    const val P321_SHORT = "When any component receives focus, it does not initiate a change of context."
    const val P321_LONG = "When any component receives focus, it does not initiate a change of context. Opening a new window, moving focus to a different component, going to a new page, or significantly re-arranging the content of a page are examples of changes of context."
    const val P321_LINK = "https://www.w3.org/TR/UNDERSTANDING-WCAG20/consistent-behavior-receive-focus.html"
    val P321_LEVEL = WCAGLevel.A
    const val P321_EXPLANATION = "A change in focus on this state resulted in a new state, and possibly a new context"

    const val P325_NAME = "WCAG 2.0 - 3.2.5 Change on Request"
    const val P325_SHORT = "Changes of context are initiated only by user request."
    const val P325_LONG = "Changes of context are initiated only by user request. Opening a new window, moving focus to a different component, going to a new page, or significantly re-arranging the content of a page are examples of changes of context."
    const val P325_LINK = "http://www.w3.org/TR/UNDERSTANDING-WCAG20/consistent-behavior-no-extreme-changes-context.html"
    val P325_LEVEL = WCAGLevel.AAA
    const val P325_EXPLANATION = "These two states are different, but no action was taken by the user to switch between these states."

}

object GoogleAccessibilityScannerConstants {

    const val DUP_CONTENT_NAME = "Google - Duplicate descriptions"
    const val DUP_CONTENT_SHORT = "Elements on the same screen should not have the same screen reader description."
    const val DUP_CONTENT_LONG = "Elements on the same screen should not have the same screen reader description. If a user interface contains multiple elements with the same text label, it can be difficult for the user to distinguish between distinct elements."
    const val DUP_CONTENT_LINK = "https://support.google.com/accessibility/android/answer/7102513"
    val DUP_CONTENT_LEVEL = WCAGLevel.A
    const val DUP_CONTENT_EXPLANATION = "Multiple elements on this page share the same screen reader descriptions. See the highlighted element as an example."
    const val DUP_CONTENT_SUGGESTION = "Make sure that each element on a screen has a different content description. When elements are repeated due to, say, a RecyclerView, make sure each repeated screen reader description is parameterized by that specific item."
    const val DUP_CONTENT_EXPLANATION_GOOD = "This element has a unique screen reader description."

}

const val FILE_DB = "/Users/vontell/Documents/Bility/AndroidServer/fileDB"
//val FILE_DB = "/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB"