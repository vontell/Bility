package io.github.ama_csail.ama.util;

/**
 * Enumerated type for various contrast levels. Note that large text is defined
 * as text that is larger than 14pt and bold, or larger than 18pt.
 * Source: https://webaim.org/resources/contrastchecker/
 * @author Aaron Vontell
 */
public enum Contrast {

    WCAG_AA_LARGE_TEXT (3d),
    WCAG_AA_NORMAL_TEXT (4.5d),
    WCAG_AAA_LARGE_TEXT (4.5d),
    WCAG_AAA_NORMAL_TEXT (7d),
    DEFAULT (4.5d);

    private final double ratio;

    Contrast(double ratio) {
        this.ratio = ratio;
    }

    public double ratio() {
        return this.ratio;
    }
}
