package org.vontech.bilitytester;

import android.app.Instrumentation;
import android.support.test.uiautomator.UiDevice;

/**
 * Created by vontell on 7/28/18.
 */
public class BilityTester {

    private final AppSpecification specification;
    private final UiDevice device;

    public BilityTester(AppSpecification specification, Instrumentation instrumentation) {
        this.specification = specification;
        this.device = UiDevice.getInstance(instrumentation);
    }

}