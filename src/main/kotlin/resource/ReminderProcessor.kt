package resource

import dao.Database
import dao.Entity
import domain.Reminder
import java.time.LocalDate
import java.util.*

class ReminderProcessor(private val database: Database<Reminder, UUID>, private val now: () -> LocalDate) {
    fun markAsRead(reminder: Entity<Reminder>) {
        val nextDate = reminder.domain.nextDate()
        val newEntity = Entity(reminder.id, reminder.domain.copy(date = nextDate))
        database.update(newEntity)
    }

    fun allRemindersDue(): List<Entity<Reminder>> =
        database.selectAll().filter { it.domain.date.value.isBefore(now()) }

}