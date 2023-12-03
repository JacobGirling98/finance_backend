package dao

import domain.PageNumber
import domain.PageSize
import exceptions.DatabaseException

interface Database<Domain : Comparable<Domain>, Id> {
    fun save(domain: Domain): Id
    fun save(domains: List<Domain>): List<Id>
    fun save(vararg domains: Domain): List<Id> = save(domains.toList())
    fun findById(id: Id): Entity<Domain>?
    fun selectAll(): List<Entity<Domain>>
    fun selectAll(pageNumber: PageNumber, pageSize: PageSize): Page<Domain>
    fun update(entity: Entity<Domain>): DatabaseException?
    fun delete(id: Id): DatabaseException?
}
