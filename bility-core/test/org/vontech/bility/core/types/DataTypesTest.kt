package org.vontech.bility.core.types

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import org.vontech.bility.core.types.AndroidAppTestConfig

class DataTypesTest: FeatureSpec({

    feature("the Android test configuration") {
        scenario("should behave correctly with defaults") {

           val defaultProjectConfig = AndroidAppTestConfig("com.example.app")

            defaultProjectConfig.timeout shouldBe 3000
            defaultProjectConfig.numRuns shouldBe 3
            defaultProjectConfig.packageName shouldBe "com.example.app"
            defaultProjectConfig.maxActions shouldBe 14

        }
    }

})