package io.github.ama_csail.ama.util.views;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A collection of utilities for various view modifications and adjustments
 * @author Aaron Vontell
 */
public class ViewHelper {

    /**
     * Returns a list of all TextViews contained (and possibly including) within view
     * @param view The view to search through for TextView objects
     * @return a list of all TextViews contained within view
     */
    public static List<TextView> getAllTextViews(View view) {

        Stack<View> front = new Stack<>();
        List<TextView> textViews = new LinkedList<>();
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
            } else if (v instanceof TextView) {
                textViews.add((TextView) v);
            }
        }

        return textViews;

    }

    /**
     * Returns a list of all Views, including ViewGroups. In other words, this method will return
     * a pointer to every View instance within a given View.
     * @param view The view to search through for all Views
     * @return a list of all Views contained within view, including view
     */
    public static List<View> getAllViews(View view) {

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
            } else {
                views.add(v);
            }
        }

        return views;

    }

    /**
     * Returns a list of all strings found within the given View (i.e. all TextView contents)
     * @param view The view to search through for Strings
     * @return a list of all Strings contained within view
     */
    public static List<String> getAllStrings(View view) {

        // TODO: Here we are sacrificing performance for reusability of code - need to decide
        //       on this tradeoff
        List<TextView> allTextViews = getAllTextViews(view);
        List<String> allStrings = new ArrayList<>(allTextViews.size());
        for (TextView tv : allTextViews) {
            allStrings.add(tv.getEditableText().toString());
        }

        return allStrings;

    }

    public static boolean isVisible(final View view) {

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0,height, width);
        return actualPosition.intersect(screen);
    }

}
