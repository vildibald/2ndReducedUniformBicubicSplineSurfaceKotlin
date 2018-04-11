package splineInterpolation.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadPool : ExecutorService by pool {
    val logicalCpuCount = Runtime.getRuntime().availableProcessors()

    fun cpuId() = Thread.currentThread().id.toInt() % logicalCpuCount + 1
}

private val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
