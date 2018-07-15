package org.vontech.amalints;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UClassLiteralExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.ULiteralExpression;
import org.jetbrains.uast.UastLiteralUtils;

import java.util.Collections;
import java.util.List;

/**
 * Detector for linting with respect to the AMA project
 */
public class AMACodeDetector extends Detector implements Detector.UastScanner {

    /** Issue describing the problem and pointing to the detector implementation */
    public static final Issue ISSUE = Issue.create(
            // ID: used in @SuppressLint warnings etc
            "ShortUniqueId",

            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            "Lint Mentions",

            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            "This check highlights string literals in code which mentions " +
                    "the word `lint`. Blah blah blah.\n" +
                    "\n" +
                    "Another paragraph here.\n",
            Category.A11Y,
            6,
            Severity.WARNING,
            new Implementation(
                    AMACodeDetector.class,
                    Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(ULiteralExpression.class);
    }

    @Override
    public void visitClass(JavaContext context, UClass uClass) {
        //super.visitClass(uClass);

        // If an Android activity is not an AccessibleActivity, give a small warning
        context.report(ISSUE, uClass, context.getNameLocation(uClass), "Number of supers is " + uClass.getSupers().length);

    }

    @Override
    public UElementHandler createUastHandler(final JavaContext context) {
        // Not: Visiting UAST nodes is a pretty general purpose mechanism;
        // Lint has specialized support to do common things like "visit every class
        // that extends a given super class or implements a given interface", and
        // "visit every call site that calls a method by a given name" etc.
        // Take a careful look at UastScanner and the various existing lint check
        // implementations before doing things the "hard way".
        // Also be aware of context.getJavaEvaluator() which provides a lot of
        // utility functionality.
        return new UElementHandler() {

            @Override
            public void visitLiteralExpression(ULiteralExpression expression) {
                String string = UastLiteralUtils.getValueIfStringLiteral(expression);
                if (string == null) {
                    return;
                }

                System.out.println(string);

                if (string.contains("lint") && string.matches(".*\\blint\\b.*")) {
                    context.report(ISSUE, expression, context.getLocation(expression),
                            "v1111");
                }
            }

        };
    }

}
