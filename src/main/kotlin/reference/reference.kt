package reference

import java.io.File

const val dataLoc = "/Users/jacobgirling/Documents/Programming/FinanceV3/data/prod"

fun read(file: String): List<String> = File("$dataLoc/$file").readLines()

fun readCategories(): List<String> = read("categories.txt")