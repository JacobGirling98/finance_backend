package resource

import domain.DateRange
import domain.EndDate
import domain.StartDate
import fixtures.aDebitTransaction
import fixtures.aWagesIncome
import fixtures.withADateOf
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TransactionAnalyserTest : DescribeSpec({

    describe("date ranges per calendar month") {
        it("can extract single date range") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 1, 10),
                    aDebitTransaction().withADateOf(2020, 1, 20),
                )
            }

            monthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 1),
                    EndDate(2020, 2, 1)
                )
            )
        }

        it("can extract date range over two months") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 1, 10),
                    aDebitTransaction().withADateOf(2020, 2, 20),
                )
            }

            monthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 1),
                    EndDate(2020, 2, 1)
                ),
                DateRange(
                    StartDate(2020, 2, 1),
                    EndDate(2020, 3, 1)
                )
            )
        }

        it("can extract date range over two years") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 12, 10),
                    aDebitTransaction().withADateOf(2021, 1, 10),
                )
            }

            monthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 12, 1),
                    EndDate(2021, 1, 1)
                ),
                DateRange(
                    StartDate(2021, 1, 1),
                    EndDate(2021, 2, 1)
                )
            )
        }
    }

    describe("date ranges per calendar year") {
        it("can extract date range for single year") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 1, 10),
                    aDebitTransaction().withADateOf(2020, 2, 20),
                )
            }

            yearsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 1),
                    EndDate(2021, 1, 1)
                )
            )
        }

        it("can extract date range for multiple years") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 1, 10),
                    aDebitTransaction().withADateOf(2021, 2, 20),
                    aDebitTransaction().withADateOf(2022, 3, 20),
                )
            }

            yearsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 1),
                    EndDate(2021, 1, 1)
                ),
                DateRange(
                    StartDate(2021, 1, 1),
                    EndDate(2022, 1, 1)
                ),
                DateRange(
                    StartDate(2022, 1, 1),
                    EndDate(2023, 1, 1)
                )
            )
        }
    }

    describe("date ranges per fiscal month") {
        it("single month with wages paid on the regular day") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 15)
                )
            )
        }

        it("single month with wages paid on the regular day and other transactions") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                    aDebitTransaction().withADateOf(2020, 1, 20)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 15)
                )
            )
        }

        it("two months with wages paid on the regular day") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                    aWagesIncome().withADateOf(2020, 2, 15)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 15)
                ),
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 15)
                )
            )
        }

        it("two months with first wage paid on a different day") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 14),
                    aWagesIncome().withADateOf(2020, 2, 15)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 14),
                    EndDate(2020, 2, 15)
                ),
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 15)
                )
            )
        }

        it("two months with second wage paid on a different day") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                    aWagesIncome().withADateOf(2020, 2, 14)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 14)
                ),
                DateRange(
                    StartDate(2020, 2, 14),
                    EndDate(2020, 3, 15)
                )
            )
        }

        it("three months with middle wage paid on a different day") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                    aWagesIncome().withADateOf(2020, 2, 13),
                    aWagesIncome().withADateOf(2020, 3, 14),
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 13)
                ),
                DateRange(
                    StartDate(2020, 2, 13),
                    EndDate(2020, 3, 14)
                ),
                DateRange(
                    StartDate(2020, 3, 14),
                    EndDate(2020, 4, 15)
                )
            )
        }

        it("can infer next fiscal month if income is missing") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                    aDebitTransaction().withADateOf(2020, 2, 20),
                    aDebitTransaction().withADateOf(2020, 3, 12)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 15)
                ),
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 15)
                ),
            )
        }

        it("can infer multiple next fiscal months if incomes are missing") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                    aDebitTransaction().withADateOf(2020, 2, 20),
                    aDebitTransaction().withADateOf(2020, 3, 20)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 15)
                ),
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 15)
                ),
                DateRange(
                    StartDate(2020, 3, 15),
                    EndDate(2020, 4, 15)
                ),
            )
        }

        it("can infer previous fiscal month if income is missing") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 2, 20),
                    aDebitTransaction().withADateOf(2020, 3, 12),
                    aWagesIncome().withADateOf(2020, 3, 15)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 15)
                ),
                DateRange(
                    StartDate(2020, 3, 15),
                    EndDate(2020, 4, 15)
                ),
            )
        }

        it("can infer fiscal month in-between existing ones") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 14),
                    aWagesIncome().withADateOf(2020, 3, 13),
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 14),
                    EndDate(2020, 2, 15)
                ),
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 13)
                ),
                DateRange(
                    StartDate(2020, 3, 13),
                    EndDate(2020, 4, 15)
                )
            )
        }

        it("can infer next fiscal month if transaction is on the 15th") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 1, 15),
                    aDebitTransaction().withADateOf(2020, 2, 15)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 1, 15),
                    EndDate(2020, 2, 15)
                ),
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 15)
                ),
            )
        }

        it("a mega test!") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 2, 20),
                    aDebitTransaction().withADateOf(2020, 3, 12),
                    aWagesIncome().withADateOf(2020, 3, 14),
                    aWagesIncome().withADateOf(2020, 6, 13),
                    aDebitTransaction().withADateOf(2020, 7, 14),
                    aDebitTransaction().withADateOf(2020, 7, 15)
                )
            }

            fiscalMonthsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 2, 15),
                    EndDate(2020, 3, 14)
                ),
                DateRange(
                    StartDate(2020, 3, 14),
                    EndDate(2020, 4, 15)
                ),
                DateRange(
                    StartDate(2020, 4, 15),
                    EndDate(2020, 5, 15)
                ),
                DateRange(
                    StartDate(2020, 5, 15),
                    EndDate(2020, 6, 13)
                ),
                DateRange(
                    StartDate(2020, 6, 13),
                    EndDate(2020, 7, 15)
                ),
                DateRange(
                    StartDate(2020, 7, 15),
                    EndDate(2020, 8, 15)
                ),
            )
        }
    }

    describe("date ranges per fiscal year") {
        it("single year") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 5, 1)
                )
            }

            fiscalYearsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 4, 15),
                    EndDate(2021, 4, 15)
                )
            )
        }

        it("multiple years") {
            val transactions = {
                listOf(
                    aDebitTransaction().withADateOf(2020, 5, 1),
                    aDebitTransaction().withADateOf(2021, 6, 10)
                )
            }

            fiscalYearsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 4, 15),
                    EndDate(2021, 4, 15)
                ),
                DateRange(
                    StartDate(2021, 4, 15),
                    EndDate(2022, 4, 15)
                )
            )
        }

        it("april transaction on irregular date") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 4, 14),
                    aDebitTransaction().withADateOf(2020, 6, 10)
                )
            }

            fiscalYearsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 4, 14),
                    EndDate(2021, 4, 15)
                )
            )
        }

        it("two wages on an irregular date") {
            val transactions = {
                listOf(
                    aWagesIncome().withADateOf(2020, 4, 14),
                    aWagesIncome().withADateOf(2021, 4, 13)
                )
            }

            fiscalYearsOf(transactions) shouldBe listOf(
                DateRange(
                    StartDate(2020, 4, 14),
                    EndDate(2021, 4, 13)
                ),
                DateRange(
                    StartDate(2021, 4, 13),
                    EndDate(2022, 4, 15)
                )
            )
        }
    }
})