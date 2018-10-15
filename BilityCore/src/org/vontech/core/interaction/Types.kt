package org.vontech.core.interaction

import org.vontech.core.interfaces.Perceptifer
import org.vontech.core.interfaces.getMidpoint
import java.util.*
import kotlin.math.roundToInt

/**
 * A collection of simple types and data classes to use when interacting
 * with a user interface.
 * @author Aaron Vontell
 * @date August 7th, 2018
 *
 */

/**
 * An InputInteractionType is a basic type of interaction that can
 * be imparted onto a user interface. In other words, what forms of
 * input can a user interface receive?
 * Note that these can later be restricted based on the type of
 * user interface
 */
enum class InputInteractionType {
    CLICK,
    LONGCLICK,
    SWIPE,
    KEY,
    PHYSICAL_BUTTON,
    SHAKE,
    SPEECH,
    IMAGE,
    SQUEEZE,
    NONE,
    QUIT,
}

class UserAction(
    val type: InputInteractionType,
    val perceptifer: Perceptifer,
    val parameters: Any? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAction

        if (type != other.type) return false
        //if (perceptifer != other.perceptifer) return false
        //if (parameters != other.parameters) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        //result = 31 * result + perceptifer.hashCode()
        //result = 31 * result + (parameters?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "$type"
    }

}

/**
 * An OutputInteractionType is a basic type of interaction that can
 * be imparted onto a user from an interface. In other words, what
 * forms of feedback does a user receive?
 * Note that these can later be restricted based on the type of
 * user interface.
 */
enum class OutputInteractionType {
    VISUAL_SCREEN,
    VISUAL_DEVICE,
    AUDIO,
    VIBRATION,
    BACKGROUND
}

data class UIState (
    val name: String?
)

//data class InputEvent (
//
//)
//
//data class OutputEvent (
//
//)

// Parameters for various input types

data class SwipeParameters(
        val startX: Int,
        val startY: Int,
        val endX: Int,
        val endY: Int
)

val MAX_SWIPE_RANGE = 1000
val MIN_SWIPE_AMOUNT = 300

fun generateRandomSwipe(perceptifer: Perceptifer): SwipeParameters {
    val start = getMidpoint(perceptifer)
    val amount = (Random().nextFloat() * MAX_SWIPE_RANGE + MIN_SWIPE_AMOUNT).roundToInt()
    val direction = Random().nextInt(12)

    return when {
        direction == 0 -> SwipeParameters(start.left, start.top, start.left + amount, start.top)
        direction == 1 -> SwipeParameters(start.left, start.top, start.left - amount, start.top)
        direction < 6 -> SwipeParameters(start.left, start.top, start.left, start.top  + amount)
        else -> SwipeParameters(start.left, start.top, start.left, start.top - amount)
    }
}