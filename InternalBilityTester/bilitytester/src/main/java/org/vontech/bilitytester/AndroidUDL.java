package org.vontech.bilitytester;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.vontech.bilitytester.utils.ViewHelper;
import org.vontech.core.interfaces.LiteralInterace;
import org.vontech.core.interfaces.Percept;
import org.vontech.core.interfaces.PerceptBuilder;
import org.vontech.core.interfaces.Perceptifer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The AndroidUDL class assists in converting Android user interfaces (i.e. objects
 * such as Activities and fragments) into literal interfaces within the Universal
 * Design Language
 * @author Aron Vontell
 * @created August 12th, 2018
 * @updated August 12th, 2018
 */
public class AndroidUDL {

    public static LiteralInterace getLiteralInterfaceFromActivity(Activity activity) {

        Set<Perceptifer> perceptifers = new HashSet<>();

        View rootView = ViewHelper.getRootView(activity);
        if (rootView != null) {
            List<View> allViews = ViewHelper.getAllViews(rootView, false);

            // For every view, create a perceptifer with all possible percepts
            for (View v : allViews) {
                Log.e("PERCEPTIFER VIEW", v.getClass().toString());
                Perceptifer p = getPerceptifer(v);
                perceptifers.add(p);
                Log.e("PERCEPTIFER", p.toString());
            }

        }

        // Create or access the output and input channels for this interface
        // The LiteralInterace class will handle the filtering of Perceptifers based
        // on these channels

        return new LiteralInterace(perceptifers, null, null, null)

        return null;

    }

    /**
     * This method takes in a View and attempts to resolve the most specific class
     * that represents this View, returning percepts attributed to that view.
     * @param v The view to get a Perceptifer for
     * @return The Perceptifer representing this View
     */
    public static Perceptifer getPerceptifer(View v) {

        PerceptBuilder builder = new PerceptBuilder();

        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            builder.createTextPercept(tv.getText().toString());
        }

        // Add all basic view properties
        Rect rectf = new Rect();
        v.getGlobalVisibleRect(rectf);

        builder.createLocationPercept(rectf.left, rectf.top);
        builder.createSizePercept(rectf.width(), rectf.height());

        return new Perceptifer(builder.buildPercepts(), builder.buildVirtualPercepts());

    }

}
