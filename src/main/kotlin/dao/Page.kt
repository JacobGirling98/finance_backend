package dao

import domain.HasNextPage
import domain.HasPreviousPage
import domain.PageNumber
import domain.PageSize
import domain.TotalElements
import domain.TotalPages

data class Page<T>(
    val data: List<T>,
    val pageNumber: PageNumber,
    val pageSize: PageSize,
    val totalElements: TotalElements,
    val totalPages: TotalPages,
    val hasPreviousPage: HasPreviousPage,
    val hasNextPage: HasNextPage
)
