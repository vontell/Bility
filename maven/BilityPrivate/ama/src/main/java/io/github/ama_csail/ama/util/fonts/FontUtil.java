package io.github.ama_csail.ama.util.fonts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * Handles the changing of fonts to new font families and typefaces
 * @author Aaron Vontell
 * @version 0.0.1
 */
public class FontUtil {

    /**
     * A cache of fonts that may be used
     */
    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    /**
     * Returns a font from the assets folder with the given name. Returns null if not found
     * @param name The font (including extension).For example, "font.ttf"
     * @param context The context asking for this font
     * @return The font corresponding to that font name, or null if not found
     */
    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }

    /**
     * Returns a font from the given resource, cached with the given name. Returns null if not found
     * Partially adapted from https://stackoverflow.com/questions/7610355/font-in-android-library
     * @param name The name of the font (used for cache references)
     * @param resource The raw resource which is a reference to the desired font
     * @param context The context which has given to the given resource
     * @return the found Typeface, or null if not found
     */
    public static Typeface getFromRes(String name, @RawRes int resource, Context context) {

        Typeface tf = fontCache.get(name);
        if(tf == null) {

            InputStream is = null;
            try {
                is = context.getResources().openRawResource(resource);
            }
            catch(Resources.NotFoundException e) {
                return null;
            }

            String outPath = context.getCacheDir() + "/tmp" + System.currentTimeMillis() + ".raw";

            try {
                byte[] buffer = new byte[is.available()];
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));

                int l = 0;
                while((l = is.read(buffer)) > 0)
                    bos.write(buffer, 0, l);

                bos.close();

                tf = Typeface.createFromFile(outPath);

                // clean up
                new File(outPath).delete();
            }
            catch (IOException e) {
                return null;
            }
        }

        fontCache.put(name, tf);
        return tf;
    }

    /**
     * Changes all TextView fonts in view 'v' to the given typeface 't'
     * @param v The view asking for the font change. If this is a ViewGroup, this function is
     *          called recursively until a TextView is found
     * @param size A dimensioned size for the new typeface. If less than 0, original size is used
     * @param rt The regular typeface to use. If null, the original typeface is used.
     * @param bt The bold typeface to use. If null, rt is used (i.e. new typeface takes precedence
     *           over existing styles). If rt is null, then the original typeface remains unchanged
     * @param it The italic typeface to use. If null, rt is used (i.e. new typeface takes precedence
     *           over existing styles). If rt is null, then the original typeface remains unchanged
     */
    public static void overrideFonts(final View v,
                                     @Dimension float size,
                                     @Nullable final Typeface rt,
                                     @Nullable final Typeface bt,
                                     @Nullable Typeface it) {

        // TODO: Open q. - do we really want to invalidate on each TextView? Might be best to
        // TODO            invalidate only on the original View v used in the first recursive level

        // TODO: There is a bug with the bold fonts - a bottom padding / margin is always added. #1

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                overrideFonts(child, size, rt, bt, it);
            }
        } else if (v instanceof TextView) {
            TextView toModify = (TextView) v;
            Typeface original = toModify.getTypeface();
            size = size > 0 ? size : toModify.getTextSize();
            int originalStyle = original.getStyle();
            if (original.isBold()) {
                if (bt != null) {
                    toModify.setTypeface(bt, originalStyle);
                    toModify.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                    toModify.invalidate();
                    return;
                }
            }
            else if (original.isItalic()) {
                if (it != null) {
                    toModify.setTypeface(it, originalStyle);
                    toModify.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                    toModify.invalidate();
                    return;
                }
            }
            if (rt != null) {
                toModify.setTypeface(rt, originalStyle);
            }
            toModify.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            toModify.invalidate();
        }

    }

}