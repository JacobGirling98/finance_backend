package dao

import domain.Transaction

interface Database {
    fun save(transaction: Transaction)
}