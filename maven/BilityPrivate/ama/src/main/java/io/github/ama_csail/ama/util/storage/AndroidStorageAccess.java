package io.github.ama_csail.ama.util.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * An internal class which provides functions and helpers for accessing and saving data
 * on the Android device.
 * @author Aaron Vontell
 */
public class AndroidStorageAccess {

    private Context context = null;
    private String prefName = null;
    private String path = null;

    private SharedPreferences preferences = null;


    /**
     * Establishes storage access which is private to this application. This is useful for
     * storing information related to the accessible application.
     * @param context An application-specific storage manager
     * @param name A name for the preferences file that you want to access
     */
    public AndroidStorageAccess(Context context, String name) {
        this.context = context;
        this.prefName = name;
        this.preferences = context.getSharedPreferences(name, 0);
    }

    /**
     * Establishes storage access which is public across this device. This is useful for
     * storing information related to the user or device as a whole.
     * @param path A path to a file for reading and writing
     */
    public AndroidStorageAccess(String path) {
        this.path = path;
    }

    /**
     * Make the default storage accessor inaccessible
     */
    private AndroidStorageAccess() {}

    /**
     * Returns true if the given key exists in this storage interface
     * @param key the key to search for
     * @return true if the storage contains data with this key, and false otherwise
     */
    public boolean hasKey(String key) {
        if(preferences != null) {
            return preferences.contains(key);
        } else {
            throw new RuntimeException("External storage not yet implemented");
        }
    }

    /**
     * Returns the string mapped to the given key
     * @param key the key to search for within internal or external storage
     * @param defaultString a default string to return if the key is not found
     * @return the string mapped to the given key, or defaultString otherwise
     */
    public String getString(String key, @Nullable String defaultString) {
        if(preferences != null) {
            return preferences.getString(key, defaultString);
        } else {
            throw new RuntimeException("External storage not yet implemented");
        }
    }

    /**
     * Returns the boolean mapped to the given key
     * @param key the key to search for within internal or external storage
     * @param defaultBool a default boolean to return if the key is not found
     * @return the boolean mapped to the given key, or defaultBool otherwise
     */
    public boolean getBoolean(String key, boolean defaultBool) {
        if(preferences != null) {
            return preferences.getBoolean(key, defaultBool);
        } else {
            throw new RuntimeException("External storage not yet implemented");
        }
    }

    /**
     * Returns the integer mapped to the given key
     * @param key the key to search for within internal or external storage
     * @param defaultInt a default integer to return if the key is not found
     * @return the integer mapped to the given key, or defaultInt otherwise
     */
    public int getInt(String key, int defaultInt) {
        if(preferences != null) {
            return preferences.getInt(key, defaultInt);
        } else {
            throw new RuntimeException("External storage not yet implemented");
        }
    }

    /**
     * Returns the long mapped to the given key
     * @param key the key to search for within internal or external storage
     * @param defaultLong a default long to return if the key is not found
     * @return the long mapped to the given key, or defaultLong otherwise
     */
    public long getLong(String key, long defaultLong) {
        if(preferences != null) {
            return preferences.getLong(key, defaultLong);
        } else {
            throw new RuntimeException("External storage not yet implemented");
        }
    }

    /**
     * Returns the float mapped to the given key
     * @param key the key to search for within internal or external storage
     * @param defaultFloat a default float to return if the key is not found
     * @return the float mapped to the given key, or defaultFloat otherwise
     */
    public float getFloat(String key, float defaultFloat) {
        if(preferences != null) {
            return preferences.getFloat(key, defaultFloat);
        } else {
            throw new RuntimeException("External storage not yet implemented");
        }
    }

}
