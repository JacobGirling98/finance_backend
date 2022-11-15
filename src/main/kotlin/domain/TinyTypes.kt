package domain

import java.math.BigDecimal
import java.time.LocalDate

data class Date(
    val value: LocalDate
)

data class Category(
    val value: String
)

data class Value(
    val value: BigDecimal
)

data class Description(
    val value: String
)

data class Quantity(
    val value: Int
)

data class Recipient(
    val value: String
)

data class Inbound(
    val value: String
)

data class Outbound(
    val value: String
)

data class Source(
    val value: String
)

data class Outgoing(
    val value: Boolean
)