package io.github.ama_csail.ama.menu.modules;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.github.ama_csail.ama.R;
import io.github.ama_csail.ama.menu.OnInstructionsLoadedListener;
import io.github.ama_csail.ama.menu.MenuModule;
import io.mattcarroll.hover.Content;

/**
 * A menu module which represents a view in which instructions can be loaded.
 */
public class InstructionsModule implements Content, MenuModule {

    private Context context;
    private String title;
    private View moduleView;
    private int layoutRes;

    /**
     * Creates the instruction module for showing views that give instructions in a given activity
     * @param context The calling context (i.e. the accessible hover service)
     * @param pageTitle The title to show for the module
     * @param layoutRes The content resource for this module
     */
    public InstructionsModule(@NonNull Context context, @NonNull String pageTitle, @LayoutRes int layoutRes) {
        this.context = context.getApplicationContext();
        this.title = pageTitle;
        this.layoutRes = layoutRes;
        this.moduleView = createScreenView();
    }

    // Methods that are used to manipulate and change the content of this section / module

    // MODEL DEFINITION
    private OnInstructionsLoadedListener listener;
    private Object configuration;
    private LinearLayout currentView;

    // CONTROLLER DEFINITIONS

    // Some important methods for startup and configuration


    private View createScreenView() {

        LinearLayout instructionView = (LinearLayout) LayoutInflater
                .from(this.context)
                .inflate(layoutRes, null);

        // Prep the view
        instructionView.setBackgroundColor(this.context.getResources().getColor(android.R.color.white));
        currentView = instructionView.findViewById(R.id.default_view);

        return instructionView;

    }

    /**
     * Sets a listener to call when an instruction is attempted to be loaded. Also takes in an
     * object that will be passed during calls as a means to config the instruction module
     * @param config An object that will be passed to the onInstructionsLoaded call (may be null)
     * @param listener The listener with onInstructionsLoaded to be called during a load
     */
    public void setOnInstructionsLoadedListener(@Nullable Object config,
                                                @Nullable OnInstructionsLoadedListener listener) {
        this.listener = listener;
        this.configuration = config;
    }

    @Override
    public void refreshContents() {

        // Replace the current view with the newly loaded view
        // TODO This is bad, because we don't know when we need to reload, since it may depend
        // on the configuration. This means that configuration should be a better object
        if (listener != null) {
            listener.onInstructionsLoaded(currentView, configuration);
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this.moduleView;
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }

    @Override
    public void onShown() {

    }

    @Override
    public void onHidden() {

    }
}
