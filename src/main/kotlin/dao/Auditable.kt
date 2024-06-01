package dao

import java.time.LocalDateTime

abstract class Auditable {
    abstract val lastModified: LocalDateTime
}