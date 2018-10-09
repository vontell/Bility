package org.vontech.core.interfaces

import org.vontech.utils.cast
import java.util.HashSet
import kotlin.math.pow
import kotlin.math.sqrt

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

    fun buildPerceptifer(): Perceptifer {
        return Perceptifer(percepts, virtualPercepts)
    }

    // REAL PERCEPTS

    /**
     * Builds and saves a percept with text information
     * @param text The text content of this percept
     */
    fun createTextPercept(text: String): PerceptBuilder {
        return createPercept(PerceptType.TEXT, text)
    }

    // TODO: Would be great to hava a color heirarchy concept

    // TODO: Instead of enum type, maybe have a hash table of types
    // to a list of percepts of that type

    fun createTextColorPercept(color: Int): PerceptBuilder {
        return createPercept(PerceptType.TEXT_COLOR, Color(color))
    }

    fun createBackgroundColorPercept(color: Int): PerceptBuilder {
        return createPercept(PerceptType.BACKGROUND_COLOR, Color(color))
    }

    fun createLocationPercept(left: Int, top: Int): PerceptBuilder {
        return createPercept(PerceptType.LOCATION, Coordinate(left, top))
    }

    fun createSizePercept(width: Int, height: Int): PerceptBuilder {
        return createPercept(PerceptType.SIZE, Size(width, height))
    }

    fun createScrollProgressPercept(progress: Float): PerceptBuilder {
        return createPercept(PerceptType.SCROLL_PROGRESS, progress)
    }

    fun createAlphaPercept(alpha: Float): PerceptBuilder {
        return createPercept(PerceptType.ALPHA, alpha)
    }

    fun createFontSizePercept(size: Float): PerceptBuilder {
        return createPercept(PerceptType.FONT_SIZE, size)
    }

    fun createFontFamilyPercept(name: String): PerceptBuilder {
        return createPercept(PerceptType.FONT_FAMILY, name)
    }

    fun createFontKerningPercept(kerning: Float): PerceptBuilder {
        return createPercept(PerceptType.FONT_KERNING, kerning)
    }

    fun createLineSpacingPercept(spacing: Float): PerceptBuilder {
        return createPercept(PerceptType.LINE_SPACING, spacing)
    }

    fun createFontStylePercept(style: FontStyle): PerceptBuilder {
        return createPercept(PerceptType.FONT_STYLE, style)
    }

    fun createPhysicalButtonPercept(buttonIdentifier: Int, name: String): PerceptBuilder {
        return createPercept(PerceptType.PHYSICAL_BUTTON, PhysicalButton(buttonIdentifier, name))
    }

    fun createMediaTypePercept(mediaType: MediaType): PerceptBuilder {
        return createPercept(PerceptType.MEDIA_TYPE, mediaType)
    }

    fun createIsPurelyContainer(): PerceptBuilder {
        return createPercept(PerceptType.INVISIBLE, true)
    }

    fun createPercept(type: PerceptType, info: Any): PerceptBuilder {
        val percept = Percept(type, info)
        percepts.add(percept)
        return this
    }

    // VIRTUAL PERCEPTS

    fun createClickableVirtualPercept(clickable: Boolean): PerceptBuilder {
        return createVirtualPercept(PerceptType.VIRTUALLY_CLICKABLE, clickable)
    }

    fun createIdentifierVirtualPercept(identifier: Any): PerceptBuilder {
        return createVirtualPercept(PerceptType.VIRTUAL_IDENTIFIER, identifier)
    }

    fun createNameVirtualPercept(name: String): PerceptBuilder {
        return createVirtualPercept(PerceptType.VIRTUAL_NAME, name)
    }

    fun createScreenReaderContentVirtualPercept(content: String): PerceptBuilder {
        return createVirtualPercept(PerceptType.VIRTUAL_SCREEN_READER_CONTENT, content)
    }

    fun createRoughViewOrderingPercept(perceptifers: List<Perceptifer>): PerceptBuilder {

        // First, get all perceptifers that have a location
        val displayed = perceptifers.filter{
            it.percepts?.any { it.type == PerceptType.LOCATION } ?: false
        }

        // Then sort by distance to 0, 0 (top left of screen)
        val sortedResult = displayed.sortedBy {
            val location = PerceptParser.fromCoordinate(it.percepts!!.first { it.type == PerceptType.LOCATION})
            location.fromOrigin()
        }

        // Finally, store the information
        val information = PerceptiferOrdering(sortedResult)
        return createVirtualPercept(PerceptType.CHILDREN_SPATIAL_RELATIONS, information)

    }

    fun createVirtualPercept(type: PerceptType, info: Any): PerceptBuilder {
        val percept = Percept(type, info)
        virtualPercepts.add(percept)
        return this
    }

}

class PerceptParser {

    companion object {

        fun fromText(percept: Percept): String {
            return percept.information.cast()
        }

        fun fromInvisible(percept: Percept): Boolean {
            return percept.information.cast()
        }

        fun fromCoordinate(percept: Percept): Coordinate {
            return percept.information.cast()
        }

    }

}

enum class PerceptType {

    // Real types
    TEXT,
    LOCATION,
    SIZE,
    TEXT_COLOR,
    BACKGROUND_COLOR,
    SCROLL_PROGRESS,
    ALPHA,
    FONT_SIZE,
    FONT_FAMILY,
    FONT_KERNING,
    LINE_SPACING,
    FONT_STYLE,
    PHYSICAL_BUTTON,
    MEDIA_TYPE,
    INVISIBLE,
    CHILDREN_SPATIAL_RELATIONS,

    // Virtual types
    VIRTUAL_NAME,
    VIRTUAL_IDENTIFIER,
    VIRTUAL_SCREEN_READER_CONTENT,
    VIRTUALLY_CLICKABLE

}

class Coordinate (val left: Int, val top: Int) {

    fun fromOrigin(): Float {
        return sqrt(left.toFloat().pow(2) + top.toFloat().pow(2))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinate

        if (left != other.left) return false
        if (top != other.top) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left
        result = 31 * result + top
        return result
    }
}

data class Size (
    val width: Int,
    val height: Int
)

enum class FontStyle {
    NORMAL, ITALIC, BOLD
}

enum class MediaType {
    IMAGE, AUDIO, TASTE, TEXTURE
}

data class PhysicalButton (
    val keycode: Int,
    val name: String
)

class Color(val color: Int) {

    fun getHexString(): String {
        return Integer.toHexString(color)
    }

    override fun toString(): String {
        return "Color(hex=${this.getHexString()})"
    }

}

class PerceptiferOrdering(orderedPerceptifers: List<Perceptifer>) {

    val ordering = orderedPerceptifers.map { it.id }

}

