package dao

import java.io.File

fun File.writeLine(value: String) {
    appendText("$value\n")
}