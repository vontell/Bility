package org.vontech.algorithms.personas

/**
 * A file containing code for representing a base persona. Special personas
 * should subclass or use this information, and then extend upon it.
 * @author Aaron Vontell
 * @created August 12th, 2018
 * @updated August 12th, 2018
 */
open class Person(val nickname: String) {

    open val baseType = "Person"

    open fun getDescription(): String {
        return "I am $nickname, a human, and that is all. I do not do anything with user interfaces."
    }



}