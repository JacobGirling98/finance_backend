package helpers.fixtures

import domain.AddedBy
import domain.Category
import domain.Date
import domain.Description
import domain.Inbound
import domain.Outbound
import domain.PageNumber
import domain.PageSize
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.Value
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

val date = Date(LocalDate.of(2020, 1, 1))
val category = Category("Food")
val value = Value(BigDecimal.valueOf(1L))
val description = Description("Bananas")
val quantity = Quantity(1)
val outbound = Outbound("outbound")
val inbound = Inbound("inbound")
val recipient = Recipient("Parents")
val addedBy = AddedBy("Jacob")
val source = Source("Work")

val pageNumber = PageNumber(1)
val pageSize = PageSize(5)

val lastModified = LocalDateTime.of(2024, 1, 1, 0, 0)
val lastModifiedString = lastModified.format(ISO_LOCAL_DATE_TIME)
