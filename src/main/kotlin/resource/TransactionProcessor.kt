package resource

import dao.Database
import domain.PageNumber
import domain.PageSize
import domain.Transaction
import java.util.*

class TransactionProcessor(private val transactionDatabase: Database<Transaction, UUID>) {

    fun selectAll(pageNumber: PageNumber, pageSize: PageSize) =
        paginate(transactionDatabase.selectAll(), pageNumber, pageSize)

    fun search(term: String, pageNumber: PageNumber, pageSize: PageSize) =
        paginate(transactionDatabase.selectAll().search(term), pageNumber, pageSize)

    fun mostRecentUserTransaction() = transactionDatabase
        .selectAll()
        .filter { it.domain.addedBy.value == "Jacob" }
        .map { it.domain }
        .mostRecent()
}