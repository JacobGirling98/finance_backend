package exceptions

import java.util.*

open class DatabaseException: RuntimeException()

data class NotFoundException(val id: UUID) : DatabaseException()
