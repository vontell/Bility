package io.github.ama_csail.ama.compiler;

import java.util.ArrayList;
import java.util.List;

import io.github.ama_csail.ama.util.storage.UserPreferences;

/**
 * A manager for the accessibility compilation process, which determines rules to apply
 * to user interfaces. Note that this is a "singleton" class and should be modified through
 * the provided static method.
 * @author Aaron Vontell
 */
public class AlternativeUIManager {

    private static AlternativeUIManager manager;

    public static AlternativeUIManager getInstance() {
        if (manager == null) {
            manager = new AlternativeUIManager();
        }
        return manager;
    }

    private List<AlternateRule> rules;

    private AlternativeUIManager(){
        this.rules = new ArrayList<>();
    }

    public static void provideRule(AlternateRule rule) {
        AlternativeUIManager.getInstance();
        manager.rules.add(rule);
    }

    public static UserInterface transform(UserInterface ui, UserPreferences preferences) {
        for (AlternateRule rule : manager.rules) {
            if (rule.condition(ui, preferences)) {
                return rule.modify(ui, preferences);
            }
        }
        return ui;
    }

}
