package org.vontech.internalbilitytester;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vontech.android.AppSpecification;
import org.vontech.android.BilitySetup;
import org.vontech.android.BilityTester;
import org.vontech.android.UiInputActionType;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class BilityTest {

    private AppSpecification specification;

    @Before
    public void configureAppSpec() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        specification = new AppSpecification(appContext.getPackageName());
        specification.allowedInputs(UiInputActionType.CLICK, UiInputActionType.SWIPE);

        // TODO: This is where we need to download test information from the server
        new BilitySetup(appContext);

    }

    @Test
    public void beginBilityTest() {

        BilityTester tester = new BilityTester(specification, getInstrumentation());

    }

}
