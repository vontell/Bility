package org.vontech.core.interaction

import org.vontech.core.interfaces.Perceptifer

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

data class UserAction(
    val type: InputInteractionType,
    val perceptifer: Perceptifer,
    val parameters: Any? = null
)

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