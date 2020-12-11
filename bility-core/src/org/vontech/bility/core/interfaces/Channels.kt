package org.vontech.bility.core.interfaces

/**
 * A collection of types and classes for dealing with input and output channels between
 * a user and an interface.
 * @author Aaron Vontell
 */

/**
 * Useful references:
 *  types of senses - https://www.livescience.com/60752-human-senses.html
 */

/**
 * The type of a channel
 */
enum class ChannelType {
    VISUAL, AUDIO, PHYSICAL, OLFACTORY, GUSTATORY
}

data class OutputChannel (
    val type: ChannelType,
    val name: String
)

data class InputChannel (
    val type: ChannelType,
    val name: String
)

// Channels should be full-fledged classes with things like send and receive,
// which are parametrized.