package io.github.ama_csail.amaexampleapp;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.ama_csail.ama.testserver.MobileSocket;
import io.github.ama_csail.amaexampleapp.utils.AppSpecification;
import io.github.ama_csail.amaexampleapp.utils.BilityTester;
import io.github.ama_csail.amaexampleapp.utils.TestSuiteType;
import io.github.ama_csail.amaexampleapp.utils.UiInputActionType;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class AccessibilityTest {

    private AppSpecification specification;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void configureAppSpec() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        specification = new AppSpecification(appContext.getPackageName());
        specification.expectActivity(MainActivity.class);
        specification.expectActivity(AboutActivity.class);
        specification.allowedInputs(UiInputActionType.CLICK, UiInputActionType.SWIPE);

    }

    @Test
    public void testAccessibility() {

        MobileSocket socket = new MobileSocket("192.168.43.230", 8080);
        //socket.start();

        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

        BilityTester tester = new BilityTester(specification, mDevice)
                .provideSocket(socket)
                .setTimeout(5000)
                .setRuns(3)
                .setSeed(20182018)
                .setMaxActions(14)
                .setTestSuites(TestSuiteType.WCAG2_A)
                .setDynamicallyAware(true)
                .startupApp()
                .startTestLoop();

        tester.printTestResults();

        assertTrue("App has loaded!", true);

    }




}
