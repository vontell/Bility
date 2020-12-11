---
id: android-getting-started
title: Getting Started
sidebar_label: Getting Started
slug: /
---

Bility is a framework for automatically scraping and assessing the accessibility of mobile applications. Unlike tools such as Google Accessibility Scanner that require manual navigation and track static issues (such as contrast and small touch target sizes), Bility automatically explores the application, finding static issues as well as dynamic issues (such as keyboard traps, unexpected changes in context, etc...). A desktop or web app displays both a live recording of the test and a succinct report of all issues found. To see a demo video of the alpha version, click the video below or visit the link <a href="https://youtu.be/Y5AeZpNnp8U">here</a>.

<div align="center">
  <iframe width="560" height="315" src="https://www.youtube.com/embed/Y5AeZpNnp8U" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</div>

:::note

A completed version of this tutorial can be found [here](http://google.com).

:::

# Setting up your Android Test

A Bility test is run on your Android application via an Instrumented Test. First, add the following dependency to your app module

```groovy
dependencies {
    ...
    androidTestImplementation 'org.vontech.bility:bility-android:0.0.1'
}
```

Next, inside your Instrumented Tests folder (this is the folder with the `androidTest` label next to it in your Android Studio project when viewing in Android model), create a new file called `AccessibilityTest.kt` and paste the following:

```kotlin
package YOUR_PACKAGE_HERE

import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.vontech.bility.android.BilityTestConfig
import org.vontech.bility.android.BilityTester


@RunWith(AndroidJUnit4::class)
class BilityTest {

    companion object {
        private const val url = "http://10.0.2.2:8080"
    }

    private var config: BilityTestConfig? = null

    @Before
    fun configureAppSpec() {
        config = BilityTestConfig()
        config.setPackageName("com.mycompany.myapp")
        config.setMaxActions(400)
    }

    @Test
    fun beginBilityTest() {
        BilityTester(url, getInstrumentation(), config)
            .startupApp()
            .loop()
    }

}
```

## Admonitions

:::note

This is a note

:::

:::tip

This is a tip

:::

:::important

This is important

:::

:::caution

This is a caution

:::

:::warning

This is a warning

:::
