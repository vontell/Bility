package org.vontech.algorithms.hci

/**
 * Returns the contrast of the given foreground and background colors, ranging from 1 to 21
 * Uses resources from W3 in calculations, especially from the following links:
 * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
 * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
 * @param colorForeground The color in front of the background, in the format 0xAARRGGBB
 * @param colorBackground The color of the background, in the format 0xAARRGGBB
 */
fun getContrast(colorForeground: Long, colorBackground: Long): Double {

    // Get the RGB values of each color
    // This integer represents 0xAARRGGBB
    val oneARGB = longArrayOf((0xFF000000L and colorForeground) shr 24, (0x00FF0000L and colorForeground) shr 16, (0x0000FF00L and colorForeground) shr 8, (0x000000FFL and colorForeground))
    val twoARGB = longArrayOf((0xFF000000L and colorBackground) shr 24, (0x00FF0000L and colorBackground) shr 16, (0x0000FF00L and colorBackground) shr 8, (0x000000FFL and colorBackground))

    // Calculation for relative luminance, as defined by https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
    val oneL = calcLuminance(oneARGB)
    val twoL = calcLuminance(twoARGB)

    return if (oneL > twoL) (oneL + 0.05) / (twoL + 0.05) else (twoL + 0.05) / (oneL + 0.05)

}

/**
 * Calculates the luminance of an ARGB color, as given by the equations at
 * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
 * @param comps The ARGB components of the color (from 0 to 255)
 * comps[0] = alpha, [1] = red, [2] = green, [3] = blue
 * @return the luminance to be used in contrast calculations
 */
fun calcLuminance(comps: LongArray): Float {

    val RSRGB = comps[1] / 255f
    val GSRGB = comps[2] / 255f
    val BSRGB = comps[3] / 255f
    val R = if (RSRGB <= 0.03928) RSRGB / 12.92f else Math.pow((RSRGB + 0.055) / 1.055f, 2.4).toFloat()
    val G = if (GSRGB <= 0.03928) GSRGB / 12.92f else Math.pow((GSRGB + 0.055) / 1.055f, 2.4).toFloat()
    val B = if (BSRGB <= 0.03928) BSRGB / 12.92f else Math.pow((BSRGB + 0.055) / 1.055f, 2.4).toFloat()

    return 0.2126f * R + 0.7152f * G + 0.0722f * B

}