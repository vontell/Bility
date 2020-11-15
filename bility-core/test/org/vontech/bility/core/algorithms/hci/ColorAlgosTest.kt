package org.vontech.bility.core.algorithms.hci

import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec

class ColorAlgosTest: FeatureSpec({

    fun contrastTest(foreground: Long, background: Long, contrast: Double): Triple<Long, Long, Double> {
        return Triple(foreground, background, contrast)
    }

    feature("the color algorithm utils") {
        scenario("should calculate correct contrasts") {

            val examples = listOf(
                    contrastTest(0x00FF00, 0xFFFFFF, 1.37),
                    contrastTest(0xFFFFFF, 0xFFFFFF, 1.00),
                    contrastTest(0x000000, 0xFFFFFF, 21.00),
                    contrastTest(0xFFFFFF, 0x000000, 21.00),
                    contrastTest(0x78110B, 0x00DF25, 6.15)

                    // Also try some colors with alpha
                    //contrastTest(0xf0f0f0f0, 0x00DF25, 7.86)
            )

            examples.forEach {
                val calcContrast = getContrast(it.first, it.second)
                calcContrast shouldBe it.third.plusOrMinus(0.01)
            }

        }
    }

})