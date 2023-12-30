import config.*


fun main() {
    standingOrderProcessor.schedule()

    FinanceServer(9000).start()

    if (properties.appMode == AppMode.DEV) {
        logger.info { "Serving Swagger at http://localhost:9000" }
    }
}
