package http.handler

import dao.Entity
import domain.Reminder
import http.lense.reminderEntityListLens
import http.lense.reminderIdLens
import http.lense.reminderLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import java.util.*

fun advanceReminderHandler(advanceReminder: (UUID) -> Unit): HttpHandler = { request ->
    val id = reminderIdLens.extract(request).id
    advanceReminder(id)
    Response(Status.NO_CONTENT)
}

fun outstandingRemindersHandler(outstandingReminders: () -> List<Entity<Reminder>>): HttpHandler = {
    Response(Status.OK).with(reminderEntityListLens of outstandingReminders())
}

fun addReminderHandler(addReminder: (Reminder) -> UUID): HttpHandler = {
    val reminder = reminderLens.extract(it)
    addReminder(reminder)
    Response(Status.NO_CONTENT)
}