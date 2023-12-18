package resource

import dao.Page
import domain.*
import kotlin.math.ceil

fun <T> paginate(allElements: List<T>, pageNumber: PageNumber, pageSize: PageSize): Page<T> {
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

private fun <T> List<T>.safeSublist(start: Int, end: Int) = try {
    subList(start, end)
} catch (e: IndexOutOfBoundsException) {
    subList(0, this.size)
}