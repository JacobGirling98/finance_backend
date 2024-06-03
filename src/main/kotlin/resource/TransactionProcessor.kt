package resource

import dao.AuditableEntity
import dao.Database
import dao.Entity
import dao.Page
import domain.Date
import domain.PageNumber
import domain.PageSize
import domain.Transaction
import java.util.*

class TransactionProcessor(private val transactionDatabase: Database<Transaction, UUID>) {

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
}
