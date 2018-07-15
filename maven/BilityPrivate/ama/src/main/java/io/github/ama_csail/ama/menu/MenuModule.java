package io.github.ama_csail.ama.menu;

/**
 * An interface which enforces each module content to provide a refreshContents() function
 * for easily updating views based on the underlying model.
 * @author Aaron Vontell
 */
public interface MenuModule {

    void refreshContents();

}
