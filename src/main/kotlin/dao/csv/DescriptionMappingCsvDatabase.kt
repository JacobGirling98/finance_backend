package dao.csv

import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import kotlin.time.Duration

class DescriptionMappingCsvDatabase(
    syncPeriod: Duration,
    fileLoc: String
) : CsvDatabase<DescriptionMapping>(syncPeriod, fileLoc) {
    override fun headers(): String = "full_description,short_description"

    override fun domainFromCommaSeparatedList(row: List<String>): DescriptionMapping = DescriptionMapping(
        FullDescription(row[indexOfColumn("full_description")]),
        ShortDescription(row[indexOfColumn("short_description")])
    )

    override fun DescriptionMapping.toRow(): String = "${fullDescription.value},${shortDescription.value}"

}