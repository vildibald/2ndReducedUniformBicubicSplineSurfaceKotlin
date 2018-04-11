package splineInterpolation

import splineInterpolation.utils.ThreadPool
import java.util.concurrent.Future

inline fun loop(from: Int, to: Int, step: Int, inParallel: Boolean, crossinline body: (i: Int) -> Unit) {
    if (inParallel) {
        val futures = mutableListOf<Future<*>>()
        val cpuCount = ThreadPool.logicalCpuCount
        for (i in 0 until cpuCount) {
            val start = i * from / cpuCount
            val stop = (i + 1) * from / cpuCount
            futures.add(
                    ThreadPool.submit {
                        for (i in start until stop step step) {
                            body(i)
                        }
                    }
            )
            for (future in futures){
                future.get()
            }
        }
    } else {
        for (i in from until to step step) {
            body(i)
        }
    }
}

