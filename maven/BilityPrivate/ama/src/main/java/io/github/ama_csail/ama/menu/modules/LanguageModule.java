package io.github.ama_csail.ama.menu.modules;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ama_csail.ama.AMA;
import io.github.ama_csail.ama.R;
import io.github.ama_csail.ama.menu.MenuModule;
import io.github.ama_csail.ama.util.views.ViewHelper;
import io.mattcarroll.hover.Content;

/**
 * The section / module with language options for the accessibility menu. Includes the following
 * features:
 *      - Toggle dyslexic font
 *      - Change text padding
 *      - Change text size
 *      - Display current text information for each of the above
 * @author Aaron Vontell
 */
public class LanguageModule implements Content, MenuModule {

    private Context context;
    private String title;
    private int layoutRes;
    private LinearLayout moduleView;
    private View modifiableView;

    // Some views that are useful to have references to
    private Switch openDyslexicSwitch;
    private SeekBar paddingSeekBar;
    private SeekBar textSizeSeekBar;
    private TextView paddingLabel;

    /**
     * Creates the language module for changing settings in the hover menu
     * @param context The calling context (i.e. the accessible hover service)
     * @param pageTitle The title to show for the hover menu module
     * @param layoutRes The content resource for this module
     */
    public LanguageModule(@NonNull Context context, @NonNull String pageTitle, @LayoutRes int layoutRes) {
        this.context = context.getApplicationContext();
        this.title = pageTitle;
        this.layoutRes = layoutRes;
        this.moduleView = createScreenView();
    }

    // MODEL DEFINITION
    private Map<TextView, Typeface> originalFonts = new HashMap<>();
    private boolean dyslexicFontEnabled = false;
    private int paddingModifier = 0;
    private int paddingUpperLimit = 5;
    private int paddingLowerLimit = -5;
    private int textSizeModifier = 0;
    private int textSizeUpperLimit = 5;
    private int textSizeLowerLimit = -5;

    // CONTROLLER DEFINITIONS

    // Some important methods for startup and configuration

    /**
     * Creates the view to be loaded into the hover menu
     * @return the view to be loaded into the hover menu
     */
    private LinearLayout createScreenView() {

        LinearLayout languageView = (LinearLayout) LayoutInflater
                .from(this.context)
                .inflate(layoutRes, null);

        // Prep the view
        languageView.setBackgroundColor(this.context.getResources().getColor(android.R.color.white));
        openDyslexicSwitch = languageView.findViewById(R.id.dyslexic_font_switch);
        paddingSeekBar = languageView.findViewById(R.id.padding_seekbar);
        paddingLabel = languageView.findViewById(R.id.padding_amount);

        // Set listeners and actions
        openDyslexicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    // First, save all TypeFaces
                    List<TextView> textViews = ViewHelper.getAllTextViews(modifiableView);
                    originalFonts.clear();
                    for (TextView v : textViews) {
                        originalFonts.put(v, v.getTypeface());
                    }

                    // Now apply the OpenDyslexic font
                    AMA.setFont(context, R.raw.opendyslexicregular,
                            R.raw.opendyslexicbold, R.raw.opendyslexicitalic, "OpenDyslexic", modifiableView);

                } else {

                    // Go through all fonts and reapply (if TextView still relevant)
                    for (TextView v : originalFonts.keySet()) {
                        if (v != null) {
                            Typeface tf = originalFonts.get(v);
                            v.setTypeface(tf, tf.getStyle());
                        }
                    }

                }

                dyslexicFontEnabled = isChecked;
            }
        });

        paddingSeekBar.setMax(paddingUpperLimit - paddingLowerLimit);
        paddingSeekBar.setProgress(-1 * paddingLowerLimit);
        paddingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int offset = progress + paddingLowerLimit;
                paddingLabel.setText((offset < 0 ? "" : "+") + Integer.toString(offset));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                
            }
        });

        return languageView;
    }

    /**
     * Refreshes the information or configuration of this module
     */
    public void refreshContents() {

        openDyslexicSwitch.setChecked(dyslexicFontEnabled);

    }

    /**
     * Sets the View that this language module can modify
     * @param rootView The view that can be modified by this module
     */
    public void setRootView(View rootView) {
        this.modifiableView = rootView;
    }

    @NonNull
    @Override
    public View getView() {
        return this.moduleView;
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }

    @Override
    public void onShown() {
        // Don't need to do anything here at the moment
    }

    @Override
    public void onHidden() {
        // Don't need to do anything here at the moment
    }

}
