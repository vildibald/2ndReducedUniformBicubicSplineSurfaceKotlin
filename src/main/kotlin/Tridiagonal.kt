class Tridiagonal : Measurable {

    override val executionTime: Long
        get() = timer.executionTime

    val buffer: DoubleArray
    val rightSideBuffer: DoubleArray
    val lhs0Coeficient: Double
    val lhs1Coeficient: Double
    val lhs2Coeficient: Double
    val h: Double
    val equationCount: Int
    val problemSize: Int
    private val timer: Timer = Timer()

    private constructor(lhs0Coeficient: Double,
                        lhs1Coeficient: Double,
                        lhs2Coeficient: Double,
                        h: Double,
                        equationCount: Int,
                        problemSize: Int) {
        this.buffer = DoubleArray(equationCount, init = { 1.0 })
        this.rightSideBuffer = DoubleArray(equationCount, init = { 1.0 })
        this.lhs0Coeficient = lhs0Coeficient
        this.lhs1Coeficient = lhs1Coeficient
        this.lhs2Coeficient = lhs2Coeficient
        this.h = h
        this.equationCount = equationCount
        this.problemSize = problemSize
    }

    fun solve(): DoubleArray {
        val lu = LuFactorization()
        lu.solveTridiagonalSystem(lhs2Coeficient,
                lhs1Coeficient,
                lhs0Coeficient,
                rightSideBuffer,
                equationCount,
                buffer)
        return rightSideBuffer
    }

    fun resetBufferAndGet(): DoubleArray {
        buffer.fill(0.0)
        return this.buffer
    }

    fun copy() = Tridiagonal(
            lhs0Coeficient,
            lhs1Coeficient,
            lhs2Coeficient,
            h,
            equationCount,
            problemSize
    )

    companion object {
        fun fullTridiagonal(knots: DoubleArray): Tridiagonal {
            val numUnknowns = knots.size - 2
            val h = knots[1] - knots[0]
            return Tridiagonal(
                    1.0,
                    4.0,
                    1.0,
                    h,
                    numUnknowns,
                    knots.size
            )
        }

        fun firstReducedTridiagonal(knots: DoubleArray): Tridiagonal {
            val numKnots = knots.size
            val even = numKnots % 2 == 0
            val numUnknowns = if (even) numKnots / 2 - 2 else numKnots / 2 - 1
            val h = knots[1] - knots[0]
            return Tridiagonal(
                    1.0,
                    -14.0,
                    1.0,
                    h,
                    numUnknowns,
                    knots.size
            )
        }

        fun secondReducedTridiagonal(knots: DoubleArray): Tridiagonal {
            val numKnots = knots.size
            val remainder = numKnots % 4
            var numUnknowns = numKnots / 4
            when (remainder) {
                1, 0 -> --numUnknowns
            }

            val h = knots[1] - knots[0]
            return Tridiagonal(
                    1.0,
                    -194.0,
                    1.0,
                    h,
                    numUnknowns,
                    knots.size
            )
        }
    }

}