package org.vontech.algorithms.personas.utils

import org.vontech.core.interfaces.Percept


class Propagator {

    var backgroundColorPercept: Percept? = null

    fun duplicate(): Propagator {
        val p = Propagator()
        p.backgroundColorPercept = backgroundColorPercept
        return p
    }

}