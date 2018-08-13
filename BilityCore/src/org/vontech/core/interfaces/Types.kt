package org.vontech.core.interfaces

import java.util.*

/**
 * A collection of simple types and data classes to use when interacting
 * with a user interface.
 * @author Aaron Vontell
 */

/**
 * For the theory behind this:
 * https://docs.google.com/document/d/1ykYy_e14mvicFQs3vtW6hbfQTd5oVvBHLTJJ0kArEWU/edit
 * Last updated August 12th, 2018
 *
 */

/**
 * A Percept is a piece of information that can be perceived
 * by a user.
 */
data class Percept (
    val type: PerceptType,
    val information: Any
)

/**
 * A Perceptifer is a bearer of percepts, or things that can
 * be perceived by a user.
 */
data class Perceptifer(
    val percepts: Set<Percept>?,
    val virtualPercepts: Set<Percept>?
)

/**
 * The EmptyPerceptifer is a special, more abstract object that can be
 * referred to when a user is aware of a perceptifer, but cannot perceive it
 */
val EmptyPerceptifer = Perceptifer(null, null)

data class LiteralInterfaceMetadata(
    val timestamp: Date
)

data class LiteralInterace(
    val perceptifers: Set<Perceptifer>,
    val outputChannels: Set<OutputChannel>,
    val inputChannels: Set<InputChannel>,
    val metadata: LiteralInterfaceMetadata
)