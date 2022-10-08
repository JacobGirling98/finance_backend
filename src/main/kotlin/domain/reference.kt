package domain

data class FullDescription(
    val value: String
)

data class ShortDescription(
    val value: String
)

data class Description(
    val fullDescription: FullDescription,
    val shortDescription: ShortDescription
)