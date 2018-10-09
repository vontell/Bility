package org.vontech.core.interfaces

/**
 * The DiffState file contains logic and classes for dealing with the "UI change"
 * model, in which the state of a user interface is defined by changes from
 * a previous state (i.e. the gain and loss of information). Most important
 * is the FuzzyDiffState, which has the following properties:
 * - A change in static information is considered a change in MetaState
 * - A change in dynamic information is not considered a change in MetaState
 *
 * For instance, a screen that has a toggle button and a scroll view. The scroll view
 * is populated dynamically with cards, each which has different content.
 *
 * - The user starts in a start MetaState
 * - The user scrolls - dynamically generated data changes, so the user stays in the
 *   same MetaState
 * - The user clicks the button, and is now in a different MetaState.
 * - The user scrolls, only dynamic information changes, same MetaState
 *
 * How does this work?
 *  Dynamic information needs to be condensed into one tangible piece
 *  of user interface
 *
 *  Example: BUTTON, dITEM, dITEM, dITEM = Button, dITEM group
 *
 *  dynamic items are grouped together based on the following:
 *
 *      1. Receive all perceptifers
 *      2. Group into items that have the same layout
 *      3. Split groups if non-media items are different
 *          (??)
 *      4. Use this set as the state
 *          Make sure things stay in order (group order
 */

class FuzzyState(literalInterace: LiteralInterace) {



    init {

    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

}