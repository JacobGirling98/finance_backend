package dao

import java.util.*

interface UUIDDatabase<T : Comparable<T>> : Database<T, UUID>