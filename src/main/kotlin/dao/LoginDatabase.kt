package dao

import java.time.LocalDate

interface LoginDatabase {

    fun lastLogin(): LocalDate?
}