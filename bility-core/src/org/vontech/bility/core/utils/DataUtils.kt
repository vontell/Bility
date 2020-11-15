package org.vontech.androidserver.utils

fun <K, V> reverseMap(map: Map<K, V>): HashMap<V, MutableList<K>> {

    val newMap = HashMap<V, MutableList<K>>()
    map.forEach { t, u ->
        if (u !in newMap.keys) {
            newMap[u] = mutableListOf()
        }
        newMap[u]!!.add(t)
    }
    return newMap

}