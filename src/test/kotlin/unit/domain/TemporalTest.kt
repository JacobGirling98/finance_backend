package unit.domain

import domain.Date
import domain.Frequency
import domain.FrequencyQuantity
import domain.Temporal
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class TemporalTest : FunSpec({

    test("can advance by months") {
        val testObj = object : Temporal {
            override val date: Date = Date(LocalDate.of(2023, 1, 1))
            override val frequency: Frequency = Frequency.MONTHLY
            override val frequencyQuantity: FrequencyQuantity = FrequencyQuantity(2)
        }

        testObj.nextDate() shouldBe Date(LocalDate.of(2023, 3, 1))
    }

    test("can advance by weeks") {
        val testObj = object : Temporal {
            override val date: Date = Date(LocalDate.of(2023, 1, 1))
            override val frequency: Frequency = Frequency.WEEKLY
            override val frequencyQuantity: FrequencyQuantity = FrequencyQuantity(2)
        }

        testObj.nextDate() shouldBe Date(LocalDate.of(2023, 1, 15))
    }
})