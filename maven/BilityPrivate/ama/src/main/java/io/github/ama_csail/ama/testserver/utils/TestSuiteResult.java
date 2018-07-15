package io.github.ama_csail.ama.testserver.utils;

/**
 * A collection of test results from an automated accessibility test
 * @author Aaron Vontell
 */
public class TestSuiteResult {

    private String testTitle;
    private String testDescription;
    private String testMoreInfo;

    public TestSuiteResult(String title, String description, String moreInfo) {
        this.testTitle = title;
        this.testDescription = description;
        this.testMoreInfo = moreInfo;
    }



}
