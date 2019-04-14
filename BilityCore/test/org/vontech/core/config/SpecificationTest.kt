package org.vontech.core.config

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import org.vontech.algorithms.rulebased.loggers.WCAG2IssuerLogger
import org.vontech.algorithms.rulebased.loggers.WCAGLevel
import org.vontech.core.interfaces.Percept
import org.vontech.core.interfaces.PerceptType
import org.vontech.getLoginAndHomePageAutomaton

class DataTypesTest: FeatureSpec({

    feature("the Specification interface") {
        scenario("should behave correctly with a basic example") {

            val spec = Specification()
            val wcagIssues = WCAG2IssuerLogger(WCAGLevel.AA)

            // Pass in loggers for detecting certain issues
            spec.testFor(wcagIssues)

            // A state is defined as a set of constraints on Perceptifer selectors
            //val showingImagesButton =

        }
    }

    feature("the specification selector") {
        scenario("should be able to detect a selector") {

            val loginTextSelector = PerceptiferSelector(
                has = listOf(
                    Percept(PerceptType.TEXT, "Login")
                ),
                omits = listOf(
                    Percept(PerceptType.TEXT, "Main Screen")
                )
            )

            val loginScreen = SpecificationState().hasExactly(1, loginTextSelector)
            val loginAndHomePage = getLoginAndHomePageAutomaton()

            loginScreen.existsIn(loginAndHomePage) shouldBe true

        }
    }

})