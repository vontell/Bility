package org.vontech.core.config

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
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

            val selectedMap = loginScreen.selectStates(loginAndHomePage)

            // We should find that the login screen was selected
            selectedMap.size shouldBe 1
            selectedMap.keys.toList()[0].first shouldBe loginTextSelector
            selectedMap.values.toList()[0]
                    .toList()[0]
                    .state.literalInterace.perceptifers.toList()[0]
                    .percepts!!.filter { it.type == PerceptType.TEXT && it.information == "Login"}
                    .size shouldBe 1

        }
        scenario("should be able to get no selections back when not enough") {

            val loginTextSelector = PerceptiferSelector(
                    has = listOf(
                            Percept(PerceptType.TEXT, "Login")
                    ),
                    omits = listOf(
                            Percept(PerceptType.TEXT, "Main Screen")
                    )
            )

            val loginScreen = SpecificationState().hasAtLeast(2, loginTextSelector)
            val loginAndHomePage = getLoginAndHomePageAutomaton()

            val selectedMap = loginScreen.selectStates(loginAndHomePage)

            // We should find that the login screen was selected
            selectedMap.size shouldBe 0

        }
        scenario("should be able to get no selections back when ommitted") {

            val loginTextSelector = PerceptiferSelector(
                    has = listOf(
                            Percept(PerceptType.TEXT, "Login")
                    ),
                    omits = listOf(
                            Percept(PerceptType.TEXT, "Main Screen"),
                            Percept(PerceptType.TEXT, "Login")
                    )
            )

            val loginScreen = SpecificationState().hasExactly(1, loginTextSelector)
            val loginAndHomePage = getLoginAndHomePageAutomaton()

            val selectedMap = loginScreen.selectStates(loginAndHomePage)

            // We should find that the login screen was selected
            selectedMap.size shouldBe 0

        }

    }

})