package io.github.ama_csail.ama.testserver.automatons;

/**
 * Created by vontell on 6/24/18.
 */

public class SimpleAutomataAction {

    private String label;

    public SimpleAutomataAction(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleAutomataAction that = (SimpleAutomataAction) o;

        return label != null ? label.equals(that.label) : that.label == null;
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }
}
