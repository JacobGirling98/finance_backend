package dao.memory

import dao.Database
import dao.Entity
import dao.entityOf
import exceptions.NotFoundException
import java.time.LocalDateTime
import java.util.*

open class InMemoryDatabase<Domain : Comparable<Domain>>(
    initialData: List<Entity<Domain>> = emptyList(),
    private val now: () -> LocalDateTime = { LocalDateTime.now() }
) : Database<Domain, UUID> {

    protected var data: MutableMap<UUID, Entity<Domain>> = initialData.associateBy { it.id }.toMutableMap()

    override fun save(domain: Domain): UUID {
        val entity = entityOf(domain, now)
        data[entity.id] = entity
        return entity.id
    }

    override fun save(domains: List<Domain>): List<UUID> = domains.map { save(it) }

    override fun findById(id: UUID): Entity<Domain>? = data[id]

    override fun selectAll(): List<Entity<Domain>> = data.values.sortedBy { it.domain }

    override fun update(entity: Entity<Domain>): NotFoundException? =
        data.replace(entity.id, entity.copy(lastModified = now())).asNullableNotFound(entity.id)

    override fun delete(id: UUID): NotFoundException? = data.remove(id).asNullableNotFound(id)

    override fun deleteAll() {
        data.clear()
    }

    private fun <T> T?.asNullableNotFound(id: UUID): NotFoundException? =
        if (this == null) NotFoundException(id) else null
}
