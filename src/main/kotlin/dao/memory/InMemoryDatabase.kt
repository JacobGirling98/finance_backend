package dao.memory

import dao.Database
import dao.Entity
import dao.Page
import dao.entityOf
import domain.HasNextPage
import domain.HasPreviousPage
import domain.PageNumber
import domain.PageSize
import domain.TotalElements
import domain.TotalPages
import exceptions.NotFoundException
import java.util.*
import kotlin.math.ceil

open class InMemoryDatabase<Domain : Comparable<Domain>>(
    initialData: List<Entity<Domain>> = emptyList()
) : Database<Domain, UUID> {

    protected var data: MutableMap<UUID, Domain> = initialData.associate { it.id to it.domain }.toMutableMap()

    override fun save(domain: Domain): UUID {
        val entity = entityOf(domain)
        data[entity.id] = entity.domain
        return entity.id
    }

    override fun save(domains: List<Domain>): List<UUID> = domains.map { save(it) }

    override fun findById(id: UUID): Entity<Domain>? = data[id]?.let { Entity(id, it) }

    override fun selectAll(): List<Entity<Domain>> = data.map { Entity(it.key, it.value) }.sortedBy { it.domain }

    override fun selectAll(pageNumber: PageNumber, pageSize: PageSize): Page<Domain> {
        val allElements = selectAll()
        val data = allElements.drop((pageNumber.value - 1) * pageSize.value).safeSublist(0, pageSize.value)
        val totalPages = ceil(allElements.size.toDouble() / pageSize.value).toInt()
        return Page(
            data,
            pageNumber,
            PageSize(data.size),
            TotalElements(allElements.size),
            TotalPages(totalPages),
            HasPreviousPage(pageNumber.value > 1),
            HasNextPage(totalPages != pageNumber.value)
        )
    }

    override fun update(entity: Entity<Domain>): NotFoundException? =
        data.replace(entity.id, entity.domain).asNullableNotFound(entity.id)

    override fun delete(id: UUID): NotFoundException? = data.remove(id).asNullableNotFound(id)

    private fun <T> T?.asNullableNotFound(id: UUID): NotFoundException? =
        if (this == null) NotFoundException(id) else null

    private fun <T> List<T>.safeSublist(start: Int, end: Int) = try {
        subList(start, end)
    } catch (e: IndexOutOfBoundsException) {
        subList(0, this.size)
    }
}
