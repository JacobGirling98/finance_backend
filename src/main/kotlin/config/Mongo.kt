package config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.UuidRepresentation

private val settings: MongoClientSettings = MongoClientSettings.builder()
    .uuidRepresentation(UuidRepresentation.STANDARD)
    .applyConnectionString(ConnectionString(properties.mongo.uri))
    .build()
val mongoClient: MongoClient = MongoClients.create(settings)