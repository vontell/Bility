package io.github.ama_csail.ama;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import java.util.List;

import io.github.ama_csail.ama.util.ActionClass;
import io.github.ama_csail.ama.util.Contrast;
import io.github.ama_csail.ama.util.fonts.FontUtil;
import io.github.ama_csail.ama.util.storage.SystemConfig;
import io.github.ama_csail.ama.util.views.ViewHelper;

import static android.content.Context.ACCESSIBILITY_SERVICE;

/**
 * The core class for basic AMA functionality.
 * @author Aaron Vontell
 */
public class AMA {

    // TODO: When we talk about dimensions, what do we mean?

    //section Constants

    private static final String TALKBACK_PACKAGE = "com.google.android.marvin.talkback";

    //section AMA Versioning and Information

    /**
     * Returns a string representing the version of this AMA library.
     * @return a string representing the version of this AMA library.
     */
    public String getVersion() {
        return SystemConfig.VERSION;
    }

    /**
     * Returns a url which contains information about the AMA library.
     * @return a url which contains information about the AMA library.
     */
    public String getHomepage() {
        return SystemConfig.HOMEPAGE;
    }


    // section Static Helper Method

    /**
     * Checks to see if TalkBack is installed (note that this is the Google version of TalkBack)
     * @param context The calling activity
     * @return true if TalkBack is installed on this device
     */
    public static boolean isTalkBackInstalled(Context context) {

        List<ApplicationInfo> packages;
        PackageManager pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(TALKBACK_PACKAGE))
                return true;
        }
        return false;

    }

    /**
     * Checks to see if TalkBack is currently enabled
     * @param context The calling activity
     * @return true if TalkBack is enabled
     */
    public static boolean isTalkBackEnabled(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
        return am.isEnabled();
    }

    /**
     * Checks to see if explore by touch (provided via TalkBack) is currently enabled
     * @param context The calling activity
     * @return true if explore by touch is enabled
     */
    public static boolean isExploreByTouchEnabled(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
        return am.isTouchExplorationEnabled();
    }

    /**
     * Returns an Intent that will open Google Play Store, and link to the TalkBack app page
     * @return The intent that will open the play store at the TalkBack page
     */
    public static Intent getTalkBackPlayIntent() {

        return new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + TALKBACK_PACKAGE));

    }

    /**
     * Modifies the given View to use the given Typefaces. If bold and italic typefaces are not
     * specified (i.e. are null), then the given regular type face is used, with a best effort to
     * apply the desired styles. If View is a ViewGroup, the View hierarchy is traversed until
     * TextViews are found. <b>NOTE: It is recommended that the String-overloaded version of this
     * method is used, which ensures caching of the fonts.</b>
     * @param regularTypeface The new default Typeface to use
     * @param boldTypeface A bold typeface to be used (optional)
     * @param italicTypeface An italic typeface to be used (optional)
     * @param view The view to display the new fonts
     */
    public static void setFont(@Nullable Typeface regularTypeface,
                               @Nullable Typeface boldTypeface,
                               @Nullable Typeface italicTypeface,
                               View view) {
        FontUtil.overrideFonts(view, -1, regularTypeface, boldTypeface, italicTypeface);
    }

    /**
     * Modifies the given View to use the given Typefaces. If bold and italic typefaces are not
     * specified (i.e. are null), then the given regular type face is used, with a best effort to
     * apply the desired styles. If View is a ViewGroup, the View hierarchy is traversed until
     * TextViews are found. Each string is a path to the desired font within the given context's
     * assets folder.
     * @param context A context which holds the font assets
     * @param regularTypeface A path in assets to the new default Typeface to use
     * @param boldTypeface A path in assets to the bold typeface to be used (optional)
     * @param italicTypeface A path in assets to the italic typeface to be used (optional)
     * @param view The view to display the new fonts
     */
    public static void setFont(@NonNull Context context,
                               @Nullable String regularTypeface,
                               @Nullable String boldTypeface,
                               @Nullable String italicTypeface,
                               View view) {
        Typeface rt = FontUtil.get(regularTypeface, context);
        Typeface bt = FontUtil.get(boldTypeface, context);
        Typeface it = FontUtil.get(italicTypeface, context);
        setFont(rt, bt, it, view);
    }

    /**
     * Modifies the given View to use the given Typefaces. If bold and italic typefaces are not
     * specified (i.e. are null), then the given regular type face is used, with a best effort to
     * apply the desired styles. If View is a ViewGroup, the View hierarchy is traversed until
     * TextViews are found. Each resource is a raw resource which points to the requested typeface.
     * @param context A context which holds the font assets
     * @param regularTypeface A resource in res/raw to the new default Typeface to use
     * @param boldTypeface A resource in res/raw to the bold typeface to be used (optional)
     * @param italicTypeface A resource in res/raw to the italic typeface to be used (optional)
     * @param identifier A name for this font type to use for caching purposes
     * @param view The view to display the new fonts
     */
    public static void setFont(@NonNull Context context,
                               @RawRes int regularTypeface,
                               @RawRes int boldTypeface,
                               @RawRes int italicTypeface,
                               String identifier,
                               View view) {

        Typeface rt = FontUtil.getFromRes(identifier + "_REG", regularTypeface, context);
        Typeface bt = FontUtil.getFromRes(identifier + "_BOLD", boldTypeface, context);
        Typeface it = FontUtil.getFromRes(identifier + "_ITALIC", italicTypeface, context);
        setFont(rt, bt, it, view);
    }

    /**
     * Overrides the font size of any TextView encapsulated within the given view to be the given
     * size.
     * @param view The view to modify (will modify any contained TextViews)
     * @param size The new font size to set
     */
    public static void setFontSize(View view, @Dimension float size) {
        FontUtil.overrideFonts(view, size, null, null, null);
    }

    /**
     * Increases the font size of each TextView contained with the given view by the given number
     * of pixels
     * @param view The View (or ViewGroup) to change font size within
     * @param amount The amount to increase each font by
     */
    public static void increaseFontSize(View view, float amount) {

        List<TextView> textViews = ViewHelper.getAllTextViews(view);
        for (TextView tv : textViews) {
            float pixelSize = tv.getTextSize();
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelSize + amount);
        }

    }

    /**
     * Increases the white space around each view by the desired amount (left, right, top, and
     * bottom)
     * @param amount The amount (in pixels) to increase the padding around each view
     * @param views Any number of views that one wishes to increase the padding around
     */
    public static void increaseWhitespace(@Dimension int amount, View ... views) {

        for (View v : views) {
            int top = v.getPaddingTop();
            int bot = v.getPaddingBottom();
            int lef = v.getPaddingLeft();
            int rig = v.getPaddingRight();
            v.setPadding(lef + amount, top + amount, rig + amount, bot + amount );
        }

    }

    /**
     * Sets the minimum amount of padding for all given views to a certain amount (left, right, top,
     * and bottom)
     * @param minimum The amount (in pixels) which is the minimum amount of padding
     * @param views Any number of views that one wishes to set the minimum the padding around
     */
    public static void setMinimumPadding(int minimum, View ... views) {

        for (View v : views) {
            int top = v.getPaddingTop();
            int bot = v.getPaddingBottom();
            int lef = v.getPaddingLeft();
            int rig = v.getPaddingRight();
            top = top < minimum ? minimum : top;
            bot = bot < minimum ? minimum : bot;
            rig = rig < minimum ? minimum : rig;
            lef = lef < minimum ? minimum : lef;
            v.setPadding(lef, top, rig, bot);
        }

    }

    /**
     * Assesses whether the given colors satisfy the given ratio specification
     * Uses resources from W3 in calculations, especially from the following links:
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
     * @param context The calling application
     * @param colorForeground The color resource in the front of the background
     * @param colorBackground The color resource of the background
     * @param contrastLevel The desired contrast specification to meet
     */
    public static boolean satisfiesContrast(Context context,
                                            @ColorRes int colorForeground,
                                            @ColorRes int colorBackground,
                                            Contrast contrastLevel) {
        double contrast = getContrast(context, colorForeground, colorBackground);
        return contrast >= contrastLevel.ratio();
    }

    /**
     * Assesses whether the given colors satisfy the given ratio specification
     * Uses resources from W3 in calculations, especially from the following links:
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
     * @param context The calling application
     * @param colorForeground The color in front of the background, in 0xAARRGGBB format
     * @param colorBackground The color of the background, in 0xAARRGGBB format
     * @param contrastLevel The desired contrast specification to meet
     */
    public static boolean satisfiesContrastInt(Context context,
                                               int colorForeground,
                                               int colorBackground,
                                               Contrast contrastLevel) {
        double contrast = getContrastInt(context, colorForeground, colorBackground);
        return contrast >= contrastLevel.ratio();
    }

    /**
     * Returns the contrast of the given foreground and background colors, ranging from 1 to 21
     * Uses resources from W3 in calculations, especially from the following links:
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
     * @param context The calling application
     * @param colorForeground The color resource in front of the background
     * @param colorBackground The color resource of the background
     */
    public static double getContrast(Context context, @ColorRes int colorForeground, @ColorRes int colorBackground) {

        // Get the RGB values of each color
        // This integer represents 0xAARRGGBB
        int color1 = context.getResources().getColor(colorForeground);
        int color2 = context.getResources().getColor(colorBackground);
        return getContrastInt(context, color1, color2);

    }

    /**
     * Returns the contrast of the given foreground and background colors, ranging from 1 to 21
     * Uses resources from W3 in calculations, especially from the following links:
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
     * @param context The calling application
     * @param colorForeground The color in front of the background, in the format 0xAARRGGBB
     * @param colorBackground The color of the background, in the format 0xAARRGGBB
     */
    public static double getContrastInt(Context context, int colorForeground, int colorBackground) {

        // Get the RGB values of each color
        // This integer represents 0xAARRGGBB
        int color1 = colorForeground;
        int color2 = colorBackground;
        int[] oneARGB = new int[] {(0xFF000000 & color1) >> 24, (0xFF0000 & color1) >> 16, (0xFF00 & color1) >> 8, 0xFF & color1};
        int[] twoARGB = new int[] {(0xFF000000 & color2) >> 24, (0xFF0000 & color2) >> 16, (0xFF00 & color2) >> 8, 0xFF & color2};

        // Calculation for relative luminance, as defined by https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
        float oneL = calcLuminance(oneARGB);
        float twoL = calcLuminance(twoARGB);

        return oneL > twoL ? (oneL + 0.05) / (twoL + 0.05) : (twoL + 0.05) / (oneL + 0.05);

    }

    /**
     * Calculates the luminance of an ARGB color, as given by the equations at
     * https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
     * @param comps The ARGB components of the color (from 0 to 255)
     *              comps[0] = alpha, [1] = red, [2] = green, [3] = blue
     * @return the luminance to be used in contrast calculations
     */
    public static float calcLuminance(int[] comps) {

        float RSRGB = comps[1] / 255f;
        float GSRGB = comps[2] / 255f;
        float BSRGB = comps[3] / 255f;
        float R = RSRGB <= 0.03928 ? RSRGB / 12.92f : (float) Math.pow((RSRGB + 0.055) / 1.055f, 2.4);
        float G = GSRGB <= 0.03928 ? GSRGB / 12.92f : (float) Math.pow((GSRGB + 0.055) / 1.055f, 2.4);
        float B = BSRGB <= 0.03928 ? BSRGB / 12.92f : (float) Math.pow((BSRGB + 0.055) / 1.055f, 2.4);
        float luminance = 0.2126f * R + 0.7152f * G + 0.0722f * B;

        return luminance;

    }

    /**
     * Specifies that the given view may have components that are navigated using speech, as
     * indicated by the given boolean
     * @param view The view to tag with speech navigation usage
     * @param speechNavigated true if this view uses speech navigation, false otherwise
     */
    public static void setIsSpeechNavigationUsed(View view, boolean speechNavigated) {
        view.setTag(R.id.AMA_view_uses_speech_navigation, speechNavigated);
    }

    /**
     * Returns true if this view has been marked as being navigated by speech, and false
     * otherwise.
     * @param view The view to check for speech navigation
     * @return True if marked as true using <code>setIsSpeechNavigationUsed(view, bool)</code>
     */
    public static boolean isSpeechNavigationUsed(View view) {
        Object rawResult = view.getTag(R.id.AMA_view_uses_speech_navigation);
        return rawResult != null && (boolean) rawResult;
    }

    /**
     * Attaches an object to a View, which can be used in accessibility helpers for contextual
     * information
     * @param view The view to tag with a help object
     * @param helper The Object which contains help information
     */
    public static void setHelpObject(View view, Object helper) {
        view.setTag(R.id.AMA_view_help_message, helper);
    }

    /**
     * Clears any helper object attached to the given view
     * @param view The view to clear of any help object
     */
    public static void clearHelpObject(View view) {
        view.setTag(R.id.AMA_view_help_message, null);
    }

    /**
     * Returns any helper object that has been attached to the given View.
     * @param view The view to check for a helper object
     * @return The helper object which has been previously attached with <code>setHelpObject</code>
     */
    public static Object getHelpObject(View view) {
        return view.getTag(R.id.AMA_view_help_message);
    }

    /**
     * Attaches an action class to an object, which can help in identifying connotations of a view
     * @param view The view to tag with an action class
     * @param action The action class to use
     */
    public static void setActionClass(View view, ActionClass action) {
        view.setTag(R.id.AMA_view_action_class, action);
    }

    /**
     * Clears any action class that was set to this object (i.e. becomes UNSET)
     * @param view The view to clear of any action class
     */
    public static void clearActionClass(View view) {
        view.setTag(R.id.AMA_view_action_class, ActionClass.UNSET);
    }

    /**
     * Returns the action class of this view, or UNSET if never defined
     * @param view The view to obtain the action class of
     * @return The action class which has been previously attached with <code>setActionClass</code>
     */
    public static ActionClass getActionClass(View view) {
        Object rawResult = view.getTag(R.id.AMA_view_action_class);
        return rawResult != null ? (ActionClass) rawResult : ActionClass.UNSET;
    }


    public static void createNaturalKeyboardNavigation(ViewGroup group) {

        for (int i = 1; i < group.getChildCount(); i++) {
            group.getChildAt(i - 1).setNextFocusDownId(group.getChildAt(i).getId());
            group.getChildAt(i).setNextFocusUpId(group.getChildAt(i - 1).getId());
        }

    }

}
