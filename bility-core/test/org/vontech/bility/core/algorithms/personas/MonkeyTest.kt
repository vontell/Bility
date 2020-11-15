package org.vontech.bility.core.algorithms.personas

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec

/**
 * Tests the basic operations of the Monkey type, which is
 * a user that clicks randomly.
 */
class MonkeyTest: FeatureSpec({

    feature("creating the Monkey user") {

        val monkey = Monkey("Albert")

        scenario("should have default information") {

            monkey.nickname shouldBe "Albert"
            monkey.baseType shouldBe "Monkey"

        }
    }

})