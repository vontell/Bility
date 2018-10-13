package org.vontech.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.gson.Gson

// Quick jackson casting taken from https://gist.github.com/remen/ad239a5b599fc03d371770c1abf7bfa3

inline fun <reified T : Any> Any.cast() : T {
    //return ObjectMapperSingleTon.INSTANCE.(this, T::class.java)
    return ObjectMapperSingleTon.INSTANCE.convertValue(this, T::class.java)
}

object ObjectMapperSingleTon {
    //val INSTANCE = Gson()
    val INSTANCE : ObjectMapper = ObjectMapper().apply {this.registerModule(KotlinModule())}
}