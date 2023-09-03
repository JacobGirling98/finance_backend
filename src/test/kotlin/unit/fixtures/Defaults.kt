package unit.fixtures

import domain.Category
import domain.Date
import domain.Description
import domain.Inbound
import domain.Outbound
import domain.Quantity
import domain.Recipient
import domain.Value
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
