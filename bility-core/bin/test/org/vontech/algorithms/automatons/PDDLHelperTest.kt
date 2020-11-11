package org.vontech.algorithms.automatons

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.FeatureSpec
import java.io.IOException


class PDDLHelperTest: FeatureSpec({

    feature("the Automaton state object") {
        scenario("should be converted into a PDDL Domain object") {

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

            val domain = createBaseDomain(automaton, "automatondomain")
            val problem = """
            (define (problem ui-navigation-one)
                (:domain automatondomain)
                (:objects
                    start middle end - state
                    t1 t2 - transition
                    click - modality)
                (:init
                    (is-in-state start)

                    (can-move start t1 middle)
                    (can-move middle t2 end)

                    (uses-modality t1 click)
                    (uses-modality t2 click)

                    (can-use-modality click)


                    (= (transition-time t1) 0.1)
                    (= (transition-time t2) 0.2))
                (:goal
                    (and
                        (is-in-state end)))
                (:metric minimize (total-time)))"""

            println(problem)
//            val factory = ProblemFactory.getInstance()
//            var errorManager: ErrorManager? = null
//            try {
//                errorManager = factory.parseFromString(domain, problem)
//                if (!errorManager!!.isEmpty) {
//                    println(errorManager!!.messages)
//                }
//                val codedProblem = factory.encode()
//                println("Encoding problem done successfully ("
//                        + codedProblem.operators.size + " ops, "
//                        + codedProblem.relevantFacts.size + " facts).")
//                if (!codedProblem.isSolvable) {
//                    println("Goal can be simplified to FALSE. No search will solve it.")
//                } else {
//                    val planner =  HSP()
//                    val plan = planner.search(codedProblem)
//                    if (plan != null) {
//                        println("Found plan as follows:")
//                        println(codedProblem.toString(plan))
//                    } else {
//                        println("No plan found.")
//                    }
//                }
//            } catch (e: IOException) {
//                println("Unexpected error when parsing the PDDL planning problem description.")
//                println(e.message)
//                System.exit(0)
//            }


        }
    }

})