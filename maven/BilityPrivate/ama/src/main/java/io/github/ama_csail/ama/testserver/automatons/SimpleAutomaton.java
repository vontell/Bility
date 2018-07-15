package io.github.ama_csail.ama.testserver.automatons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A representation of a user interface through an automaton. Use as a base class for more
 * complex automatons, such as timed automatons. This class supports both building and running
 * through a timed automaton, which is great for both dynamic testing and dynamic creation. This
 * automaton may also be non-deterministic.
 */
public class SimpleAutomaton {

    // The simple automaton has information for the typical automaton definition:
    //  states - A set of states facilitated by the user interface
    //  alphabet - A set of actions, such as user actions, device actions, etc
    //  acceptStates - A set of states that are not reject states
    //  startState - A state to start in
    //  transitions - A mapping of states to actions  that lead to new states

    private Set<SimpleAutomataState> states;
    private Set<SimpleAutomataAction> alphabet;
    private Set<SimpleAutomataState> acceptStates;
    private SimpleAutomataState startState;
    private Map<SimpleAutomataState, Set<Pair<SimpleAutomataAction, SimpleAutomataState>>> transitions;
    private SimpleAutomataState currentState;

    public SimpleAutomaton(SimpleAutomataState startState) {
        init();
        this.startState = startState;
        this.currentState = startState;
    }

    public SimpleAutomaton() {
        init();
    }

    private void init() {
        states = new HashSet<>();
        alphabet = new HashSet<>();
        acceptStates = new HashSet<>();
        transitions = new HashMap<>();
    }

    public boolean addState(SimpleAutomataState state) {
        return states.add(state);
    }

    public boolean addAction(SimpleAutomataAction action) {
        return alphabet.add(action);
    }

    public boolean addTransition(SimpleAutomataState state, SimpleAutomataAction action, SimpleAutomataState result) {

        // First, if these states and actions do not exist, add them
        states.add(state);
        states.add(result);
        alphabet.add(action);

        // Now add the new pair, returning true if this is actually new
        Pair<SimpleAutomataAction, SimpleAutomataState> actionResult = new Pair<>(action, result);
        if (transitions.containsKey(state)) {
            Set<Pair<SimpleAutomataAction, SimpleAutomataState>> possible = transitions.get(state);
            return !possible.contains(actionResult) && possible.add(actionResult);
        } else {
            HashSet<Pair<SimpleAutomataAction, SimpleAutomataState>> possibles = new HashSet<>();
            possibles.add(actionResult);
            transitions.put(state, possibles);
            return true;
        }

    }

    public void reset() {
        this.currentState = this.startState;
    }

    public boolean transitionFromCurrentState(SimpleAutomataAction action, SimpleAutomataState result) {
        boolean transitionAdded = this.addTransition(this.currentState, action, result);
        this.currentState = result;
        return transitionAdded;
    }

    public SimpleAutomataState transition(SimpleAutomataAction action) {
        return null;
    }

    public String getStringForGraphViz() {
        StringBuilder dotFile = new StringBuilder();
        dotFile.append("digraph {\n\t");                    // Define a digraph
        dotFile.append("rankdir=LR;\n\t");                  // Draw the graph from left to right
        dotFile.append("node [shape = doublecircle]; \"");    // Draw a double circle around start state
        dotFile.append(this.startState.toString());
        dotFile.append("\";\n\tnode [shape = circle];\n\t");      // All the rest should be circles
        for (SimpleAutomataState state : transitions.keySet()) {
            Set<Pair<SimpleAutomataAction, SimpleAutomataState>> actionMappings = transitions.get(state);
            for (Pair<SimpleAutomataAction, SimpleAutomataState> actionMap : actionMappings) {
                SimpleAutomataAction action = actionMap.first;
                SimpleAutomataState newState = actionMap.second;
                dotFile.append("\"");
                dotFile.append(state.toString());               // Create a new state mapping going from here...
                dotFile.append("\" -> \"");
                dotFile.append(newState.toString());            // .... to here ....
                dotFile.append("\" [ label = \"");                // through this action
                dotFile.append(action.toString());
                dotFile.append("\" ];\n\t");
            }
        }
        String finalString = dotFile.toString();
        finalString = finalString.trim();
        finalString += "\n}";
        return finalString;

    }

    public void writeDotFile() {

        try {
            File file = new File("/Users/vontell/Desktop/auto.dot");
            file.createNewFile();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            writer.write(getStringForGraphViz());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void dotFileToPng() {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("dot -Tpng /Users/vontell/Desktop/auto.dot -o /Users/vontell/Desktop/auto.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayAutomatonImage() {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("open /Users/vontell/Desktop/auto.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
