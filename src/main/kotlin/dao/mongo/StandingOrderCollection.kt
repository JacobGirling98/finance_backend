package dao.mongo

import com.mongodb.client.MongoClient
import domain.*
import org.bson.Document
import org.bson.types.Decimal128

class StandingOrderCollection(client: MongoClient) : Collection<StandingOrder>(client) {
    override fun StandingOrder.toDocument() = Document(
        mapOf(
            "nextDate" to nextDate.value,
            "frequency" to frequency.value,
            "outgoing" to outgoing.value,
            "value" to value.value,
            "transactionType" to type.type,
            "description" to description.value,
            "category" to category.value,
            "quantity" to quantity.value,
            "recipient" to recipient?.value,
            "inbound" to inbound?.value,
            "outbound" to outbound?.value,
            "source" to source?.value
        )
    )

    override fun Document.toDomain() = StandingOrder(
        Date(getLocalDate("nextDate")),
        frequencyFrom(getString("frequency")),
        Category(getString("category")),
        Value.of(get("value", Decimal128::class.java).toDouble()),
        Description(getString("description")),
        transactionTypeFrom(getString("transactionType")),
        Outgoing(getBoolean("outgoing")),
        Quantity(getInteger("quantity")),
        getString("recipient")?.let { Recipient(it) },
        getString("inbound")?.let { Inbound(it) },
        getString("outbound")?.let { Outbound(it) },
        getString("source")?.let { Source(it) }
    )

    override val name: String = "standingOrders"
}