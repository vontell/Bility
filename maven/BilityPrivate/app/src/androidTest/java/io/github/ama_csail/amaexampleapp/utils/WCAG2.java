package io.github.ama_csail.amaexampleapp.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Definitions and methods to test WCAG 2.0 guidelines on aspects of an application
 * For more information, see https://www.w3.org/TR/WCAG20/
 * @author Aaron Vontell
 */
public class WCAG2 {

    public enum Level {
        A, AA, AAA
    }

    // PRINCIPLE 1: Perceivable - Information and user interface components must be
    // presentable to users in ways they can perceive.

    // Guideline 1.1 Text Alternatives

    // A list of bad content descriptions
    private static final List<String> BAD_DESC = Arrays.asList(
            "button",
            "image",
            "view",
            "description",
            "content",
            "hello world!",
            "example",
            ""
    );

    /**
     * 1.1.1 Non-text Content: All non-text content that is presented to the user has a text
     * alternative that serves the equivalent purpose, except for the situations listed below.
     *      (Level A) - https://www.w3.org/TR/UNDERSTANDING-WCAG20/text-equiv-all.html
     * @param view The view to check for WCAG2.0 1.1.1 satisfiability
     */
    public static TestResult testPrinciple_1_1_1(View view) {

        TestResult result = new TestResult(
                "WCAG 2.0 - 1.1.1 Non-text Content",
                "Non-text content that is presented to the user has a text alternative " +
                        "that serves the equivalent purpose",
                TestClassification.WCAG21_1_1
        );

        // If this is a TextView, then Android handles this automatically
        if (view instanceof TextView) {

            // However, if the text of this TextView is empty, it should have a content description
            TextView textView = (TextView) view;
            if (textView.getText() == null || textView.getText().length() == 0) {
                result.setPassed(false);
                result.setTestError("This TextView has no text, and should therefore have a content description.");
                result.setSuggestion("Please make sure that this TextView has a content description.");
                if (view.getId() > 0) result.setIdentifier(view.getResources().getResourceName(view.getId()));
                return result;
            }

            // If the content description is present but is not sufficient, report it
            if (textView.getContentDescription() != null && BAD_DESC.contains(textView.getContentDescription().toString().toLowerCase())) {
                result.setPassed(false);
                result.setTestError("This TextView has an insufficient content description.");
                result.setSuggestion("This text does have a content description, but it is not sufficient for providing information to a user.");
                if (view.getId() > 0) result.setIdentifier(view.getResources().getResourceName(view.getId()));
                return result;
            }

            result.setPassed(true);
            result.setSuggestion("No suggestion - TextViews do not need text alternatives.");
            if (view.getId() > 0) result.setIdentifier(view.getResources().getResourceName(view.getId()));
            return result;

        }

        // If this is an ImageView, it must have a content description
        if (view instanceof ImageView) {

            CharSequence contentDesc = view.getContentDescription();
            if ((contentDesc == null || contentDesc.length() == 0)) {

                // If the content description is present but is not sufficient, report it
                if (contentDesc != null && BAD_DESC.contains(contentDesc.toString().toLowerCase())) {
                    result.setPassed(false);
                    result.setTestError("This ImageView-type has an insufficient content description.");
                    result.setSuggestion("This image does have a content description, but it is not sufficient for providing information to a user.");
                    if (view.getId() > 0) result.setIdentifier(view.getResources().getResourceName(view.getId()));
                    return result;
                }

                result.setPassed(false);
                result.setTestError("This ImageView-type view does not have a content description.");
                result.setSuggestion("Please make sure that this ImageView has a content description.");
                if (view.getId() > 0) result.setIdentifier(view.getResources().getResourceName(view.getId()));
                return result;

            } else {
                result.setPassed(true);
                result.setSuggestion("No suggestion - ImageView does have alternative text.");
                if (view.getId() > 0) result.setIdentifier(view.getResources().getResourceName(view.getId()));
                return result;
            }

        }

        // If this view is not an text or an image, it may be a custom component, container, or
        // decoration.
        result.setPassed(true);
        result.setSuggestion("No suggestion - This may be a custom component, container, or decoration.");
        if (view.getId() > 0) result.setIdentifier(view.getResources().getResourceName(view.getId()));

        return result;

    }

    public static TestSuiteResult runTestSuiteWCAG2A(Activity activity, AppSpecification app) {

        TestSuiteResult testSuite = new TestSuiteResult(
                "WCAG 2.0 - Level A Satisfaction",
                "Runs tests to check if WCAG 2.0 has been satisfied at the A level.",
                "https://www.w3.org/WAI/WCAG20/quickref/?currentsidebar=%23col_customize&levels=aa%2Caaa");

        return testSuite;

    }

}
