package org.vontech.bility.server.services

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*
import org.vontech.bility.server.*


object Mongo {

    private val client: com.mongodb.client.MongoClient = KMongo.createClient(databaseURI)
    //private val client: MongoClient = KMongo.createClient(databaseHost, databasePort)
    val database: MongoDatabase = client.getDatabase(databaseName)

}