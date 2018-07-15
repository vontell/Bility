package io.github.ama_csail.ama.testserver.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines a specification of an application which can be used for automated testing
 * @author Aaron Vontell
 */
public class AppSpecification {

    private String packageName;

    // Definitions for the real-time application tree
    private List<Class> knownActivities;
    private List<String> knownViewIds;

    // Definitions for the expected application tree
    private List<UiInputActionType> possibleInputs;

    public AppSpecification(String packageName) {
        this.packageName = packageName;

        knownActivities = new ArrayList<>();
        knownViewIds = new ArrayList<>();

    }

    // Setters

    public void expectActivity(Class activityClass) {
        knownActivities.add(activityClass);
    }

    public void expectId(String id) {
        knownViewIds.add(id);
    }

    public void allowedInputs(UiInputActionType ... inputs) {
        possibleInputs = Arrays.asList(inputs);
    }

    // Getters

    public String getPackageName() {
        return packageName;
    }


}
