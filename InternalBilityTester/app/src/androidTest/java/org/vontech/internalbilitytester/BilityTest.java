package org.vontech.internalbilitytester;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vontech.bilitytester.BilityTestConfig;
import org.vontech.bilitytester.BilityTester;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class BilityTest {

    private final static String url = "http://10.0.2.2:8080";

    private BilityTestConfig config;

    @Before
    public void configureAppSpec() {
        config = new BilityTestConfig();
        config.setPackageName("org.vontech.internalbilitytester");
        config.setMaxActions(400);
    }

    @Test
    public void beginBilityTest() {
        BilityTester tester = new BilityTester(url, getInstrumentation())
            .startupApp()
            .loop();
    }

}
