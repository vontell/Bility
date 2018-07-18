package org.vontech.drivers.android

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.FeatureSpec


class AndroidDriverTest: FeatureSpec({

    feature("the Android info commands") {
        scenario("should pass") {
            true.shouldBeTrue()
        }

        scenario("should fail") {
            false.shouldBeTrue()
        }
    }

})