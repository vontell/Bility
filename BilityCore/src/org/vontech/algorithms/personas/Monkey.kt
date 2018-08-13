package org.vontech.algorithms.personas

/**
 * A representing of a user which clicks randomly through a user interface
 * (which is commonly called a Monkey in user-interface language).
 * @author Aaron Vontell
 * @created August 12th, 2018
 * @updated August 12th, 2018
 */
class Monkey(nickname: String): Person(nickname) {

    override val baseType: String = "Monkey"

    override fun getDescription(): String {
        return "I am $nickname, a Monkey! I will randomly click through a user interface!"
    }

}