package org.vontech.myexamplebilityapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.vontech.bility.android.BilityTestConfig
import org.vontech.bility.android.BilityTester


@RunWith(AndroidJUnit4::class)
class BilityKotlinTest {
    private lateinit var config: BilityTestConfig

    @Before
    fun configureAppSpec() {
        config = BilityTestConfig()
        config.packageName = "org.vontech.myexamplebilityapplication"
        config.maxActions = 400
    }

    @Test
    fun beginBilityTest() {
        BilityTester(url, getInstrumentation(), config)
                .startupApp()
                .loop()
    }

    companion object {
        private const val url = "http://10.0.2.2:8080"
    }
}