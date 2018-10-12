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
class Percept (
    val type: PerceptType,
    val information: Any
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Percept

        if (type != other.type) return false
        if (information != other.information) return false

        return true
    }

    /**
     * This is the IntelliJ-generated hash for this object
     */
    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + information.hashCode()
        return result
    }

    override fun toString(): String {
        return "Percept(type=$type, info=$information)"
    }
}

val gen = Random()

/**
 * A Perceptifer is a bearer of percepts, or things that can
 * be perceived by a user.
 */
class Perceptifer(val percepts: Set<Percept>?,
                  val virtualPercepts: Set<Percept>?) {

    val id: Long = gen.nextLong()

    /**
     * Returns all percepts (real and virtual) that match the given
     * Percept type.
     */
    fun getPerceptsOfType(pType: PerceptType): Set<Percept> {
        val found = percepts!!.filter { it.type == pType }.toMutableSet()
        found.addAll(virtualPercepts!!.filter { it.type == pType })
        return found
    }

}

/**
 * Returns an empty perceptifer, useful for wait or NOOP
 */
fun emptyPerceptifer(): Perceptifer {
    return Perceptifer(setOf(), setOf())
}

/**
 * The EmptyPerceptifer is a special, more abstract object that can be
 * referred to when a user is aware of a perceptifer, but cannot perceive it
 */
val EmptyPerceptifer = Perceptifer(null, null)

data class LiteralInterfaceMetadata(
    //val timestamp: Date
    val id: Long
)

data class LiteralInterace(
    val perceptifers: Set<Perceptifer>,
    val outputChannels: Set<OutputChannel>,
    val inputChannels: Set<InputChannel>,
    val metadata: LiteralInterfaceMetadata
)