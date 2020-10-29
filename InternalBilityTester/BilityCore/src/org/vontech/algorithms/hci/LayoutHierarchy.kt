package org.vontech.algorithms.hci

import org.vontech.core.interfaces.Perceptifer

/**
 * Layout hierarchy defines a way to describe the position of
 * element within another element.
 */

/**
 * Enum that describes the direction of a UI element.
 * TODO: Add every option as given within https://developer.android.com/reference/android/widget/RelativeLayout
 * @author Aaron Vontell
 */
enum class LayoutDirection {
    ABOVE, BELOW, LEFT_OF, RIGHT_OF
}


/**
 * A hierarchy that does not pay attention to padding, margin, or
 * size parameters of a layout, and rather cares about the
 * direction and position of elements relative to each other
 * in a directional matter. For instance, Rather than recording whether
 * one text box is below another text box with an 8 pixel margin, we simply
 * care that the text box is below the other text box.
 */
class LazyLayoutHierarchy {

    // A map of Perceptifer IDs -> Perceptifer
    private val knownPerceptifers = mutableMapOf<String, Perceptifer>()

    // Definitions of layout hierarchy
    private val relations = mutableListOf<Triple<String, String, LayoutDirection>>()

    /**
     * Adds a perceptifer to this layout, but without any
     * constraints yet.
     * @param perceptifer The perceptifer to add to this interface
     * @return The ID of the added perceptifer
     */
    fun addUnconstrained(perceptifer: Perceptifer): String {
        knownPerceptifers.put(perceptifer.id, perceptifer)
        return perceptifer.id
    }

    /**
     * Adds a constrained perceptifer, which is equivalent to the following
     * statement:
     *  'newPerceptifer' is 'direction' 'existingPerceptifer'
     *  i.e. textBox2 is BELOW textBox1
     * @param newPerceptifer The first perceptifer to constrain
     * @param direction The direction from existingPerceptifer to newPerceptifer
     * @param existingPerceptifer The second perceptifer to constrain
     */
    fun addConstrained(newPerceptifer: Perceptifer,
                       direction: LayoutDirection,
                       existingPerceptifer: Perceptifer) {

        relations.add(Triple(newPerceptifer.id, existingPerceptifer.id, direction))

        // Add Perceptifers if not already there
        if (!knownPerceptifers.contains(newPerceptifer.id)) {
            addUnconstrained(newPerceptifer)
        }
        if (!knownPerceptifers.contains(existingPerceptifer.id)) {
            addUnconstrained(existingPerceptifer)
        }

    }

}