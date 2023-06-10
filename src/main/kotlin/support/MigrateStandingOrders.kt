package support

import config.mongoClient
import config.properties
import dao.StandingOrdersDatabase
import dao.mongo.StandingOrderCollection

fun main() {
    val standingOrdersCsv = StandingOrdersDatabase(properties.dataLocation)
    val standingOrderCollection = StandingOrderCollection(mongoClient)
    standingOrdersCsv.read()
    standingOrdersCsv.data.forEach { standingOrderCollection.add(it) }
}