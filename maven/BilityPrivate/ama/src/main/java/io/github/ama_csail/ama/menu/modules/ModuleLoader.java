package io.github.ama_csail.ama.menu.modules;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import io.github.ama_csail.ama.R;
import io.github.ama_csail.ama.menu.MenuHelper;
import io.mattcarroll.hover.Content;
import io.mattcarroll.hover.HoverMenu;

/**
 * Class for creating various modules to be used within the menu.
 * @author Aaron Vontell
 */
public class ModuleLoader {

    /**
     * Creates and returns the home menu module.
     * @param context The calling context, such as the accessible hover menu service
     * @return the home menu module
     */
    private static HoverMenu.Section getHomeModule(Context context) {

        final String IDENTIFIER = "AMAHome";
        final String TITLE = "Home";
        final int ICON_RESOURCE = R.drawable.ama;
        final int LAYOUT_RESOURCE = R.layout.home_module;

        Content home = new HomeModule(context, TITLE, LAYOUT_RESOURCE);

        return createSection(context, IDENTIFIER, ICON_RESOURCE, home);

    }

    /**
     * Creates and returns the glossary menu module.
     * @param context The calling context, such as the accessible hover menu service
     * @return the glossary menu module
     */
    private static HoverMenu.Section getGlossaryModule(Context context) {

        final String IDENTIFIER = "AMAGlossary";
        final String TITLE = "Glossary";
        final int ICON_RESOURCE = R.drawable.ama;
        final int LAYOUT_RESOURCE = R.layout.glossary_module;

        Content glossary = new GlossaryModule(context, TITLE, LAYOUT_RESOURCE);

        return createSection(context, IDENTIFIER, ICON_RESOURCE, glossary);

    }

    /**
     * Creates and returns the instructions menu module.
     * @param context The calling context, such as the accessible hover menu service
     * @return the instructions menu module
     */
    private static HoverMenu.Section getInstructionsModule(Context context) {

        final String IDENTIFIER = "AMAInstructions";
        final String TITLE = "Instructions";
        final int ICON_RESOURCE = R.drawable.ama;
        final int LAYOUT_RESOURCE = R.layout.instruction_module;

        Content glossary = new InstructionsModule(context, TITLE, LAYOUT_RESOURCE);

        return createSection(context, IDENTIFIER, ICON_RESOURCE, glossary);

    }

    /**
     * Creates and returns the language menu module.
     * @param context The calling context, such as the accessible hover menu service
     * @return the language menu module
     */
    private static HoverMenu.Section getLanguageModule(Context context) {

        final String IDENTIFIER = "AMALanguageOptions";
        final String TITLE = "Language Options";
        final int ICON_RESOURCE = R.drawable.ama;
        final int LAYOUT_RESOURCE = R.layout.language_module;

        Content glossary = new LanguageModule(context, TITLE, LAYOUT_RESOURCE);

        return createSection(context, IDENTIFIER, ICON_RESOURCE, glossary);

    }

    /**
     * Useful helper function to create sections (i.e. entire hover menu components) from various
     * properties.
     * @param context The calling context, such as the accessible hover menu service
     * @param identifier A unique identifier for this section
     * @param iconRes The drawable resource to use as an icon for this section
     * @param module The content to load into this section
     * @return the constructed section
     */
    private static HoverMenu.Section createSection(Context context, String identifier,
                                                   @DrawableRes int iconRes, Content module) {

        HoverMenu.SectionId sectionId = new HoverMenu.SectionId(identifier);
        ImageView tabView = MenuHelper.getTabView(context, iconRes);
        return new HoverMenu.Section(sectionId, tabView, module);

    }

    /**
     * Creates an entire module given the calling service context and a type of module to load.
     * @param context The calling context, such as the accessible hover menu service
     * @param type The MenuModuleType to load, such as GLOSSARY or HOME
     * @return the resulting section which can be loaded into the HoverMenu
     */
    public static HoverMenu.Section getModule(Context context, MenuModuleType type) {
        switch (type) {
            case GLOSSARY:
                return getGlossaryModule(context);
            case HOME:
                return getHomeModule(context);
            case INSTRUCTIONS:
                return getInstructionsModule(context);
            case LANGUAGE:
                return getLanguageModule(context);
            default:
                throw new RuntimeException("Module type is not a valid module.");
        }
    }

    /**
     * This class should only be interacted with through the <code>getModule()</code> method.
     */
    private ModuleLoader() {}

}
