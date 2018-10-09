package org.vontech.bilitytester;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.vontech.bilitytester.utils.ViewHelper;
import org.vontech.core.interfaces.FontStyle;
import org.vontech.core.interfaces.InputChannel;
import org.vontech.core.interfaces.LiteralInterace;
import org.vontech.core.interfaces.LiteralInterfaceMetadata;
import org.vontech.core.interfaces.MediaType;
import org.vontech.core.interfaces.OutputChannel;
import org.vontech.core.interfaces.Percept;
import org.vontech.core.interfaces.PerceptBuilder;
import org.vontech.core.interfaces.Perceptifer;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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

        // First create perceptifers for views
        View rootView = ViewHelper.getRootView(activity);
        if (rootView != null) {
            List<View> allViews = ViewHelper.getAllViews(rootView, true);

            // For every view, create a perceptifer with all possible percepts
            for (View v : allViews) {
                if (isOnScreen(activity, v)) {
                    Perceptifer p = getViewPerceptifer(v);
                    perceptifers.add(p);
                }
            }

        }

        // Then create perceptifers for device buttons
        perceptifers.addAll(getPhysicalButtonPerceptifers(activity));

        Log.i("BILITY", "CREATED UNFILTERED PERCEPTIFERS");

        // Create or access the output and input channels for this interface
        // The LiteralInterace class will handle the filtering of Perceptifers based
        // on these channels
        LiteralInterfaceMetadata metadata = new LiteralInterfaceMetadata(new Random().nextLong());
        return new LiteralInterace(
                perceptifers,
                new HashSet<OutputChannel>(),
                new HashSet<InputChannel>(),
                metadata
        );

    }

    /**
     * This method takes in a View and attempts to resolve the most specific class
     * that represents this View, returning percepts attributed to that view.
     * @param v The view to get a Perceptifer for
     * @return The Perceptifer representing this View
     */
    private static Perceptifer getViewPerceptifer(View v) {

        PerceptBuilder builder = new PerceptBuilder();

        // TODO: This could be useful, ignoring hidden text: https://stackoverflow.com/questions/8636946/get-current-visible-text-in-textview

        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            builder.createTextPercept(tv.getText().toString());
            builder.createFontSizePercept(tv.getTextSize());
            builder.createLineSpacingPercept(
                    tv.getPaint().getFontSpacing() * tv.getLineSpacingMultiplier() + tv.getLineSpacingExtra()
            );
            boolean normal = true;
            if (tv.getTypeface().isBold()) {
                builder.createFontStylePercept(FontStyle.BOLD);
                normal = false;
            }
            if (tv.getTypeface().isItalic()) {
                builder.createFontStylePercept(FontStyle.ITALIC);
                normal = false;
            }
            if (normal) {
                builder.createFontStylePercept(FontStyle.NORMAL);
            }
            builder.createTextColorPercept(tv.getCurrentTextColor());
            Drawable background = tv.getBackground();
            if (background instanceof ColorDrawable) {
                builder.createBackgroundColorPercept(((ColorDrawable) background).getColor());
            }
        }

        if (v instanceof ScrollView) {
            ScrollView sv = (ScrollView) v;
            if (sv.isVerticalScrollBarEnabled()) {
                builder.createScrollProgressPercept((float) sv.getScrollY() / sv.getHeight());
            }
        }

        if (v instanceof ImageView) {
            ImageView iv = (ImageView) v;
            if (iv.getContentDescription() != null) {
                builder.createScreenReaderContentVirtualPercept(iv.getContentDescription().toString());
                builder.createMediaTypePercept(MediaType.IMAGE);
            }
        }

        if (isPurelyContainer(v)) {
            builder.createIsPurelyContainer();
        }

        // Add all basic view properties
        Rect rectf = new Rect();
        v.getGlobalVisibleRect(rectf);

        builder.createLocationPercept(rectf.left, rectf.top);
        builder.createSizePercept(rectf.width(), rectf.height());
        builder.createAlphaPercept(v.getAlpha());
        builder.createClickableVirtualPercept(v.hasOnClickListeners());
        builder.createIdentifierVirtualPercept(v.getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.createNameVirtualPercept(v.getAccessibilityClassName().toString());
        } else {
            builder.createNameVirtualPercept(v.getClass().getName());
        }

        return new Perceptifer(builder.buildPercepts(), builder.buildVirtualPercepts());

    }

    private static Set<Perceptifer> getPhysicalButtonPerceptifers(Activity activity) {

        Set<Perceptifer> deviceButtonPerceptifers = new HashSet<>();
        if (probablyHasBackKey(activity)) {
            PerceptBuilder builder = new PerceptBuilder();
            builder.createPhysicalButtonPercept(KeyEvent.KEYCODE_BACK, "BACK");
            deviceButtonPerceptifers.add(new Perceptifer(builder.buildPercepts(), builder.buildVirtualPercepts()));
        }

        return deviceButtonPerceptifers;

    }

    private static boolean probablyHasBackKey(Activity activity) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = activity.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        }

        //boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return hasBackKey;
    }

    /**
     * Returns true if this View is purely a container - in other words, this View holds other
     * Views that contain information, and do no necessarily hold information themselves. This is
     * determined by:
     *      1) It is a ViewGroup
     *      2) There is no background / borders on this View
     *      3) It contains children
     * @param view
     * @return
     */
    public static boolean isPurelyContainer(View view) {

        if (view instanceof ViewGroup) {
            Log.e("BACKGROUND", "" + view.getBackground());
            if (view.getBackground() == null) {
                if (((ViewGroup) view).getChildCount() > 0) {
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * Returns true if the given view is actually observable on the screen.
     * TODO: Instead, pass all items to backend, as well as device info, and decide on there
     * TODO: Make enum - ONSCREEN, OFFSCREEN, PARTIAL
     * @param
     * @return
     */
    private static boolean isOnScreen(Activity activity, View view) {
        Rect rectf = new Rect();
        view.getGlobalVisibleRect(rectf);
        DisplayMetrics mets = activity.getResources().getDisplayMetrics();
        final Rect screen = new Rect(0, 0, mets.widthPixels, mets.heightPixels);
        return rectf.intersect(screen);
    }

}
