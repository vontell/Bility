package org.vontech.bilitytester.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A collection of helper functions for dealing with Views
 * @author Aron Vontell
 * @created August 12th, 2018
 * @updated August 12th, 2018
 */
public class ViewHelper {

    /**
     * Returns a list of all Views, including ViewGroups. In other words, this method will return
     * a pointer to every View instance within a given View.
     * @param view The view to search through for all Views
     * @return a list of all Views contained within view, including view
     */
    public static List<View> getAllViews(View view, boolean includeViewGroups) {

        Stack<View> front = new Stack<>();
        List<View> views = new LinkedList<>();
        front.add(view);

        // Traverse through the tree, finding TextViews as we go
        while (!front.empty()) {
            View v = front.pop();
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    front.add(child);
                }
                if (includeViewGroups) {
                    views.add(vg);
                }
            } else {
                views.add(v);
            }
        }

        return views;

    }

    /**
     * Returns the root view of an Activity
     * @param activity The activity to get the root view of
     * @return The root view of Activity (as defined by android.R.id.content)
     */
    public static View getRootView(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }

}
