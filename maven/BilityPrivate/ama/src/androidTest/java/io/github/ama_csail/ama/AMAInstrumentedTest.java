package io.github.ama_csail.ama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AMAInstrumentedTest {

    private Context appContext = null;
    private String packageName = null;
    private UiDevice mDevice;

    private static final int LAUNCH_TIMEOUT = 5000;

    @Rule
    public ActivityTestRule<Activity> activityTestRule = new ActivityTestRule<>(Activity.class);

    @BeforeClass
    public void setupTesting() {
        appContext = InstrumentationRegistry.getTargetContext();
        packageName = appContext.getPackageName();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.github.ama_csail.ama.test", appContext.getPackageName());
    }

    @Test
    public void openTest() throws Exception {

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)),
                LAUNCH_TIMEOUT);

        // First and foremost, grant external write permissions if not already allowed. This will'
        // allows us to save screenshots and logs with all testing results. Once the tests are finished,
        // we will delete the temporary storage.

        Activity activity = activityTestRule.getActivity();
        //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        //PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.READ_EXTERNAL_STORAGE);

        final File screenFile = new File(Environment.getExternalStorageDirectory(), "screenshotNew.png");
        final File viewFile = new File(Environment.getExternalStorageDirectory(), "viewFile.xml");
        try {
            screenFile.createNewFile();
            viewFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Screenshot saved at", screenFile.toString());
        boolean success = mDevice.takeScreenshot(screenFile);
        Log.e("Screenshot taken?", "" + success);


        try {
            mDevice.dumpWindowHierarchy(viewFile);
            Log.e("FILE", "View file saved");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("App has loaded!", true);

    }


}
