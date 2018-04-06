class ComparisonBenchmarkResult() : Iterable<Long>  {

    private val algorithmTimes = mutableListOf<Long>()

    override fun iterator() = algorithmTimes.iterator()

    fun add(time: Long): ComparisonBenchmarkResult {
        algorithmTimes.add(time)
        return this
    }

    operator fun get(i: Int) = algorithmTimes[i]

    fun ratio(i: Int, j: Int) = algorithmTimes[i].toDouble() / algorithmTimes[j].toDouble()
}