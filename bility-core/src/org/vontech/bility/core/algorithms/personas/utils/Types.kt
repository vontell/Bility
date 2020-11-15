package org.vontech.bility.core.algorithms.personas.utils

/**
 * A collection of basic types for Personas on the Bility platform
 * @author Aaron Vontell
 * @date August 7th, 2018
 */

/**
 * The context of a Persona dictates the behavior and goals
 * of the Persona for this specific experiment.
 */
data class PersonaContext (
    val desiredState: Any
)