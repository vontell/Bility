package io.github.ama_csail.ama.compiler;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

/**
 * Defines an identifier of a user interface, such as an Activity, Fragment, or View
 * @author Aaron Vontell
 */
public class UserInterface {

    private Class clazz;

    /**
     * Creates an identifier for the given class type (i.e. MainActivity.class)
     * @param type the class for the user interface
     */
    public UserInterface(Class type) {
        boolean isActivity = Activity.class.isAssignableFrom(type);
        boolean isView = View.class.isAssignableFrom(type);
        boolean isFragmentSupport = android.support.v4.app.Fragment.class.isAssignableFrom(type);
        boolean isFragment = Fragment.class.isAssignableFrom(type);
        if (!(isActivity || isView || isFragmentSupport || isFragment)) {
            throw new RuntimeException("A user interface must be an Activity, View, or Fragment");
        } else {
            this.clazz = type;
        }
    }

    /**
     * Returns true if the given class is of the same type as this user interface
     * @param anotherClazz a class to compare this class to
     * @return true if the given class is of the same type as this user interface
     */
    public boolean is(Class anotherClazz) {
        return clazz == anotherClazz;
    }

}
