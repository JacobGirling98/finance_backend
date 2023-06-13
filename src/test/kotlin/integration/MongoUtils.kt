package integration

import com.mongodb.client.result.DeleteResult
import config.mongoClient
import config.properties
import org.bson.Document

fun deleteFrom(collection: String): DeleteResult =
    mongoClient.getDatabase(properties.mongo.database).getCollection(collection).deleteMany(Document())