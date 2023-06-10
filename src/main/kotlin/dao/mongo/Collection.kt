package dao.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import config.properties
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.ZoneId


abstract class Collection<T>(private val client: MongoClient) {

    protected abstract fun T.toDocument(): Document
    protected abstract fun Document.toDomain(): T
    protected abstract val name: String

    protected val collection: () -> MongoCollection<Document> = {
        client
            .getDatabase(properties.mongo.database)
            .getCollection(name)
    }


    fun add(domain: T): String? = collection().insertOne(
        Document(domain.toDocument())
    ).insertedId?.asObjectId()?.value?.toHexString()

    fun findById(id: String): Entity<T>? = collection().find(eq(ObjectId(id))).first()?.let {
        Entity(id, it.toDomain())
    }

    protected fun Document.getLocalDate(field: String): LocalDate = getDate(field)
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    fun findAll(): List<Entity<T>> {
        val documents = mutableListOf<Entity<T>>()
        val cursor = collection().find().cursor()
        while (cursor.hasNext()) {
            documents.add(cursor.next().let { Entity(it.getObjectId("_id").toHexString(), it.toDomain()) })
        }
        return documents.toList()
    }

    fun update(entity: Entity<T>) {
        collection().replaceOne(eq(ObjectId(entity.id)), entity.domain.toDocument())
    }
}