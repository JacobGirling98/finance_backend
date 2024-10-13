package config.contract

import dao.asAuditableEntity
import dao.entityOf
import domain.*
import domain.Date
import http.asTag
import http.handler.addReminderHandler
import http.handler.advanceReminderHandler
import http.handler.outstandingRemindersHandler
import http.handler.updateReminderHandler
import http.lense.createdIdLens
import http.lense.reminderEntityLens
import http.lense.reminderEntityListLens
import http.lense.reminderLens
import http.model.CreatedId
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import resource.ReminderProcessor
import java.time.LocalDate
import java.util.*

private const val BASE_URL = "/reminders"
private val tag = BASE_URL.asTag()

fun reminderContracts(processor: ReminderProcessor) = listOf(
    addReminder(processor),
    getOutstandingReminders(processor),
    advanceReminder(processor),
    editReminder(processor)
)

fun getOutstandingReminders(processor: ReminderProcessor) = BASE_URL meta {
    operationId = BASE_URL
    summary = "Get outstanding reminders"
    tags += tag
    returning(
        Status.OK,
        reminderEntityListLens to
            listOf(
                entityOf(
                    Reminder(
                        Date(LocalDate.of(2023, 1, 1)),
                        Frequency.WEEKLY,
                        FrequencyQuantity(1),
                        Description("String")
                    )
                )
            )
    )
} bindContract Method.GET to outstandingRemindersHandler { processor.allRemindersDue() }

fun addReminder(processor: ReminderProcessor) = BASE_URL meta {
    operationId = "$BASE_URL/post"
    summary = "Add a new reminder"
    tags += tag
    receiving(
        reminderLens to Reminder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            FrequencyQuantity(1),
            Description("String")
        )
    )
    returning(Status.OK, createdIdLens to CreatedId(UUID.randomUUID()))
} bindContract Method.POST to addReminderHandler { processor.addReminder(it) }

fun advanceReminder(processor: ReminderProcessor) = "$BASE_URL/advance" meta {
    operationId = "$BASE_URL/advance"
    summary = "Advance a reminder"
    tags += tag
    receiving(createdIdLens to CreatedId(UUID.randomUUID()))
    returning(Status.NO_CONTENT)
} bindContract Method.POST to advanceReminderHandler { processor.markAsRead(it) }

fun editReminder(processor: ReminderProcessor) = BASE_URL meta {
    operationId = "$BASE_URL/put"
    summary = "Update a reminder"
    tags += tag
    receiving(
        reminderEntityLens to Reminder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            FrequencyQuantity(1),
            Description("String")
        ).asAuditableEntity(UUID.randomUUID())
    )
    returning(Status.NO_CONTENT)
} bindContract Method.PUT to updateReminderHandler { processor.updateReminder(it) }
