package domain

enum class TransactionType(val type: String) {
    CREDIT("Credit"),
    DEBIT("Debit"),
    BANK_TRANSFER("Bank Transfer"),
    PERSONAL_TRANSFER("Personal Transfer"),
    INCOME("Income")
}

fun transactionTypeFrom(value: String): TransactionType =
    TransactionType.entries.first { it.type.lowercase() == value.lowercase() }
