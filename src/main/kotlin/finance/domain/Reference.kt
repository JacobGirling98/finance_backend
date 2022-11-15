package finance.domain

data class FullDescription(
    val value: String
)

data class ShortDescription(
    val value: String
)

data class DescriptionMapping(
    val fullDescription: FullDescription,
    val shortDescription: ShortDescription
)