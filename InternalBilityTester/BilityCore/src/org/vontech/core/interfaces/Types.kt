package org.vontech.core.interfaces

import java.util.*
import kotlin.math.roundToInt

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
 * NOTE: It is important that equals() always goes by reference equality here
 */
class Perceptifer(val percepts: MutableSet<Percept>?,
                  val virtualPercepts: MutableSet<Percept>?) {

    val id: String = UUID.randomUUID().toString()

    var parentId: String? = null

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
    return Perceptifer(mutableSetOf(), mutableSetOf())
}

/**
 * The EmptyPerceptifer is a special, more abstract object that can be
 * referred to when a user is aware of a perceptifer, but cannot perceive it
 */
val EmptyPerceptifer = Perceptifer(null, null)

data class LiteralInterfaceMetadata(
    //val timestamp: Date
    val id: String = UUID.randomUUID().toString()
)

data class LiteralInterace(
    val perceptifers: Set<Perceptifer>,
    val outputChannels: Set<OutputChannel>,
    val inputChannels: Set<InputChannel>,
    val metadata: LiteralInterfaceMetadata
)

fun getLiteralInterfacePrettyString(literalInterace: LiteralInterace): String {
    val builder = StringBuilder()
    builder.append(literalInterace.metadata.toString() + "\n")
    literalInterace.perceptifers.forEach {
        builder.append("\tPerceptifer w/ ID ${it.id}\n")
        it.percepts!!.forEach {
            builder.append("\t\t(R) $it\n")
        }
        it.virtualPercepts!!.forEach {
            builder.append("\t\t(V) $it\n")
        }
    }
    return builder.toString()
}
/**
 * Given a perceptifer, returns the IDs of it's in-order children, or
 * an empty list if his perceptifer has no children
 */
fun getIdsOfChildren(perceptifer: Perceptifer): List<String> {
    val childrenPercepts = perceptifer.virtualPercepts!!.filter { it.type == PerceptType.CHILDREN_SPATIAL_RELATIONS}
    if (childrenPercepts.isEmpty()) return ArrayList()
    return PerceptParser.fromPerceptiferOrdering(childrenPercepts.first()).ordering
}

fun getRoot(perceptifers: Iterable<Perceptifer>): Perceptifer {
    return perceptifers.first {it.virtualPercepts!!.any { it.type == PerceptType.VIRTUAL_ROOT }}
}

fun getMidpoint(perceptifer: Perceptifer): Coordinate {
    val location = PerceptParser.fromCoordinate(perceptifer.percepts!!.first { it.type == PerceptType.LOCATION })
    val size = PerceptParser.fromSize(perceptifer.percepts.first { it.type == PerceptType.SIZE })
    val xMidpoint = (location.left + (size.width / 2.0)).roundToInt()
    val yMidpoint = (location.top + (size.height / 2.0)).roundToInt()
    return Coordinate(xMidpoint, yMidpoint)
}