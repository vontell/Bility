package org.vontech.bilitytester;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.vontech.core.interaction.InputInteractionType;
import org.vontech.core.interaction.KeyPress;
import org.vontech.core.interaction.SwipeParameters;
import org.vontech.core.interaction.UserAction;
import org.vontech.core.interfaces.Coordinate;
import org.vontech.core.interfaces.LiteralInterace;
import org.vontech.core.interfaces.LiteralInterfaceMetadata;
import org.vontech.core.interfaces.Percept;
import org.vontech.core.interfaces.Perceptifer;
import org.vontech.core.types.AndroidAppTestConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.KeyEvent.KEYCODE_TAB;
import static java.lang.Thread.sleep;
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
    private BilityTestConfig setup;

    private final int SWIPE_STEP_COUNT = 100;

    private final HashMap<KeyPress, Integer> keyMap = new HashMap<>();


    /**
     * Sets up a BilityTester by:
     *  1) Grabbing an instance of the device being tested
     *  2) Downloading the testing configuration from the test server
     * @param instrumentation The instrumentation for this specific test
     */
    public BilityTester(String host, Instrumentation instrumentation, BilityTestConfig setup) {
        this.device = UiDevice.getInstance(instrumentation);
        serverConnection = new ServerConnection(host);
        config = serverConnection.getAppConfig();
        this.setup = setup;

        // Instantiate some constants
        keyMap.put(KeyPress.TAB, KEYCODE_TAB);
        keyMap.put(KeyPress.ENTER, KEYCODE_ENTER);
        keyMap.put(KeyPress.UP, KEYCODE_DPAD_UP);
        keyMap.put(KeyPress.DOWN, KEYCODE_DPAD_DOWN);
        keyMap.put(KeyPress.LEFT, KEYCODE_DPAD_LEFT);
        keyMap.put(KeyPress.RIGHT, KEYCODE_DPAD_RIGHT);

    }

    public BilityTester startupApp() {

        Log.e("BILITY", "STARTING APP");

        String appPackageName = setup.getPackageName();

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

    private final int SLEEP_TIME = 1000;

    public BilityTester loop() {

        // 1) If this is not the first call, wait for any actions, and execute them
        //    An action may involve a user action, waiting, quitting, meta, etc
        UserAction nextAction = serverConnection.awaitNextAction();
        boolean shouldExit = handleAction(nextAction);
        if (shouldExit) {
            return this;
        }

        device.waitForIdle(SLEEP_TIME);
        try { sleep(SLEEP_TIME); } catch (InterruptedException ignored) { }

        // 2) Get information about the state of the user interface
        Activity current = getActivityInstance();
        while(current == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            current = getActivityInstance();
            Log.e("UH", "WAITING");
        }
        LiteralInterace face = AndroidUDL.getLiteralInterfaceFromActivity(current);

        // 3) Send that user interface information to the server, and wait for a response
        serverConnection.sendInterface(face);

        // 4) Also send the screenshot to the server
        //serverConnection.sendScreenshot(face.getMetadata().getId(), current, 200, "SMALL", device);
        //serverConnection.sendScreenshot(face.getMetadata().getId(), current, 500, "WEB", device);

        serverConnection.sendScreenshot(face.getMetadata().getId(), current, 200, "SMALL", device);
        serverConnection.sendScreenshot(face.getMetadata().getId(), current, 500, "WEB", device);

        try { sleep(SLEEP_TIME); } catch (InterruptedException ignored) { }

        return loop();

    }

    /**
     * Returns the name of this activity by class name, or null if this activity does not
     * belong to this package.
     * @return The name of this activity currently in use
     */
    private String getActivityName() {
        Activity act = getActivityInstance();
        if (act == null) {
            return null;
        } else {
            return act.getLocalClassName();
        }
    }

    /**
     * Returns a reference the current activity, if one is currently found. Generally, the
     * activity will be <code>null</code> if the current activity does not belong to this
     * package
     * @return The activity currently in use
     */
    private Activity getActivityInstance(){

        final Activity[] currentActivity = {null};

        getInstrumentation().runOnMainSync(new Runnable(){
            public void run(){
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                Iterator<Activity> it = resumedActivity.iterator();
                if (it.hasNext()) {
                    currentActivity[0] = it.next();
                }
            }
        });

        return currentActivity[0];
    }

    private boolean handleAction(UserAction action) {

        // First, if do nothing, do nothing
        if (action.getType() == InputInteractionType.NONE) {
            return false;
        }

        if (action.getType() == InputInteractionType.QUIT) {
            return true;
        }

        // Otherwise, do action
        Perceptifer pf = action.getPerceptifer();

        if (action.getType() == InputInteractionType.SWIPE) {

            // Get the object to swipe on
            LinkedTreeMap<String, Double> params = (LinkedTreeMap) action.getParameters();
            device.swipe(
                    params.get("startX").intValue(),
                    params.get("startY").intValue(),
                    params.get("endX").intValue(),
                    params.get("endY").intValue(),
                    SWIPE_STEP_COUNT);
            Log.e("SWIPING", params.values().toString());
            return false;
        }

        if (action.getType() == InputInteractionType.CLICK) {
            LinkedTreeMap<String, Double> params = (LinkedTreeMap) action.getParameters();
            device.click(params.get("left").intValue(), params.get("top").intValue());
            return false;
        }

        if (action.getType() == InputInteractionType.FOCUS) {
            List<UiObject2> possibleFocusables = device.findObjects(By.focusable(true));
            LinkedTreeMap<String, Double> params = (LinkedTreeMap) action.getParameters();
            int midpointLeft = params.get("left").intValue();
            int midpointTop = params.get("top").intValue();
            for (UiObject2 f : possibleFocusables) {
                Rect bounds = f.getVisibleBounds();
                if (bounds.contains(midpointLeft, midpointTop)) {
                    
                }
            }

        }

        if(action.getType() == InputInteractionType.KEYPRESS) {
            Log.e("KEY", action.getParameters().toString());
            KeyPress key = KeyPress.valueOf(action.getParameters().toString());
            device.pressKeyCode(keyMap.get(key));
            return false;
        }

        return false;

    }


}