package org.vontech.algorithms.hci

import org.vontech.androidserver.utils.reverseMap
import org.vontech.core.interfaces.*
import java.util.*
import kotlin.collections.HashMap

/**
 * The process for grouping similar components of user interfaces
 * (for the purposes of accessibility) is done through the following
 * steps (note we basically just separate things on dividing percepts,
 * which for accessibility means splitting percepts based on things that
 * actually affect accessibility)
 *
 *  Repeat the following for any perceptifer that does not have children:
 *      1. Grab accessibility-related percepts
 *      2. Hash these percepts, mapping this view to an ID that holds these percepts
 *      3.
 */


fun getAccessibilityStateFromLiteralInterface(literalInterace: LiteralInterace) {

    // 0. Create a map of IDs -> Perceptifers by traversing through the perceptifers.
    // The Int is the depth of that element. First find the root (the only item with CHILDREN_SPATIAL_RELATIONS
    // but no one has pointers to them. O(n)
    val idPerceptiferPairings = literalInterace.perceptifers.map {it.id to it}.toTypedArray()
    val ps = hashMapOf(*idPerceptiferPairings)
    println("ps: $ps")
    println("ps keys: ${ps.keys}")

    // 1. Find the root of the tree through child elimination. O(n)
    val rootPerceptifer = ps.values.first {it.virtualPercepts!!.any { it.type == PerceptType.VIRTUAL_ROOT }}
    println("Root node: $rootPerceptifer")

    // 2. Recurse top to bottom, hashing children and propagating upward (this allows for one-time computation
    //    of layout percepts. Fill the idsToHash map.
    val idsToHash = HashMap<String, Int>()
    println("About to get hashes...")
    performPerceptiferAccessibilityHash(rootPerceptifer, ps, idsToHash)
    println("Obtained hashes!")

    // 3. Reverse idsToHash to get M, the mapping of hashes to similar elements
    val m = reverseMap(idsToHash)
    println("MMMMMMMMMMMMMMM")
    println(m)

    // 4. Reduction
    // Using
    m.values.forEach {
        println("GROUPING (${it.size})--------------")
        it.forEach {
            val perceptifer = ps[it]!!
            println("\t${perceptifer.id}")
            perceptifer.percepts!!.forEach {
                println("\t\t(R) $it")
            }
            perceptifer.virtualPercepts!!.forEach {
                println("\t\t(V) $it")
            }
        }
    }



}

/**
 * Represents an accessibility-related hash of a Perceptifer, used
 * in condensed state creation. The following percepts are tracked:
 *  ALL:
 *      ALPHA
 *      BACKGROUND_COLOR
 *  TEXT:
 *      FONT_SIZE
 *      FONT_STYLE
 *      LINE_SPACING
 *      TEXT_COLOR
 *  IMAGE:
 *      (none)
 *  CONTAINER:
 *      CHILDREN_SPATIAL_RELATIONS
 */
val ACCESSIBILITY_PERCEPTS = listOf(
        //PerceptType.ALPHA,
        PerceptType.BACKGROUND_COLOR,
        //PerceptType.FONT_SIZE,
        PerceptType.FONT_STYLE,
        //PerceptType.LINE_SPACING,
        PerceptType.VIRTUAL_NAME,
        PerceptType.TEXT_COLOR//,
        //PerceptType.CHILDREN_SPATIAL_RELATIONS
)

fun performPerceptiferAccessibilityHash(perceptifer: Perceptifer, perceptiferMap: HashMap<String, Perceptifer>, hashCache: HashMap<String, Int>): Int {

    var perceptsList: List<Percept> = perceptifer.percepts?.filter {
        it.type in ACCESSIBILITY_PERCEPTS
    } ?: listOf()

    perceptsList += perceptifer.virtualPercepts?.filter {
        it.type in ACCESSIBILITY_PERCEPTS
    } ?: listOf()

    // remove the order constraints of percepts
    val percepts = perceptsList.toSet()

    println("Percepts to hash: " + percepts)

    // First, traverse and compute children if needed (making sure to stay in order)
    val children = getIdsOfChildren(perceptifer)
    println("Children ids: $children")
    if (children.isNotEmpty()) {
        val childHashes = children.map { performPerceptiferAccessibilityHash(perceptiferMap[it]!!, perceptiferMap, hashCache) }
        println("Child hashes: " + childHashes)
        val hash = Objects.hash(childHashes, percepts)
        hashCache.put(perceptifer.id, hash)
        return hash
    }

    // We have reached a leaf - store and return
    val hash =  percepts.hashCode()
    hashCache.put(perceptifer.id, hash)
    return hash

}