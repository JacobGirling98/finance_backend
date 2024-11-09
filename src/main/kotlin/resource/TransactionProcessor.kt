package resource

import dao.AuditableEntity
import dao.Entity
import dao.Page
import dao.UUIDDatabase
import domain.Category
import domain.Date
import domain.DateRange
import domain.PageNumber
import domain.PageSize
import domain.Transaction

class TransactionProcessor(private val transactionDatabase: UUIDDatabase<Transaction>) {

    fun selectAll(pageNumber: PageNumber, pageSize: PageSize) =
        paginate(transactionDatabase.selectAll(), pageNumber, pageSize)

    fun selectBy(
        pageNumber: PageNumber,
        pageSize: PageSize,
        filter: ((AuditableEntity<Transaction>) -> Boolean)
    ): Page<AuditableEntity<Transaction>> = paginate(
        transactionDatabase.selectAll().filter(filter),
        pageNumber,
        pageSize
    )

    fun search(term: String, pageNumber: PageNumber, pageSize: PageSize) =
        paginate(transactionDatabase.selectAll().search(term), pageNumber, pageSize)

    fun search(term: String, pageNumber: PageNumber, pageSize: PageSize, filter: ((Entity<Transaction>) -> Boolean)) =
        paginate(transactionDatabase.selectAll().search(term).filter(filter), pageNumber, pageSize)

    fun mostRecentUserTransaction(): Date? = transactionDatabase
        .selectAll()
        .filter { it.domain.addedBy.value == "Jacob" }
        .map { it.domain }
        .mostRecent()

    fun transactionsBy(category: Category, dateRange: DateRange): List<AuditableEntity<Transaction>> =
        transactionDatabase.selectAll().filterEntities(dateRange).filter { it.domain.category == category }
}
