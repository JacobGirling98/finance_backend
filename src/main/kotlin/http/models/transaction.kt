package http.models

import domain.*

data class Credit(
    val date: Date,
    val category: Category,
    val value: Value,
    val description: Description,
    val quantity: Quantity,
)