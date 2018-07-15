package io.github.ama_csail.ama.compiler;

import io.github.ama_csail.ama.util.storage.UserPreferences;

/**
 * A rule which is matched against to determine if a layout should be changed
 * depending on the current layout and user preferences.
 * @author Aaron Vontell
 */
public interface AlternateRule {

    /**
     * Returns true if the given activity and user preferences indicates that this rule
     * is matched, and therefore that the modifications dictated by this rule should be applied
     * @param userInterface
     * @param prefs
     * @return
     */
    boolean condition(UserInterface userInterface, UserPreferences prefs);

    /**
     * Returns a new user interface that should be used if <code>condition</code> evaluated
     * to true.
     * @param userInterface
     * @param prefs
     * @return
     */
    UserInterface modify(UserInterface userInterface, UserPreferences prefs);

}
