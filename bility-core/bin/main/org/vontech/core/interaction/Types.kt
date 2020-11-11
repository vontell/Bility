package org.vontech.core.interaction

import org.vontech.algorithms.hci.AccessibilityHashResults
import org.vontech.core.interfaces.Perceptifer
import org.vontech.core.interfaces.getMidpoint
import org.vontech.core.interfaces.getShortName
import org.vontech.utils.cast
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
    FOCUS,
    KEYPRESS,
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

    private var hashContext: AccessibilityHashResults? = null
    private var overrideString: String? = null

    fun provideContext(context: AccessibilityHashResults) {
        this.hashContext = context
    }

    fun overrideString(newString: String) {
        this.overrideString = newString
    }

    /**
     * equals for a user action is dependent on the verb
     * and subject of the transition
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAction

        return this.toString() == other.toString()

//        if (type != other.type) return false
//
//        if (this.hashContext != null) {
//            println("Using hashcontext")
//            if (this.hashContext!!.idsToHashes[this.perceptifer.id] != this.hashContext!!.idsToHashes[other.perceptifer.id]) {
//                return false
//            }
//        }
//        return true
    }

    override fun hashCode(): Int {
        //var result = type.hashCode()
        //result = 31 * result + perceptifer.hashCode()
        //result = 31 * result + (this.hashContext?.idsToHashes!![perceptifer.id] ?: 0)
        return toString().hashCode()
    }

    override fun toString(): String {
        if (this.overrideString != null) {
            return this.overrideString!!
        }
        if (this.hashContext == null) {
            return "$type"
        }
        return "$type on ${getShortName(perceptifer)} (${hashContext!!.idsToHashes[perceptifer.id]})"
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

val MAX_SWIPE_RANGE = 2000
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

/**
 * Attempts to reverse an action made by the Monkey.
 */
fun getBestEffortOppositeUserAction(action: UserAction): UserAction? {

    if (action.type == InputInteractionType.SWIPE) {
        val params = action.parameters?.cast<SwipeParameters>()
        if (params != null) {
            val newParams = params.copy(
                    startX = params.endX,
                    endX = params.startX,
                    startY = params.endY,
                    endY = params.startY)
            return UserAction(action.type, action.perceptifer, newParams)
        }
    }
    return null

}

enum class KeyPress {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    TAB,
    ENTER,
}