package io.github.ama_csail.ama;

import android.test.mock.MockContext;

import org.junit.Assert;
import org.junit.Test;

import io.github.ama_csail.ama.testserver.automatons.SimpleAutomataAction;
import io.github.ama_csail.ama.testserver.automatons.SimpleAutomataState;
import io.github.ama_csail.ama.testserver.automatons.SimpleAutomaton;
import io.github.ama_csail.ama.util.Contrast;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void contrastIsCorrect() throws Exception {
        MockContext mock = new MockContext();

        int color1 = 0x00666666;
        int color2 = 0x00FFFFFF;

        double contrast = AMA.getContrastInt(mock, color1, color2);
        assertEquals(5.74, contrast, 0.01);

        assertTrue(AMA.satisfiesContrastInt(mock, color1, color2, Contrast.WCAG_AA_NORMAL_TEXT));
        assertFalse(AMA.satisfiesContrastInt(mock, color1, color2, Contrast.WCAG_AAA_NORMAL_TEXT));

    }

    @Test
    public void drawAutomaton() {

        SimpleAutomataState startScreen = new SimpleAutomataState("Start Screen");
        SimpleAutomataState swipedScreen = new SimpleAutomataState("Start Screen\\nButton in Focus");
        SimpleAutomataState informationScreen = new SimpleAutomataState("Information Screen");
        SimpleAutomataState timeoutScreen = new SimpleAutomataState("Timeout Screen");

        SimpleAutomataAction swipeAction = new SimpleAutomataAction("Swiped");
        SimpleAutomataAction clickedButton = new SimpleAutomataAction("Clicked button");
        SimpleAutomataAction timedOut = new SimpleAutomataAction("Process timeout");

        SimpleAutomaton auto = new SimpleAutomaton(startScreen);
        auto.addTransition(startScreen, swipeAction, swipedScreen);
        auto.addTransition(swipedScreen, clickedButton, informationScreen);
        auto.addTransition(startScreen, timedOut, timeoutScreen);
        auto.addTransition(swipedScreen, timedOut, timeoutScreen);

        System.out.println(auto.getStringForGraphViz());
        auto.writeDotFile();
        auto.dotFileToPng();
        auto.displayAutomatonImage();
        //Assert.assertTrue(false);

    }

}