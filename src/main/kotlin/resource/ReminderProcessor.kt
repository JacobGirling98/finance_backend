package resource

import dao.Database
import dao.Entity
import domain.Reminder
import java.time.LocalDate
import java.util.*

class ReminderProcessor(private val database: Database<Reminder, UUID>, private val now: () -> LocalDate) {
    fun markAsRead(id: UUID) {
        val reminder = database.findById(id) ?: throw RuntimeException("No matching reminder found")
        val nextDate = reminder.domain.nextDate()
        val newEntity = Entity(reminder.id, reminder.domain.copy(date = nextDate), reminder.lastModified)
        database.update(newEntity)
    }

    fun allRemindersDue(): List<Entity<Reminder>> =
        database.selectAll().filter { it.domain.date.value.isBefore(now()) }

    fun addReminder(reminder: Reminder): UUID = database.save(reminder)

    fun updateReminder(reminder: Entity<Reminder>) {
        database.update(reminder)
    }
}
