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

<br />

:::note

A completed version of this tutorial can be found [here](https://github.com/Vontech/BilityExample).

:::

## Setting up your Android Test

A Bility test is run on your Android application via an Instrumented Test. First, add the following dependency to your app module

```groovy
dependencies {
    ...
    androidTestImplementation 'org.vontech.bility:bility-android:0.0.5'
}
```

Next, inside your Instrumented Tests folder (this is the folder with the `androidTest` label next to it in your Android Studio project when viewing in Android mode), create a new file called `AccessibilityTest.kt` and paste the following:

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
    private lateinit var config: BilityTestConfig

    @Before
    fun configureAppSpec() {
        config = BilityTestConfig()
        config.packageName = "com.mycompany.myapp"
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
```

Make sure to set `YOUR_PACKAGE_NAME` to your package (see the default instrumented test to see what this should be), and set `com.mycompany.myapp` to your app classpath.

**It is important to set the correct permissions for your application**. These permissions allow the test to communicate with the Bility backend, as well as save screenshots for reporting. Within your `AndroidManifest.xml` file, please include the following permissions within the manifest block:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

You may also need to permit clear text traffic for your application. Within the `application` block, include the following:

```
android:usesCleartextTraffic="true"
```

:::important

You may need to run your app once and navigate to the Android app settings to turn on some of these permissions.

:::

## Starting the Bility Backend

Now that the test is created, we can run it on Bility. First, clone the Bility project to your file system:

```
git clone git@github.com:vontell/Bility.git
```

Next, navigate to the Bility server, and start up the Bility server.

```
cd Bility
./gradlew bility-server:run
```

You should see a message 

Now, start the frontend for Bility. In a new terminal, navigate to the Bility project again, and run the following:

```
./gradlew runUI
```

This may take a minute to run, and should say `runUI` at the bottom when complete. An electron app will appear with the Bility frontend.

![Bility frontend with no app running](/img/bility_frontend.png)

### Optional - Starting Screen Cast

The frontend for Bility also supports screencasting directly from a device to the frontend. This can be done by using `minicap`. DOCUMENTATION COMING SOON, but if you are curious, check out the scripts folder.

## Running the Accessibility Tests

The final step in running the accessibility test is to simply run the instrumented test! In Android Studio, run the `BilityTest`, and wait for your device and test to startup. Once it is waiting to run, in the frontend, click `Start Bility Test`. You should begin to see accessibility testing results!

![Bility frontend with results as a graph](/img/results_1.png)

![Bility frontend with a specific accessibility issue highlighted](/img/results_2.png)
