package io.github.project_travel_mate;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vontech.bilitytester.BilityTestConfig;
import org.vontech.bilitytester.BilityTester;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class BilityTest {

    private BilityTestConfig config;

    private final static String url = "http://10.0.2.2:8080";

    @Before
    public void configureAppSpec() {

        config = new BilityTestConfig();
        config.setPackageName("io.github.project_travel_mate");
        config.setMaxActions(400);

    }

    @Test
    public void beginBilityTest() {
        System.out.println(getInstrumentation().getContext().getPackageName());
        new BilityTester(url, getInstrumentation(), config)
                .startupApp()
                .loop();
    }

}
