package org.vontech.core.interfaces

import org.vontech.algorithms.hci.getAccessibilityHashes
import org.vontech.utils.cast

/**
 * A condensed state
 */
class CondensedState(val literalInterace: LiteralInterace) {

    // First, get hashes (and some organized perceptifers for ease of use)
    val hashResults = getAccessibilityHashes(literalInterace)
    // Setup a tree of ids
    val rootForIdTree = TreeNode(getRoot(hashResults.idsToPerceptifers.values).id)

    init {
        buildTree(rootForIdTree, hashResults.idsToPerceptifers)

        val shouldIgnoreCounts = true
        if (shouldIgnoreCounts) {

        }

    }


    fun getStringRepresentation(): String {
        return rootForIdTree.getPrettyPrintString({
            "${getShortName(hashResults.idsToPerceptifers[it]!!)} ${hashResults.idsToHashes[it].toString()}"
        })
    }

    fun differenceBetween(state: CondensedState): Set<Percept> {

        val myPercepts = literalInterace.perceptifers.map {
            val combination = mutableSetOf<Percept>()
            combination.addAll(it.percepts!!)
            combination.addAll(it.virtualPercepts!!)
            combination
        }.flatten().toSet()
        val otherPercepts = state.literalInterace.perceptifers.map {
            val combination = mutableSetOf<Percept>()
            combination.addAll(it.percepts!!)
            combination.addAll(it.virtualPercepts!!)
            combination
        }.flatten()

        return myPercepts.union(otherPercepts).minus(myPercepts.intersect(otherPercepts))

    }

    override fun equals(other: Any?): Boolean {
        if (other is CondensedState) {
            val otherState = other.cast<CondensedState>()
            return otherState.hashResults.idsToHashes[other.rootForIdTree.value] ==
                    this.hashResults.idsToHashes[this.rootForIdTree.value]
        }
        return false
    }

    override fun hashCode(): Int {
        return this.hashResults.idsToHashes[this.rootForIdTree.value]!!
    }

    override fun toString(): String {
        return this.hashResults.idsToHashes[this.rootForIdTree.value].toString()
    }

}

private fun buildTree(perceptifer: TreeNode<String>, idsToPerceptifers: HashMap<String, Perceptifer>) {
    val children = getIdsOfChildren(idsToPerceptifers[perceptifer.value]!!)
    children.forEach {
        val newNode = TreeNode(idsToPerceptifers[it]!!.id)
        newNode.parent = perceptifer
        perceptifer.addChild(newNode)
        buildTree(newNode, idsToPerceptifers)
    }
}

class TreeNode<T>(value:T){
    var value:T = value
    var parent:TreeNode<T>? = null
    var children:MutableList<TreeNode<T>> = mutableListOf()

    fun addChild(node:TreeNode<T>){
        children.add(node)
        node.parent = this
    }

    fun getPrettyPrintString(converter: (T) -> String, depth: Int = 0): String {
        var s = converter(value)
        if (!children.isEmpty()) {
            s += " {" + children.map { "\n" + "\t".repeat(depth + 1) + it.getPrettyPrintString(converter, depth + 1) } + " \n${"\t".repeat(depth)}}"
        }
        return s
    }

    override fun toString(): String {
        var s = "${value}"
        if (!children.isEmpty()) {
            s += " {" + children.map { it.toString() } + " }"
        }
        return s
    }
}