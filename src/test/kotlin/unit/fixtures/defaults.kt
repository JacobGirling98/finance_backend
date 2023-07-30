package unit.fixtures

import domain.*
import java.math.BigDecimal
import java.time.LocalDate

val date = Date(LocalDate.of(2020, 1, 1))
val category = Category("Food")
val value = Value(BigDecimal.valueOf(1L))
val description = Description("Bananas")
val quantity = Quantity(1)
val outbound = Outbound("outbound")
val inbound = Inbound("inbound")
val recipient = Recipient("Parents")