package io.github.ama_csail.ama.menu;

import android.view.ViewGroup;

/**
 * An interface that gets used during the loading of an instruction module
 * @author Aaron Vontell
 */
public interface OnInstructionsLoadedListener {

    void onInstructionsLoaded(ViewGroup parent, Object config);

}
