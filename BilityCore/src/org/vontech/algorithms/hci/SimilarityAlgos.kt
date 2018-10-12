package org.vontech.algorithms.hci

import org.vontech.core.interfaces.*

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


fun getAccessibilityStateFromLiteralInterface(literalInterace: LiteralInterace): FuzzyState {

    val ps = literalInterace.perceptifers

    val idsToHash = HashMap<Long, Long>()

    // 0. Create a map of IDs -> (Perceptifers, Int) by traversing through the perceptifers.
    // The Int is the depth of that element. First find the root (the only item with CHILDREN_SPATIAL_RELATIONS
    // but no one has pointers to them.

    // 1. Sort the Perceptifers by depth, call this queue G

    // 2. Initial a map M of hash(Long) -> [Perceptifers]

    // 3. For each p in G, hash and append to corresponding entry in M

    // 4.



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
        PerceptType.ALPHA,
        PerceptType.BACKGROUND_COLOR,
        PerceptType.FONT_SIZE,
        PerceptType.FONT_STYLE,
        PerceptType.LINE_SPACING,
        PerceptType.TEXT_COLOR,
        PerceptType.CHILDREN_SPATIAL_RELATIONS
)

class PerceptiferAccessibilityHash(val perceptifer: Perceptifer) {

    var percepts: List<Percept> = perceptifer.percepts?.filter {
        it.type in ACCESSIBILITY_PERCEPTS
    } ?: listOf()

    /**
     * As an additional step, there may be a conversion of some percepts into another
     * percept, to allow for fuzzy matching (for instance, using a Location object
     * to make sure that the left alignment is tracked, but not top position)
     */
    init {

    }

    private var uiHash: Long? = null

    /**
     * The definition of as hash for a set is to add the hash codes
     * of each individual Percept. If order does matter, this can be
     * an issue.
     */
    private fun _getHash(): Long {

        // First

        return percepts.hashCode().toLong()
    }

    override fun equals(other: Any?): Boolean {
        if (other is PerceptiferAccessibilityHash) {
            return other.uiHash == this.uiHash
        }
        return false
    }

}