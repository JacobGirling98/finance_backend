import config.AppMode
import config.FinanceServer
import config.logger
import config.properties
import config.standingOrderProcessor

fun main() {
    standingOrderProcessor.schedule()

    FinanceServer(9000).start()

    if (properties.appMode == AppMode.DEV) {
        logger.info { "Serving Swagger at http://localhost:9000" }
    }
}
