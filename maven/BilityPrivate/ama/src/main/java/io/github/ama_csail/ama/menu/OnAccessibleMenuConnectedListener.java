package io.github.ama_csail.ama.menu;

/**
 * A listener to be implemented within an accessible activity which is called when the accessibility
 * menu is ready to begin receiving model changes and configurations.
 * @author Aaron Vontell
 */
public interface OnAccessibleMenuConnectedListener {

    void configureMenu();

}
