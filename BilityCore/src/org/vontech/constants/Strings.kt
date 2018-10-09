package org.vontech.constants

import org.vontech.algorithms.rulebased.loggers.WCAGLevel

/**
 * A collection of strings / constants
 * @author Aaron Vontell
 */

object WCAGConstants {

    val HOMEPAGE = "https://www.w3.org/TR/WCAG20/"

    val SUGGEST_NONE = "No changes needed."

    val P111_NAME = "WCAG 2.0 - 1.1.1 Non-text Content"
    val P111_SHORT = "Non-text content has text alternatives for screen readers."
    val P111_LONG = "All non-text content that is presented to the user has a text alternative that serves the equivalent purpose, except for certain situations such as with time-based media ."
    val P111_LINK = "https://www.w3.org/TR/UNDERSTANDING-WCAG20/text-equiv-all.html"
    val P111_LEVEL = WCAGLevel.A
    val P111_TEXT_EXPLANATION = "This UI component is composed of text, and can be read by a screen reader."
    val P111_INV_EXPLANATION = "This UI component is invisible and does not need alternative text."
    val P111_SCREEN_READER_AVAIL_EXPLANATION = "This UI component contains attributes that can be read by a screen reader."
    val P111_SCREEN_READER_GONE_EXPLANATION = "This UI component does not have any attributes that can be read by the screen reader."

}