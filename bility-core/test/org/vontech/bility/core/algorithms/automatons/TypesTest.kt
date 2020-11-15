package org.vontech.bility.core.algorithms.automatons

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FeatureSpec

class DataTypesTest: FeatureSpec({

    feature("the Automaton state object") {
        scenario("should respect equality of inner state") {

            val stringStateOne = AutomatonState("Hello world")
            val stringStateTwo = AutomatonState("Hello world")
            val stringStateThree = AutomatonState("Hello worldz")
            val integerState = AutomatonState(2018)

            val obj = Object()
            val objStateOne = AutomatonState(obj)
            val objStateTwo = AutomatonState(Object())
            val objStateThree = AutomatonState(obj)

            stringStateOne shouldBe stringStateTwo
            stringStateThree shouldNotBe stringStateTwo
            stringStateThree shouldNotBe integerState
            objStateOne shouldNotBe objStateTwo
            objStateOne shouldBe objStateThree

        }
    }

    feature("the Automaton") {

        // First, created desired states and transitions
        val startState = AutomatonState("Start")
        val endState = AutomatonState("End")
        val middleState = AutomatonState("Middle")
        val startToMiddle = AutomatonTransition("Start to Middle Transition")
        val middleToEnd = AutomatonTransition("Middle to End Transition")

        // Now build the automaton
        val automaton = Automaton<String, String>(startState)
        automaton.addTransition(startState, startToMiddle, middleState)
        automaton.addTransition(middleState, middleToEnd, endState)

        scenario("should correctly instantiate and transition states") {

            // Verify the start state, move to the end, and validate the end state
            automaton.currentState shouldBe startState
            automaton.transition(startToMiddle)
            automaton.transition(middleToEnd)
            automaton.currentState shouldBe endState

            // Now verify the resetting of the Automaton
            automaton.reset()
            automaton.currentState shouldBe startState

        }

        scenario("should fail when making a non-existent transition") {

            // Reset and validate the automaton
            automaton.reset()
            automaton.currentState shouldBe startState

            // Attempt a bad transition
            shouldThrow<StateTransitionException> {
                automaton.transition(middleToEnd)
            }

        }

        scenario("should generate and write dot and png files") {

            automaton.writeDotFile()
            automaton.dotFileToPng()
            automaton.displayAutomatonImage()

        }

        scenario("should use hash code correctly") {

//            val hashResults = AccessibilityHashResults(
//
//            )
//            val tra1 = AutomatonTransition(UserAction(InputInteractionType.CLICK, emptyPerceptifer(), null))
//            val tra2 = AutomatonTransition(UserAction(InputInteractionType.CLICK, emptyPerceptifer(), null))
//            val transMap1 = hashMapOf(AutomatonTransition(tra1) to hashSetOf("yolo"))
//
//            transMap1.contains(AutomatonTransition(tra1)) shouldBe true
//            transMap1[AutomatonTransition(tra2)] = hashSetOf("wooooaaahhh")
//            transMap1.keys.size shouldBe 1

        }

    }

})