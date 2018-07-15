package io.github.ama_csail.ama.menu;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import io.github.ama_csail.ama.R;

/**
 * Helper functions for the accessible hover menu
 * @author Aaron Vontell
 */
public class MenuHelper {

    /**
     * Returns an image view to be displayed within the accessibility menu given
     * a drawable to load
     * @param context The context of the calling activity or service
     * @param drawable The drawable to return as an image view, ready to be a tab
     * @return An image view which is modified to be a tab in the hover menu
     */
    public static ImageView getTabView(Context context, @DrawableRes int drawable) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return imageView;
    }

}
