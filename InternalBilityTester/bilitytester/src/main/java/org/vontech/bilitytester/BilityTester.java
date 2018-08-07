package org.vontech.bilitytester;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.google.gson.Gson;

import org.vontech.core.types.AndroidAppTestConfig;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * The core driver for testing Android devices, given a specification from the Android
 * test server. Once setup, a test loop will begin, sending results back to the server.
 */
public class BilityTester {

    private AndroidAppTestConfig config;
    private final UiDevice device;
    private ServerConnection serverConnection;


    /**
     * Sets up a BilityTester by:
     *  1) Grabbing an instance of the device being tested
     *  2) Downloading the testing configuration from the test server
     * @param instrumentation The instrumentation for this specific test
     */
    public BilityTester(String host, Instrumentation instrumentation) {
        this.device = UiDevice.getInstance(instrumentation);
        serverConnection = new ServerConnection(host);
        config = serverConnection.getAppConfig();
    }

    public BilityTester startupApp() {

        Log.e("BILITY", "STARTING APP");

        String appPackageName = config.getPackageName();

        // Send info that the app has started
        serverConnection.sendStartupEvent(config);

        // Start from the home screen
        //mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                config.getTimeout());

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(appPackageName);

        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(appPackageName).depth(0)),
                config.getTimeout());

        device.waitForIdle();

        // Save the start state of the automaton
        // SimpleAutomataState startState = new SimpleAutomataState(getActivityInstance().getLocalClassName() + " Screen");
        // automaton = new SimpleAutomaton(startState);

        return this;

    }

    // PRIVATE HELPER METHODS


}