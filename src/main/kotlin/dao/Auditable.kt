package dao

import java.time.LocalDateTime

interface Auditable {
    val lastModified: LocalDateTime
}