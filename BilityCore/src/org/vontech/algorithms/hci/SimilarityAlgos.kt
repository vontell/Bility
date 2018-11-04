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

data class AccessibilityHashResults(
        val hashesToIds: HashMap<Int, MutableList<String>>,
        val idsToHashes: HashMap<String, Int>,
        val idsToPerceptifers: HashMap<String, Perceptifer>
)


fun getAccessibilityHashes(literalInterace: LiteralInterace): AccessibilityHashResults {

    // 0. Create a map of IDs -> Perceptifers by traversing through the perceptifers.
    // The Int is the depth of that element. First find the root (the only item with CHILDREN_SPATIAL_RELATIONS
    // but no one has pointers to them. O(n)
    val idPerceptiferPairings = literalInterace.perceptifers.map {it.id to it}.toTypedArray()
    val ps = hashMapOf(*idPerceptiferPairings)
    //println("ps: $ps")
    //println("ps keys: ${ps.keys}")

    // 1. Find the root of the tree through child elimination. O(n)
    val rootPerceptifer = getRoot(ps.values)
    //println("Root node: $rootPerceptifer")

    // 2. Recurse top to bottom, hashing children and propagating upward (this allows for one-time computation
    //    of layout percepts. Fill the idsToHash map.
    val idsToHash = HashMap<String, Int>()
    //println("About to get hashes...")
    performPerceptiferAccessibilityHash(rootPerceptifer, ps, idsToHash, true) { baseAccessibilityTransform(it) }
    //println("Obtained hashes!")

    // 3. Reverse idsToHash to get M, the mapping of hashes to similar elements
    val m = reverseMap(idsToHash)
    //println("MMMMMMMMMMMMMMM")
    //println(m)

    // 4. Count Reduction: Provide a further-condensed version of this hash by ignoring
    //    repeated elements within the user interface. For instance, the following
    //    conversion would take place:
    //    A                 ->  A
    //      B               ->    B
    //        C             ->      C
    //      B               ->    D
    //        C             ->      E
    //      D               ->
    //        E             ->
    //        E             ->
    // NOTE: This is done by default now


    return AccessibilityHashResults(m, idsToHash, ps)

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
        PerceptType.ALPHA,
        PerceptType.BACKGROUND_COLOR,
        PerceptType.FONT_SIZE,
        PerceptType.FONT_STYLE,
        PerceptType.LINE_SPACING,
        PerceptType.VIRTUAL_NAME,
        PerceptType.VIRTUAL_FOCUSABLE,
        PerceptType.TEXT_COLOR//,
        //PerceptType.CHILDREN_SPATIAL_RELATIONS
)

fun baseAccessibilityTransform(perceptifer: Perceptifer): Set<Percept> {

    var perceptsList: List<Percept> = perceptifer.percepts?.filter {
        it.type in ACCESSIBILITY_PERCEPTS
    } ?: listOf()

    perceptsList += perceptifer.virtualPercepts?.filter {
        it.type in ACCESSIBILITY_PERCEPTS
    } ?: listOf()

    return perceptsList.toSet()

}



fun performPerceptiferAccessibilityHash(perceptifer: Perceptifer,
                                        perceptiferMap: HashMap<String, Perceptifer>,
                                        hashCache: HashMap<String, Int>,
                                        ignoreRepeatChildren: Boolean,
                                        filterAndTransform: (Perceptifer) -> Set<Percept>): Int {



    // remove the order constraints of percepts
    val percepts = filterAndTransform(perceptifer)

    //println("Percepts to hash: " + percepts)

    // First, traverse and compute children if needed (making sure to stay in order)
    val children = getIdsOfChildren(perceptifer)
    //println("Children ids: $children")
    if (children.isNotEmpty()) {
        var childHashes = children.map {
            performPerceptiferAccessibilityHash(perceptiferMap[it]!!, perceptiferMap, hashCache, ignoreRepeatChildren, filterAndTransform)
        }
        //println("Child hashes: " + childHashes)
        if (ignoreRepeatChildren) {
            childHashes = childHashes.toSet().toList()
        }
        val hash = Objects.hash(childHashes, percepts)
        hashCache.put(perceptifer.id, hash)
        return hash
    }

    // We have reached a leaf - store and return
    val hash =  percepts.hashCode()
    hashCache.put(perceptifer.id, hash)
    return hash

}