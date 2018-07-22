package org.vontech.androidserver.services

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.vontech.androidserver.*


object MongoDAO {

    private val client: MongoClient = KMongo.createClient(databaseHost, databasePort)
    val database: MongoDatabase = client.getDatabase(databaseName)

}