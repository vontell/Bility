package org.vontech.algorithms.hci

import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.shouldEqual
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.FeatureSpec
import org.vontech.core.interfaces.EmptyPerceptifer
import org.vontech.core.interfaces.FontStyle
import org.vontech.core.interfaces.PerceptBuilder

class SimilarityAlgosTest: FeatureSpec({


    feature("the similarity of ui components") {

        scenario("should satisfy basic hashing requirements") {

            val perceptiferOne = PerceptBuilder()
                    .createAlphaPercept(1.0f)
                    .createFontStylePercept(FontStyle.BOLD)
                    .createFontKerningPercept(1.7f)
                    .createTextPercept("Written by Aaron Vontell")
                    .buildPerceptifer()

            val perceptiferTwo = PerceptBuilder()
                    .createAlphaPercept(1.0f)
                    .createFontStylePercept(FontStyle.BOLD)
                    .createFontKerningPercept(1.7f)
                    .createTextPercept("Written by Bob Smith")
                    .buildPerceptifer()

            val perceptiferThree = PerceptBuilder()
                    .createAlphaPercept(1.0f)
                    .createFontStylePercept(FontStyle.NORMAL) // Differs by boldness
                    .createFontKerningPercept(1.7f)
                    .createTextPercept("Written by Bob Smith")
                    .buildPerceptifer()

            val hashOne = PerceptiferAccessibilityHash(perceptiferOne)
            val hashTwo = PerceptiferAccessibilityHash(perceptiferTwo)
            val hashThree = PerceptiferAccessibilityHash(perceptiferThree)

            println(hashOne.uiHash)
            println(hashTwo.uiHash)
            println(hashThree.uiHash)

            // Assert that the correct hashes are equal to each other
            hashOne shouldBe hashTwo
            hashTwo shouldBe hashOne
            hashOne shouldNotBe hashThree
            hashTwo shouldNotBe hashThree

        }

        scenario("should satisfy edge case hashing requirements") {

            val perceptiferOne = PerceptBuilder().buildPerceptifer()

            val perceptiferTwo = EmptyPerceptifer

            val perceptiferThree = PerceptBuilder()
                    .createTextPercept("Written by Bob Smith")
                    .buildPerceptifer()

            val hashOne = PerceptiferAccessibilityHash(perceptiferOne)
            val hashTwo = PerceptiferAccessibilityHash(perceptiferTwo)
            val hashThree = PerceptiferAccessibilityHash(perceptiferThree)

            println(hashOne.uiHash)
            println(hashTwo.uiHash)
            println(hashThree.uiHash)

            // Assert that the correct hashes are equal to each other
            hashOne shouldBe hashTwo
            hashTwo shouldBe hashThree
            hashOne shouldBe hashThree

        }

    }

})