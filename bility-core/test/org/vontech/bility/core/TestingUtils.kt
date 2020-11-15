package org.vontech.bility.core

import org.vontech.bility.core.algorithms.automatons.Automaton
import org.vontech.bility.core.algorithms.automatons.AutomatonState
import org.vontech.bility.core.algorithms.automatons.AutomatonTransition
import org.vontech.bility.core.interaction.InputInteractionType
import org.vontech.bility.core.interaction.UserAction
import org.vontech.bility.core.interfaces.*

fun getLoginAndHomePageAutomaton(): Automaton<CondensedState, UserAction> {

    // Create the literal interface for the login screen
    val loginButton = Perceptifer(
        percepts = mutableSetOf(
            Percept(PerceptType.TEXT, "Login")
        ),
        virtualPercepts = mutableSetOf(
            Percept(PerceptType.VIRTUAL_ROOT, true)
        )
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
        virtualPercepts = mutableSetOf(
            Percept(PerceptType.VIRTUAL_ROOT, true)
        )
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