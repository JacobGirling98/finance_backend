package finance.dao

interface Database<T> {
    fun save(data: T)
    fun save(data: List<T>) {
        data.forEach { save(it) }
    }
}