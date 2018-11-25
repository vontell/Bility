package org.vontech.bilitytester;

/**
 * Created by vontell on 11/23/18.
 */
public class BilityTestConfig {

    private Integer maxActions = 500;
    private String packageName = null;
    private Integer seed = null;

    public Integer getMaxActions() {
        return maxActions;
    }

    public void setMaxActions(Integer maxActions) {
        this.maxActions = maxActions;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }
}
