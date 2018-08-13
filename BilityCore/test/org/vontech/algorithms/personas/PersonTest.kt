package org.vontech.algorithms.personas

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec

/**
 * Tests the basic operations of the core Person type, which is
 * an inactive user.
 */
class PersonTest: FeatureSpec({

    feature("creating the Person user") {

        val person = Person("John Doe")

        scenario("should have default information") {

            person.nickname shouldBe "John Doe"
            person.baseType shouldBe "Person"

        }
    }

})