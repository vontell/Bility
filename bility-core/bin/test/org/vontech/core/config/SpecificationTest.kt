package org.vontech.core.config

import io.kotlintest.specs.FeatureSpec
import org.vontech.algorithms.rulebased.loggers.WCAG2IssuerLogger
import org.vontech.algorithms.rulebased.loggers.WCAGLevel

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

})