package org.vontech.core.interfaces

import org.vontech.utils.cast
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

    // REAL PERCEPTS

    /**
     * Builds and saves a percept with text information
     * @param text The text content of this percept
     */
    fun createTextPercept(text: String): PerceptBuilder {
        return createPercept(PerceptType.TEXT, text)
    }

    // TODO: Would be great to hava a color heirarchy concept

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

    // Virtual types
    VIRTUAL_NAME,
    VIRTUAL_IDENTIFIER,
    VIRTUAL_SCREEN_READER_CONTENT,
    VIRTUALLY_CLICKABLE

}

data class Coordinate (
    val left: Int,
    val top: Int
)

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

