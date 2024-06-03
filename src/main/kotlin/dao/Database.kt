package dao

import exceptions.DatabaseException

interface Database<Domain : Comparable<Domain>, Id> {
    fun save(domain: Domain): Id
    fun save(domains: List<Domain>): List<Id>
    fun save(vararg domains: Domain): List<Id> = save(domains.toList())
    fun findById(id: Id): AuditableEntity<Domain>?
    fun selectAll(): List<AuditableEntity<Domain>>
    fun update(entity: Entity<Domain>): DatabaseException?
    fun delete(id: Id): DatabaseException?

    fun deleteAll()
}
