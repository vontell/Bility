package org.vontech.myexamplebilityapplication

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.vontech.bilitytester.BilityTestConfig
import org.vontech.bilitytester.BilityTester


@RunWith(AndroidJUnit4::class)
class BilityTest {
    private lateinit var config: BilityTestConfig

    private val url = "http://10.0.2.2:8080"

    @Before
    fun configureAppSpec() {
        Log.e("Bility", "STARTING APP SPEC")
        config = BilityTestConfig()
        config.packageName = "org.vontech.myexamplebilityapplication"
        config.maxActions = 400
    }

    @Test
    fun beginBilityTest() {
        Log.e("Bility", "STARTING TEST")
        BilityTester(url, getInstrumentation(), config)
            .startupApp()
            .loop()
    }
}