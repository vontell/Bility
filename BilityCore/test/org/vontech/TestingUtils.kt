package org.vontech

import org.vontech.algorithms.automatons.Automaton
import org.vontech.algorithms.automatons.AutomatonState
import org.vontech.algorithms.automatons.AutomatonTransition
import org.vontech.core.interaction.InputInteractionType
import org.vontech.core.interaction.UserAction
import org.vontech.core.interfaces.*

fun getLoginAndHomePageAutomaton(): Automaton<CondensedState, UserAction> {

    // Create the literal interface for the login screen
    val loginButton = Perceptifer(
        percepts = mutableSetOf(
            Percept(PerceptType.TEXT, "Login")
        ),
        virtualPercepts = mutableSetOf()
    )
    val loginInterface = LiteralInterace(
        perceptifers = setOf(loginButton),
        outputChannels = setOf(),
        inputChannels = setOf(),
        metadata = LiteralInterfaceMetadata()
    )

    // Create the literal interface for the home screen
    val mainPageText = Perceptifer(
            percepts = mutableSetOf(
                Percept(PerceptType.TEXT, "Main Screen")
            ),
            virtualPercepts = mutableSetOf()
    )
    val mainInterface = LiteralInterace(
            perceptifers = setOf(mainPageText),
            outputChannels = setOf(),
            inputChannels = setOf(),
            metadata = LiteralInterfaceMetadata()
    )

    // Generate and return the automaton
    val loginState = AutomatonState(CondensedState(loginInterface))
    val mainState = AutomatonState(CondensedState(mainInterface))
    val automaton = Automaton<CondensedState, UserAction>(loginState)

    val action = UserAction(InputInteractionType.CLICK, loginButton)

    automaton.addTransition(loginState, AutomatonTransition(action), mainState)

    return automaton

}