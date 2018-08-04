package drivers.android

import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import org.vontech.androidserver.drivers.android.AndroidDriver

class AndroidDriverTest: FeatureSpec({

    feature("the Android info commands") {

        val driver = AndroidDriver()

        scenario("should have ANDROID_HOME set") {
            driver.isAndroidHomeSet() shouldBe true
        }
        scenario("should have > 0 platforms installed") {
            driver.getAvailablePlatforms().size.shouldBeGreaterThan(0)
        }
        scenario("should have > 0 build tools installed") {
            driver.getAvailableBuildTools().size.shouldBeGreaterThan(0)
        }
        scenario("should have >0 emulators installed") {
            driver.getAvailableEmulators().size.shouldBeGreaterThan(0)
        }

    }

})