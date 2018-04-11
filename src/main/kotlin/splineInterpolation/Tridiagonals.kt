package splineInterpolation

import splineInterpolation.utils.MultiThreadPreparator
import splineInterpolation.utils.ThreadPool

class Tridiagonals(tridiagonal: Tridiagonal) {
    private val tridiagonals = mutableListOf<Tridiagonal>()

    val isParallel: Boolean
        get() = tridiagonals.size == 1

    fun get(): Tridiagonal {
        if (isParallel) {
            return tridiagonals[0]
        }
        return tridiagonals[ThreadPool.cpuId()]
    }


    fun parallel(parallelize: Boolean = true): Tridiagonals {
        if (!parallelize) return this
        val multiThreadPreparator = MultiThreadPreparator()
        multiThreadPreparator.prepareList(tridiagonals)
        return this
    }

    init {
        tridiagonals.add(tridiagonal)
    }

}