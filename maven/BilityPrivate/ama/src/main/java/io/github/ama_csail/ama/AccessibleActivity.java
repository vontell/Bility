package io.github.ama_csail.ama;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Map;

import io.github.ama_csail.ama.menu.AccessibleHoverMenuService;
import io.github.ama_csail.ama.menu.OnInstructionsLoadedListener;
import io.github.ama_csail.ama.menu.OnAccessibleMenuConnectedListener;
import io.mattcarroll.hover.overlay.OverlayPermission;

/**
 * An accessible version of an Android activity (compatible version).
 * @author Aaron Vontell
 */
public class AccessibleActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_HOVER_PERMISSION = 1000;
    private boolean mPermissionsRequested = false;

    private AccessibleHoverMenuService menuService;
    private OnAccessibleMenuConnectedListener menuConnectedListener;
    private boolean menuBound = false;

//    @Override
//    public void startActivity(Intent intent) {
//
////        String intendedClass = intent.getComponent().getClassName();
////        AlternativeUIManager.getInstance();
////
////        UserInterface desiredInterface = AlternativeUIManager.transform();
//
//        super.startActivity(intent);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        // On Android M and above we need to ask the user for permission to display the Hover
        // menu within the "alert window" layer.  Use OverlayPermission to check for the permission
        // and to request it.
        if (!mPermissionsRequested && !OverlayPermission.hasRuntimePermissionToDrawOverlay(this)) {
            @SuppressWarnings("NewApi")
            Intent myIntent = OverlayPermission.createIntentToRequestOverlayPermission(this);
            startActivityForResult(myIntent, REQUEST_CODE_HOVER_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (menuService != null && menuBound) {
            unbindService(menuConnection);
            menuBound = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_HOVER_PERMISSION == requestCode) {
            mPermissionsRequested = true;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public View getRootView() {
        return findViewById(android.R.id.content);
    }

    /**
     * Enables the accessible menu in a collapsed state
     */
    public void enableMenu() {

        Intent intent = new Intent(this, AccessibleHoverMenuService.class);
        bindService(intent, menuConnection, Context.BIND_AUTO_CREATE);
        startService(intent); // Still must call startService to activate onStartCommand of service

    }

    /**
     * Attaches a listener which gets called when the accessible menu is enabled and a connection
     * is established.
     * @param listener
     */
    public void setOnAccessibleMenuConnectedListener(OnAccessibleMenuConnectedListener listener) {
        this.menuConnectedListener = listener;
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection menuConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AccessibleHoverMenuService.AccessibleHoverMenuBinder binder
                    = (AccessibleHoverMenuService.AccessibleHoverMenuBinder) service;

            //if (binder != null) {
                menuService = binder.getService();
                menuBound = true;

                if (menuConnectedListener != null) {
                    menuConnectedListener.configureMenu();
                }
            //}

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            menuBound = false;
        }
    };

    /**
     * Throws an exception if the menu is not yet enabled.
     */
    private void checkMenu() {
        if (!menuBound) {
            throw new RuntimeException("Menu is not yet started. Please start it with enableMenu()");
        }
    }

    /**
     * Provide a glossary (or mapping of terms to definitions) to the menu, which will be
     * displayed within the Information section. Throws an error if the menu has not been enabled
     * with enableMenu()
     * @param glossary The mapping of terms (as keys) to definitions (as values)
     */
    public void provideGlossary(Map<String, String> glossary) {
        checkMenu();
        menuService.provideGlossary(glossary);
    }

    /**
     * Add more entries (or mapping of terms to definitions) to the glossary with the menu, which
     * will be displayed within the Information section. Throws an error if the menu has not been
     * enabled with enableMenu()
     * @param glossary The additional mapping of terms (as keys) to definitions (as values)
     */
    public void addToGlossary(Map<String, String> glossary) {
        checkMenu();
        menuService.addGlossary(glossary);
    }

    /**
     * Clears all terms and definitions from the glossary within the accessible menu
     */
    public void clearGlossary() {
        checkMenu();
        menuService.clearGlossary();
    }

    /**
     * Disables the menu, barring the user from seeing and interacting with it
     */
    public void disabledMenu() {
        throw new RuntimeException("Not yet implemented!");
    }

    // Vision-based helper methods

    /**
     * Enables language settings within the accessible menu, which provides font resizing, dyslexic
     * font toggle, and more language options.
     */
    public void enableLanguageSettings() {
        checkMenu();
        menuService.enableLanguageSettings(getRootView());
    }

    /**
     * Replaces all fonts within the main content with the OpenDyslexic font
     * @param enabled True if OpenDyslexic should be used
     */
    public void enableDyslexiaFont(boolean enabled) {

        // TODO: Caching to restore old font
        if (enabled) {
            View container = getRootView();
            AMA.setFont(this, R.raw.opendyslexicregular,
                    R.raw.opendyslexicbold, R.raw.opendyslexicitalic, "OpenDyslexic", container);
            //AMA.setFontSize(container, 18);
        } else {
            throw new RuntimeException("Not yet implemented!");
        }

    }

    /**
     * Sets a listener to call when an instruction is attempted to be loaded. Also takes in an
     * object that will be passed during calls as a means to config the instruction module
     * @param config An object that will be passed to the onInstructionsLoaded call (may be null)
     * @param listener The listener with onInstructionsLoaded to be called during a load
     */
    public void setOnInstructionsLoadedListener(Object config,
                                                OnInstructionsLoadedListener listener) {
        checkMenu();
        menuService.setOnInstructionsLoadedListener(config, listener);
    }

}
