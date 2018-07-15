package io.github.ama_csail.ama.testserver;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * An object holding basic information about a View or ViewGroup in Android.
 * @author Aaron Vontell
 */

public class ViewInfoStruct {

    private String longName;
    private String shortName;
    private String id;
    private Class classObj;
    private String contentDescription;
    private String explicitText;
    private ViewInfoStruct parent;
    private List<Color> colors;
    private List<ViewInfoStruct> children;
    private int[] upperLeftBound;
    private int[] lowerRightBound;
    private boolean isImageView;
    private boolean isTextView;
    private boolean isViewGroup;
    private Bitmap screenShot;

    public ViewInfoStruct(View v) {

        this.longName = v.getClass().getCanonicalName();
        this.shortName = v.getClass().getSimpleName();
        this.classObj = v.getClass();
        this.contentDescription = v.getContentDescription() == null ? null : v.getContentDescription().toString();
        this.isTextView = v instanceof TextView;
        this.isImageView = v instanceof ImageView;
        this.isViewGroup = v instanceof ViewGroup;
        this.explicitText = this.isTextView ? ((TextView) v).getText().toString() : null;
        try {
            this.id = v.getResources().getResourceEntryName(v.getId());
        } catch(Exception e) {
            this.id = "unknown";
        }

        upperLeftBound = new int[2];
        lowerRightBound = new int[2];
        v.getLocationOnScreen(upperLeftBound);
        lowerRightBound[0] = upperLeftBound[0] + v.getWidth();
        lowerRightBound[1] = upperLeftBound[1] + v.getHeight();

        // Get view screenshot and colors
//        if (ViewHelper.isVisible(v)) {
//            Bitmap b = Bitmap.createBitmap(v.getWidth() , v.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas c = new Canvas(b);
//            v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
//            v.draw(c);
//            screenShot = b;
//        }

    }

    public boolean needsContentDescription() {
        return contentDescription == null && isImageView;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getId() {
        return id;
    }

    public int[] getUpperLeftBound() {
        return upperLeftBound;
    }

    public int[] getLowerRightBound() {
        return lowerRightBound;
    }

    public boolean isImageView() {
        return isImageView;
    }

    public boolean isTextView() {
        return isTextView;
    }

    public boolean isViewGroup() {
        return isViewGroup;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(shortName);
        builder.append(" with id ");
        builder.append(id);
        builder.append("\n");

        builder.append("\t");
        builder.append("Long name: ");
        builder.append(longName);
        builder.append("\n");

        builder.append("\t");
        builder.append("This is a ");
        if (isTextView) { builder.append("TextView"); }
        else if (isImageView) { builder.append("ImageView"); }
        else if (isViewGroup) { builder.append("ViewGroup"); }
        else { builder.append("View"); }
        builder.append("\n");

        if (isTextView) {
            builder.append("\t");
            builder.append("Text content: ");
            builder.append(explicitText);
            builder.append("\n");
        }

        if (contentDescription != null) {
            builder.append("\t");
            builder.append("Content Description: ");
            builder.append(contentDescription);
            builder.append("\n");
        }

        if (contentDescription == null && isImageView) {
            builder.append("\t");
            builder.append("This image is missing a content description");
            builder.append("\n");
        }

        builder.append("\t");
        builder.append("Bounds: [");
        builder.append(upperLeftBound[0]);
        builder.append(",");
        builder.append(upperLeftBound[1]);
        builder.append("],[");
        builder.append(lowerRightBound[0]);
        builder.append(",");
        builder.append(lowerRightBound[1]);
        builder.append("]");
        builder.append("\n");

//        builder.append("\t");
//        builder.append("Screenshot: ");
//        builder.append(this.screenShot);
//        builder.append("\n");

        return builder.toString();

    }

}
