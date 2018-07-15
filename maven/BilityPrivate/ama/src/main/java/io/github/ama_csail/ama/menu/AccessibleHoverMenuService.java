package io.github.ama_csail.ama.menu;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.ama_csail.ama.menu.modules.MenuModuleType;
import io.mattcarroll.hover.HoverView;
import io.mattcarroll.hover.window.HoverMenuService;

/**
 * The menu service that will be started to provide an accessibility menu.
 * @author Aaron Vontell
 */
public class AccessibleHoverMenuService extends HoverMenuService {

    public static String IDENTIFIER = "AMAAccessibleMenuService";
    private final IBinder binder = new AccessibleHoverMenuBinder();
    private Set<MenuModuleType> registeredModules;

    private AccessibleHoverMenu menu;

    @Override
    protected void onHoverMenuLaunched(@NonNull Intent intent, @NonNull HoverView hoverView) {
        // Configure and start your HoverView.
        menu = new AccessibleHoverMenu(getApplicationContext());
        registeredModules = new HashSet<>();
        possiblyRegisterModule(MenuModuleType.HOME);
        hoverView.setMenu(menu);
        hoverView.collapse();
    }

    /**
     * Registers / creates a module, if that module is not already started
     * @param type The MenuModuleType to start, such as GLOSSARY or HOME
     */
    private void possiblyRegisterModule(MenuModuleType type) {
        if (!registeredModules.contains(type)) {
            menu.registerModule(type);
            registeredModules.add(type);
        }
    }

    /**
     * Provides a glossary to the glossary module, refreshing the contents in the process
     * @param glossary The new mapping of terms to definitions to display
     */
    public void provideGlossary(Map<String, String> glossary) {
        possiblyRegisterModule(MenuModuleType.GLOSSARY);
        menu.provideGlossary(glossary);
    }

    /**
     * Starts clearing the glossary module
     */
    public void clearGlossary() {
        possiblyRegisterModule(MenuModuleType.GLOSSARY);
        menu.clearGlossary();
    }

    /**
     * Adds more terms to any existing glossary
     * @param glossary The additional mapping of terms to definitions to display
     */
    public void addGlossary(Map<String, String> glossary) {
        possiblyRegisterModule(MenuModuleType.GLOSSARY);
        menu.addGlossary(glossary);
    }

    /**
     * Sets a listener to call when an instruction is attempted to be loaded. Also takes in an
     * object that will be passed during calls as a means to config the instruction module
     * @param config An object that will be passed to the onInstructionsLoaded call (may be null)
     * @param listener The listener with onInstructionsLoaded to be called during a load
     */
    public void setOnInstructionsLoadedListener(@Nullable Object config,
                                                @Nullable OnInstructionsLoadedListener listener) {
        possiblyRegisterModule(MenuModuleType.INSTRUCTIONS);
        menu.setOnInstructionsLoadedListener(config, listener);
    }

    /**
     * Enables language settings, which allows the user to enable the dyslexic font, change text
     * padding, and text size.
     * @param rootView The view which the module can change text of
     */
    public void enableLanguageSettings(View rootView) {
        possiblyRegisterModule(MenuModuleType.LANGUAGE);
        menu.enableLanguageSettings(rootView);
    }

    /**
     * Class used for the client Binder.
     */
    public class AccessibleHoverMenuBinder extends Binder {
        public AccessibleHoverMenuService getService() {
            // Return this instance of AccessibleHoverMenuService so clients can call public methods
            return AccessibleHoverMenuService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
