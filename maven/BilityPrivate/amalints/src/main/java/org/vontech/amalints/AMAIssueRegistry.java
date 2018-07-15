package org.vontech.amalints;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import java.util.Collections;
import java.util.List;

/**
 * A loaded registry of all issues that will be checked during the linting process
 * @author Aaron Vontell
 */
public class AMAIssueRegistry extends IssueRegistry {

    @Override
    public List<Issue> getIssues() {
        return Collections.singletonList(AMACodeDetector.ISSUE);
    }

}
