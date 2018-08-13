package org.vontech.core.interfaces

import java.util.HashSet

/**
 * A collection of supporting types for Percepts, such as coordinates, colors,
 * etc... These are a mix of data classes and actual classes
 * @author Aaron Vontell
 */

class PerceptBuilder {

    private val percepts: MutableSet<Percept> = HashSet()
    private val virtualPercepts: MutableSet<Percept> = HashSet()

    fun buildPercepts(): Set<Percept> {
        return percepts
    }

    fun buildVirtualPercepts(): Set<Percept> {
        return virtualPercepts
    }

    /**
     * Builds and saves a percept with text information
     * @param text The text content of this percept
     */
    fun createTextPercept(text: String): PerceptBuilder {
        return createPercept(PerceptType.TEXT, text)
    }

    fun createLocationPercept(left: Int, top: Int): PerceptBuilder {
        return createPercept(PerceptType.LOCATION, Coordinate(left, top))
    }

    fun createSizePercept(width: Int, height: Int): PerceptBuilder {
        return createPercept(PerceptType.SIZE, Size(width, height))
    }

    fun createPercept(type: PerceptType, info: Any): PerceptBuilder {
        val percept = Percept(type, info)
        percepts.add(percept)
        return this
    }

}

enum class PerceptType {
    TEXT,
    LOCATION,
    SIZE
}

data class Coordinate (
    val left: Int,
    val top: Int
)

data class Size (
    val width: Int,
    val height: Int
)

