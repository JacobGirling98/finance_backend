package http.model

import java.util.*

data class CreatedId(
    val id: UUID
) {
    companion object {
        fun random() = CreatedId(UUID.randomUUID())
    }
}
