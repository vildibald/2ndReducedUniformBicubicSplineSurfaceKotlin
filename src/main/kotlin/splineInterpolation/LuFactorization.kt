package splineInterpolation

class LuFactorization {
    fun solveTridiagonalSystem(lowerDiagonalValue: Double,
                               mainDiagonalValue: Double,
                               upperDiagonalValue: Double,
                               rightSide: DoubleArray,
                               numEquations: Int,
                               buffer: DoubleArray,
                               lastMainDiagonalValue: Double = mainDiagonalValue) {
        val m0 = 1 / mainDiagonalValue
        buffer[0] = upperDiagonalValue * m0
        rightSide[0] = rightSide[0] * m0
        val lastIndex = numEquations - 1

        for (i in 1 until lastIndex) {
            val m = 1 / (mainDiagonalValue - lowerDiagonalValue * buffer[i - 1])
            buffer[i] = upperDiagonalValue * m
            rightSide[i] = (rightSide[i] - lowerDiagonalValue * rightSide[i - 1]) * m
        }

        val mL = 1 / (lastMainDiagonalValue - lowerDiagonalValue * buffer[lastIndex - 1])
        buffer[lastIndex] = upperDiagonalValue * mL
        rightSide[lastIndex] = (rightSide[lastIndex] - lowerDiagonalValue * rightSide
                [lastIndex - 1]) * mL

        var i = numEquations - 1
        while (i-- > 0) {
            rightSide[i] = rightSide[i] - buffer[i] * rightSide[i+ 1]
        }
    }
}