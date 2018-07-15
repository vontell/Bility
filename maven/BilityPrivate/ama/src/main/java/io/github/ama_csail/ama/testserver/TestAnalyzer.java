package io.github.ama_csail.ama.testserver;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;


/**
 * Analyzes the test files pulled from a device which ran an AMA instrumented accessibility test
 * @author Aaron Vontell
 */
public class TestAnalyzer {

    public static void main(String[] args) {

        try {
            Builder parser = new Builder();
            Document doc = parser.build("/Users/vontell/Desktop/viewFile.xml");
            Element root = doc.getRootElement();

            List<Element> elQ = new ArrayList<>();
            elQ.add(root);

            while (!elQ.isEmpty()) {

                // Get the newest node in this hierarchy
                Element popped = elQ.remove(0);

                // Get desired information from this node
                String className = popped.getAttributeValue("class");
                if (className != null && className.toLowerCase().contains("image")) {
                    String desc = popped.getAttributeValue("content-desc");
                    if (desc == null || desc.equals("")) {
                        String bounds = popped.getAttributeValue("bounds");
                        System.out.println("Image at " + bounds + " does not have a content description");
                    }
                }


                // Finish off the processing by adding this node's children
                Elements newChildren = popped.getChildElements();
                for (int i = 0; i < newChildren.size(); i++) {
                    elQ.add(newChildren.get(i));
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
