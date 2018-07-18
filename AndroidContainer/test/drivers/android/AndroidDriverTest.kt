package drivers.android

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.FeatureSpec

class AndroidDriverTest: FeatureSpec({

    feature("the Android info commands") {
        scenario("should pass") {
            true.shouldBeTrue()
        }

        scenario("should pass again") {
            false.shouldBeFalse()
        }
    }

})