package unit.resource

import domain.DateRange
import domain.EndDate
import domain.StartDate
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.aWagesIncome
import helpers.fixtures.anEntity
import helpers.fixtures.withADateOf
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import resource.fiscalMonthsOf
import resource.fiscalYearsOf
import resource.monthsOf
import resource.yearsOf

class DateExtractorTest : DescribeSpec({

    describe("date ranges per calendar month") {
        it("can extract single date range") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 1, 10)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 1, 20))
                )
            }

            monthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 1, 1),
                    EndDate.of(2020, 2, 1)
                )
            )
        }

        it("can extract date range over two months") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 1, 10)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 2, 20))
                )
            }

            monthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 2, 1),
                    EndDate.of(2020, 3, 1)
                ),
                DateRange(
                    StartDate.of(2020, 1, 1),
                    EndDate.of(2020, 2, 1)
                )
            )
        }

        it("can extract date range over two years") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 12, 10)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2021, 1, 10))
                )
            }

            monthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2021, 1, 1),
                    EndDate.of(2021, 2, 1)
                ),
                DateRange(
                    StartDate.of(2020, 12, 1),
                    EndDate.of(2021, 1, 1)
                )
            )
        }

        it("can handle same month in different years") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 12, 10)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2021, 12, 10))
                )
            }

            monthsOf(transactions)() shouldContainAll listOf(
                DateRange(
                    StartDate.of(2021, 12, 1),
                    EndDate.of(2022, 1, 1)
                ),
                DateRange(
                    StartDate.of(2020, 12, 1),
                    EndDate.of(2021, 1, 1)
                )
            )
        }
    }

    describe("date ranges per calendar year") {
        it("can extract date range for single year") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 1, 10)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 2, 20))
                )
            }

            yearsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 1, 1),
                    EndDate.of(2021, 1, 1)
                )
            )
        }

        it("can extract date range for multiple years") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 1, 10)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2021, 2, 20)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2022, 3, 20))
                )
            }

            yearsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2022, 1, 1),
                    EndDate.of(2023, 1, 1)
                ),
                DateRange(
                    StartDate.of(2021, 1, 1),
                    EndDate.of(2022, 1, 1)
                ),
                DateRange(
                    StartDate.of(2020, 1, 1),
                    EndDate.of(2021, 1, 1)
                )
            )
        }
    }

    describe("date ranges per fiscal month") {
        it("single month with wages paid on the regular day") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("single month with wages paid on the regular day and other transactions") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 1, 20))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("two months with wages paid on the regular day") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 2, 15))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 15)
                ),
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("two months with first wage paid on a different day") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 14)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 2, 15))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 15)
                ),
                DateRange(
                    StartDate.of(2020, 1, 14),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("two months with second wage paid on a different day") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 2, 14))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 2, 14),
                    EndDate.of(2020, 3, 15)
                ),
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 14)
                )
            )
        }

        it("three months with middle wage paid on a different day") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 2, 13)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 3, 14))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 3, 14),
                    EndDate.of(2020, 4, 15)
                ),
                DateRange(
                    StartDate.of(2020, 2, 13),
                    EndDate.of(2020, 3, 14)
                ),
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 13)
                )
            )
        }

        it("can infer next fiscal month if income is missing") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 2, 20)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 3, 12))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 15)
                ),
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("can infer multiple next fiscal months if incomes are missing") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 2, 20)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 3, 20))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 3, 15),
                    EndDate.of(2020, 4, 15)
                ),
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 15)
                ),
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("can infer previous fiscal month if income is missing") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 2, 20)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 3, 12)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 3, 15))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 3, 15),
                    EndDate.of(2020, 4, 15)
                ),
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 15)
                )
            )
        }

        it("can infer fiscal month in-between existing ones") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 14)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 3, 13))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 3, 13),
                    EndDate.of(2020, 4, 15)
                ),
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 13)
                ),
                DateRange(
                    StartDate.of(2020, 1, 14),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("can infer next fiscal month if transaction is on the 15th") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 1, 15)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 2, 15))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 15)
                ),
                DateRange(
                    StartDate.of(2020, 1, 15),
                    EndDate.of(2020, 2, 15)
                )
            )
        }

        it("a mega test!") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 2, 20)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 3, 12)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 3, 14)),
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 6, 13)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 7, 14)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 7, 15))
                )
            }

            fiscalMonthsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 7, 15),
                    EndDate.of(2020, 8, 15)
                ),
                DateRange(
                    StartDate.of(2020, 6, 13),
                    EndDate.of(2020, 7, 15)
                ),
                DateRange(
                    StartDate.of(2020, 5, 15),
                    EndDate.of(2020, 6, 13)
                ),
                DateRange(
                    StartDate.of(2020, 4, 15),
                    EndDate.of(2020, 5, 15)
                ),
                DateRange(
                    StartDate.of(2020, 3, 14),
                    EndDate.of(2020, 4, 15)
                ),
                DateRange(
                    StartDate.of(2020, 2, 15),
                    EndDate.of(2020, 3, 14)
                )
            )
        }
    }

    describe("date ranges per fiscal year") {
        it("single year") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 5, 1))
                )
            }

            fiscalYearsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 4, 15),
                    EndDate.of(2021, 4, 15)
                )
            )
        }

        it("multiple years") {
            val transactions = {
                listOf(
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 5, 1)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2021, 6, 10))
                )
            }

            fiscalYearsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2021, 4, 15),
                    EndDate.of(2022, 4, 15)
                ),
                DateRange(
                    StartDate.of(2020, 4, 15),
                    EndDate.of(2021, 4, 15)
                )
            )
        }

        it("april transaction on irregular date") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 4, 14)),
                    anEntity(transaction = aDebitTransaction().withADateOf(2020, 6, 10))
                )
            }

            fiscalYearsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2020, 4, 14),
                    EndDate.of(2021, 4, 15)
                )
            )
        }

        it("two wages on an irregular date") {
            val transactions = {
                listOf(
                    anEntity(transaction = aWagesIncome().withADateOf(2020, 4, 14)),
                    anEntity(transaction = aWagesIncome().withADateOf(2021, 4, 13))
                )
            }

            fiscalYearsOf(transactions)() shouldBe listOf(
                DateRange(
                    StartDate.of(2021, 4, 13),
                    EndDate.of(2022, 4, 15)
                ),
                DateRange(
                    StartDate.of(2020, 4, 14),
                    EndDate.of(2021, 4, 13)
                )
            )
        }
    }
})
