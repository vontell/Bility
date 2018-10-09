package org.vontech.algorithms.hci

import org.vontech.core.interfaces.FuzzyState
import org.vontech.core.interfaces.LiteralInterace
import org.vontech.core.interfaces.PerceptType
import org.vontech.core.interfaces.Perceptifer

/**
 * The process for grouping similar components of user interfaces
 * (for the purposes of accessibility) is done through the following
 * steps (note we basically just separate things on dividing percepts,
 * which for accessibility means splitting percepts based on things that
 * actually affect accessibility)
 *
 *  Repeat the following for any perceptifer that does not have children:
 *      1. Grab accessibility-related percepts
 *      2. Hash these percepts, mapping this view to an ID that holds these percepts
 *      3.
 */


fun getStateFromLiteralInterface(literalInterace: LiteralInterace): FuzzyState {

    val ps = literalInterace.perceptifers



}

/**
 * Represents an accessibility-related hash of a Perceptifer, used
 * in condensed state creation. The following percepts are tracked:
 *  ALL:
 *      ALPHA
 *      BACKGROUND_COLOR
 *  TEXT:
 *      FONT_SIZE
 *      FONT_STYLE
 *      LINE_SPACING
 *      TEXT_COLOR
 *  IMAGE:
 *      (none)
 *  CONTAINER:
 *      CHILDREN_SPATIAL_RELATIONS
 */
val ACCESSIBILITY_PERCEPTS = listOf(
    PerceptType.ALPHA
)
class PerceptiferAccessibilityHash(val perceptifer: Perceptifer) {

    init {
        val percepts = perceptifer.percepts!!.filter {
            it.type in
        }
    }

}


/** Accessibility is potentially affected by the following percepts:

 */