package unit.resource

import dao.Database
import dao.asEntity
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import resource.LoginSynchroniser
import java.time.LocalDate
import java.util.*

class LoginSynchroniserTest : FunSpec({

    val database = mockk<Database<LocalDate, UUID>>()

    val firstDay = LocalDate.of(2023, 7, 30)
    val firstDayId = UUID.randomUUID()

    val secondDay = firstDay.minusDays(1)
    val secondDayId = UUID.randomUUID()

    val thirdDay = secondDay.minusDays(1)
    val thirdDayId = UUID.randomUUID()

    val fourthDay = secondDay.minusDays(1)
    val fourthDayId = UUID.randomUUID()

    val synchroniser = LoginSynchroniser(database)

    test("if current date is already in database then it is not added again") {
        every { database.selectAll() } returns listOf(firstDay.asEntity(firstDayId))

        synchroniser.addLogin(firstDay)

        verify(exactly = 0) { database.save(firstDay) }
    }

    test("new login is added if it doesn't exist in the database") {
        every { database.selectAll() } returns listOf(secondDay.asEntity(secondDayId))
        every { database.save(any<LocalDate>()) } returns UUID.randomUUID()

        synchroniser.addLogin(firstDay)

        verify { database.save(firstDay) }
    }

    test("no logins are deleted if there are 3 or less in the database") {
        every { database.selectAll() } returns listOf(secondDay.asEntity(secondDayId), thirdDay.asEntity(thirdDayId))
        every { database.save(any<LocalDate>()) } returns UUID.randomUUID()

        synchroniser.addLogin(firstDay)

        verify(exactly = 0) { database.delete(any()) }
    }

    test("the oldest logins are deleted if there are more than 3 in the database") {
        every { database.selectAll() } returns listOf(
            secondDay.asEntity(secondDayId),
            thirdDay.asEntity(thirdDayId),
            fourthDay.asEntity(fourthDayId)
        )
        every { database.save(any<LocalDate>()) } returns UUID.randomUUID()
        every { database.delete(any()) } returns null

        synchroniser.addLogin(firstDay)

        verifyAll(true) {
            database.delete(firstDayId)
            database.delete(secondDayId)
            database.delete(thirdDayId)
        }
        verify { database.delete(fourthDayId) }
    }
})
