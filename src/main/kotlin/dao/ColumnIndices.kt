package dao

interface ColumnIndices

data class StandingOrderColumns(
    val nextDateColumn: Int,
    val frequencyColumn: Int,
    val categoryColumn: Int,
    val valueColumn: Int,
    val quantityColumn: Int,
    val descriptionColumn: Int,
    val transactionTypeColumn: Int,
    val recipientColumn: Int,
    val inboundColumn: Int,
    val outboundColumn: Int,
    val sourceColumn: Int,
    val outgoingColumn: Int,
    val idColumn: Int
): ColumnIndices

data class TransactionColumns(
    val dateColumn: Int,
    val categoryColumn: Int,
    val valueColumn: Int,
    val quantityColumn: Int,
    val descriptionColumn: Int,
    val transactionTypeColumn: Int,
    val recipientColumn: Int,
    val inboundColumn: Int,
    val outboundColumn: Int,
    val sourceColumn: Int,
    val outgoingColumn: Int
): ColumnIndices