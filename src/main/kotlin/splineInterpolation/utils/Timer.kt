package splineInterpolation.utils

class Timer(val start: Long = System.nanoTime()) : Measurable {
    override val executionTime: Long
        get() = (System.nanoTime() - start) / 1000

    companion object {
        fun start(): Timer {
            return Timer()
        }
    }

}