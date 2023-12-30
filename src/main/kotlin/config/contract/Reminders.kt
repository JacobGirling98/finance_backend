package config.contract

import dao.entityOf
import domain.*
import http.asTag
import http.handler.outstandingRemindersHandler
import http.lense.reminderEntityListLens
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import resource.ReminderProcessor
import java.time.LocalDate

private const val BASE_URL = "/reminders"
private val tag = BASE_URL.asTag()

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
                            Description("String"),
                        )
                    )
                )
    )
} bindContract Method.GET to outstandingRemindersHandler { processor.allRemindersDue() }