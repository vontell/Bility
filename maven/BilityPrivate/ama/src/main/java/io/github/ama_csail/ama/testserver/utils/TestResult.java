package io.github.ama_csail.ama.testserver.utils;

import android.support.annotation.NonNull;

/**
 * A structure which holds information regarding a test from an individual guideline
 */
public class TestResult {

    private boolean passed;
    private String testTitle;
    private String testDescription;
    private String testError;
    private String suggestion;
    private String identifier;
    private TestClassification classification;

    public TestResult(@NonNull String testTitle, @NonNull String testDescription, TestClassification classification) {
        this.testTitle = testTitle;
        this.testDescription = testDescription;
        this.classification = classification;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getTestError() {
        return testError;
    }

    public void setTestError(String testError) {
        this.testError = testError;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setClassification(TestClassification classification) {
        this.classification = classification;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public String getIdentifier() {
        return identifier;
    }

    public TestClassification getClassification() {
        return classification;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "TestResult {" + "\n" +
                "\tpassed=" + passed + ",\n" +
                "\ttestTitle='" + testTitle + '\'' + ",\n" +
                "\ttestDescription='" + testDescription + '\'' + ",\n" +
                "\tclassification='" + classification + '\'' + ",\n" +
                "\ttestError='" + testError + '\'' + ",\n" +
                "\tsuggestion='" + suggestion + '\'' + ",\n" +
                "\tidentifier='" + identifier + '\'' + ",\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestResult that = (TestResult) o;

        if (passed != that.passed) return false;
        if (classification != that.classification) return false;
        if (!testTitle.equals(that.testTitle)) return false;
        if (!testDescription.equals(that.testDescription)) return false;
        if (testError != null ? !testError.equals(that.testError) : that.testError != null)
            return false;
        if (suggestion != null ? !suggestion.equals(that.suggestion) : that.suggestion != null)
            return false;
        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        int result = (passed ? 1 : 0);
        result = 31 * result + classification.hashCode();
        result = 31 * result + testTitle.hashCode();
        result = 31 * result + testDescription.hashCode();
        result = 31 * result + (testError != null ? testError.hashCode() : 0);
        result = 31 * result + (suggestion != null ? suggestion.hashCode() : 0);
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        return result;
    }
}
