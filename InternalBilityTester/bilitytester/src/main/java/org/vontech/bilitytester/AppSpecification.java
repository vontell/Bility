package org.vontech.bilitytester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vontell on 7/28/18.
 */
public class AppSpecification {

    private String packageName;

    // Definitions or the real-time application tree
    private List<Class> knownActivities;
    private List<String> knownViewIds;

    // Definitions for the expected application tree
    private List<UiInputActionType> possibleInputs;

    public AppSpecification(String packageName) {
        this.packageName = packageName;

        knownActivities = new ArrayList<>();
        knownViewIds = new ArrayList<>();

    }

    public void allowedInputs(UiInputActionType ... inputs) {
        possibleInputs = Arrays.asList(inputs);
    }

    // Getters

    public String getPackageName() {
        return packageName;
    }


}