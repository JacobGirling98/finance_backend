package domain

data class FullDescription(
    val value: String
)

data class ShortDescription(
    val value: String
)

data class DescriptionMapping(
    val fullDescription: FullDescription,
    val shortDescription: ShortDescription
) : Comparable<DescriptionMapping> {
    override fun compareTo(other: DescriptionMapping): Int =
        fullDescription.value.compareTo(other.fullDescription.value)
}
