class FullSolver : Solver {
    override fun solve(derivationCount: Int,
                       tridiagonals: Tridiagonals,
                       parameterGetter: (Int, Int) -> Double,
                       derivationGetter: (Int, Int) -> Double,
                       derivationSetter: (Int, Int, Double) -> Unit,
                       systemIdx: Int) {
        val tridiagonal = tridiagonals.get()
        val rightSide = tridiagonal.rightSideBuffer
        val threeDivH = 3.0 / tridiagonal.h
        val equationCount = derivationCount - 2
        for (i in 0 until equationCount) {
            rightSide[i] = threeDivH * (parameterGetter(i + 2, systemIdx) - parameterGetter(i, systemIdx))
        }
        rightSide[0] -= derivationGetter(0, systemIdx)
        rightSide[equationCount - 1] -= derivationGetter(derivationCount - 1, systemIdx)

        tridiagonal.solve()

        for (i in 0 until equationCount) {
            derivationSetter(i + 1, systemIdx, rightSide[i])
        }
    }

    override fun tridiagonal(knots: DoubleArray) = Tridiagonal.fullTridiagonal(knots)
}