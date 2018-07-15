package io.github.ama_csail.amaexampleapp;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by vontell on 3/5/18.
 */

@RunWith(RobolectricTestRunner.class)
public class ExampleRoboTest {

    Activity exampleActivity;

    @Before
    public void setUp() {
        exampleActivity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void loadSuccess() {

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        ShadowActivity activity = shadowOf(exampleActivity);
        System.out.println(activity.getContentView().toString());
        assertTrue(true);
    }

}
