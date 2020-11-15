package org.vontech.bility.core.utils

/**
 * A collection of helpers for lists
 * @author Aaron Vontell
 */

/**
 * Returns a random element using the specified [random] instance as the source of randomness.
 */
fun <E> List<E>.random(random: java.util.Random): E? = if (size > 0) get(random.nextInt(size)) else null