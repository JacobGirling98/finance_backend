package dao

interface Database<T> {
    fun save(data: T)
    fun save(data: List<T>): Int {
        data.forEach { save(it) }
        return data.size
    }
}