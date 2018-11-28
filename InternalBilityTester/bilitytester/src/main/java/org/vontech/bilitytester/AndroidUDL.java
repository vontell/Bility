package org.vontech.bilitytester;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.michaelevans.colorart.library.ColorArt;
import org.vontech.bilitytester.utils.ViewHelper;
import org.vontech.core.interfaces.FontStyle;
import org.vontech.core.interfaces.InputChannel;
import org.vontech.core.interfaces.LiteralInterace;
import org.vontech.core.interfaces.LiteralInterfaceMetadata;
import org.vontech.core.interfaces.MediaType;
import org.vontech.core.interfaces.OutputChannel;
import org.vontech.core.interfaces.PerceptBuilder;
import org.vontech.core.interfaces.Perceptifer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

        // First create perceptifers for views
        Set<Perceptifer> perceptifers = new HashSet<>();

        // Fill up some references for later

        // Then get all root views, finding the activity rootView as well
        View activityRootView = ViewHelper.getRootView(activity);
        List<View> rootViews = getViewRoots();
        rootViews.remove(activityRootView);

//        // Create the base percepts for the activity rootView
//        // Do a traversal through the tree
//        Pair<Perceptifer, Set<Perceptifer>> activityResult = traverseAndGeneratePerceptifers(activity, activityRootView, false);
//        perceptifers.addAll(activityResult.second);
//
//        // Now do it for every other rootView, then adding the Perceptifer as a child of the activity perceptifer
//        Set<Perceptifer> windowChildren = new HashSet<>();
//        windowChildren.add(activityResult.first);
//        for (View rootView : rootViews) {
//
//            // Do a traversal through the tree
//            Pair<Perceptifer, Set<Perceptifer>> otherProcessed = traverseAndGeneratePerceptifers(activity, rootView, false);
//
//            // TODO: Now add this perceptifer as a child to the activity perceptifer
//
//            // Finally, add these percepts to all percepts as well
//            perceptifers.addAll(otherProcessed.second);
//            windowChildren.add(otherProcessed.first);
//
//        }
//
//        Perceptifer finalRoot = new PerceptBuilder()
//                .createVirtualRootPercept()
//                .createLocationPercept(0, 0)
//                .createRoughViewOrderingPercept(windowChildren)
//                .buildPerceptifer();
//
//        perceptifers.add(finalRoot);

        // REMOVE AFTER TEST
        Pair<Perceptifer, Set<Perceptifer>> activityResult = traverseAndGeneratePerceptifers(activity, activityRootView, true);
        perceptifers.addAll(activityResult.second);


        // Then create perceptifers for device buttons
        perceptifers.addAll(getPhysicalButtonPerceptifers(activity));

        Log.i("BILITY", "CREATED UNFILTERED PERCEPTIFERS");

        // Create or access the output and input channels for this interface
        // The LiteralInterace class will handle the filtering of Perceptifers based
        // on these channels
        LiteralInterfaceMetadata metadata = new LiteralInterfaceMetadata(UUID.randomUUID().toString());

        for (Perceptifer p: perceptifers) {
            p.setParentId(metadata.getId());
        }

        return new LiteralInterace(
                perceptifers,
                new HashSet<OutputChannel>(),
                new HashSet<InputChannel>(),
                metadata
        );

    }

    /**
     * This method takes in a View and attempts to resolve the most specific class
     * that represents this View, returning percepts attributed to that view. Does this
     * recursively and propagates id's back up the tree for positional information.
     * @param v The view to create a perceptifer for
     * @return The Perceptifer representing this View
     */
    private static Pair<Perceptifer, Set<Perceptifer>> traverseAndGeneratePerceptifers(Activity activity, View v, boolean isRoot) {

        if (!isOnScreen(activity, v)) {
            return null; // TODO: This makes the assumption that if the parent isn't on-screen, than the children are not as well - this could potentially be wrong
        }

        Set<Perceptifer> perceptifers = new HashSet<>();
        PerceptBuilder builder = new PerceptBuilder();

        // First, build the percepts of each child if this is a ViewGroup
        if (v instanceof ViewGroup) {
            Set<Perceptifer> directChildren = new HashSet<>();
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                Pair<Perceptifer, Set<Perceptifer>> result = traverseAndGeneratePerceptifers(activity, child, false);
                if (result != null) {
                    perceptifers.addAll(result.second); // Add the child and all descendants of that child
                    directChildren.add(result.first);   // Keep a pointer to that specific child
                }
            }

            // Now that we can access the IDs of children, create a hierarchy percept
            builder.createRoughViewOrderingPercept(directChildren);

        }

        // TODO: This could be useful, ignoring hidden text: https://stackoverflow.com/questions/8636946/get-current-visible-text-in-textview

        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            builder.createTextPercept(tv.getText().toString());
            builder.createFontSizePercept(Math.round(tv.getTextSize()));
            builder.createLineSpacingPercept(
                    Math.round(tv.getPaint().getFontSpacing() * tv.getLineSpacingMultiplier() + tv.getLineSpacingExtra())
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

        int[] screenLocation = new int[]{0, 0};
        v.getLocationOnScreen(screenLocation);

        builder.createLocationPercept(screenLocation[0], screenLocation[1]);
        builder.createSizePercept(rectf.width(), rectf.height());
        builder.createAlphaPercept(v.getAlpha());
        builder.createClickableVirtualPercept(v.hasOnClickListeners());
        builder.createFocusableVirtualPercept(v.isFocused());
        builder.createIdentifierVirtualPercept(v.getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.createNameVirtualPercept(v.getAccessibilityClassName().toString());
        } else {
            builder.createNameVirtualPercept(v.getClass().getName());
        }

        if (v.getClass().getName().toLowerCase().contains("seekbar")) {
            System.out.println("SEEKBAR WAS SEEN AND LOGGED");
        }

        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable) {
            builder.createBackgroundColorPercept(((ColorDrawable) background).getColor());
        } else if (background != null) {
            Bitmap bitmap = drawableToBitmap(background);
            if (bitmap != null) {
                ColorArt colorArt = new ColorArt(bitmap);
                builder.createBackgroundColorPercept(colorArt.getBackgroundColor());
            }
        }

        // If root, add root indicator
        if (isRoot) {
            builder.createVirtualRootPercept();
        }

        // Add this Perceptifer
        Perceptifer finalPerceptifer = builder.buildPerceptifer();
        perceptifers.add(finalPerceptifer);
        return new Pair<>(finalPerceptifer, perceptifers);

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

    public static View getOpenDialog(Activity act) {

        if (act instanceof FragmentActivity) {
            System.out.println("IS A FRAGMENT ACTIVITY");
            FragmentActivity activity = (FragmentActivity) act;
            List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof DialogFragment) {
                        System.out.println("HAS A DIALOG FRAGMENT");
                        return fragment.getView();
                    }
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<View> getViewRoots() {

        List<ViewParent> viewRoots = new ArrayList<>();

        try {
            Object windowManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                windowManager = Class.forName("android.view.WindowManagerGlobal")
                        .getMethod("getInstance").invoke(null);
            } else {
                Field f = Class.forName("android.view.WindowManagerImpl")
                        .getDeclaredField("sWindowManager");
                f.setAccessible(true);
                windowManager = f.get(null);
            }

            Field rootsField = windowManager.getClass().getDeclaredField("mRoots");
            rootsField.setAccessible(true);

            Field stoppedField = Class.forName("android.view.ViewRootImpl")
                    .getDeclaredField("mStopped");
            stoppedField.setAccessible(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                List<ViewParent> viewParents = (List<ViewParent>) rootsField.get(windowManager);
                // Filter out inactive view roots
                for (ViewParent viewParent : viewParents) {
                    boolean stopped = (boolean) stoppedField.get(viewParent);
                    if (!stopped) {
                        viewRoots.add(viewParent);
                    }
                }
            } else {
                ViewParent[] viewParents = (ViewParent[]) rootsField.get(windowManager);
                // Filter out inactive view roots
                for (ViewParent viewParent : viewParents) {
                    boolean stopped = (boolean) stoppedField.get(viewParent);
                    if (!stopped) {
                        viewRoots.add(viewParent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<View> rootViews = new ArrayList<>();
        for (ViewParent vp : viewRoots) {
            if (vp instanceof View) {
                rootViews.add((View) vp);
            }
            if (vp.getClass().getCanonicalName().equals("android.view.ViewRootImpl")) {
                try {
                    View view = (View) Class.forName("android.view.ViewRootImpl")
                            .getMethod("getView").invoke(vp);
                    rootViews.add(view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("&&&&&&&& " + rootViews.size());

        return rootViews;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        return null;

    }

//    static ViewGroup combineGroups(List<View> groups) {
//        if (groups.size() > 0) {
//            ViewGroup vg = new FrameLayout(groups.get(0).getContext());
//            for (View v : groups) {
//                vg.addView(v);
//            }
//            if (vg.getChildCount() > 1) {
//                System.out.println("GETTING DIALOG STUFF");
//            }
//            return vg;
//        }
//        return null;
//    }

}
