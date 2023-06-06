package integration

import config.mongoClient
import config.properties
import org.bson.Document

fun deleteFrom(collection: String) =
    mongoClient.getDatabase(properties.mongo.database).getCollection(collection).deleteMany(Document())