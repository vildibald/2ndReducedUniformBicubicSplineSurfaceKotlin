class FirstReducedSolver : Solver {
    override fun solve(derivationCount: Int,
                       tridiagonals: Tridiagonals,
                       parameterGetter: (Int, Int) -> Double,
                       derivationGetter: (Int, Int) -> Double,
                       derivationSetter: (Int, Int, Double) -> Unit,
                       systemIdx: Int) {
        val unknownsCount = derivationCount
        val even = unknownsCount % 2 == 0
        val equationsCount = if (even) unknownsCount / 2 - 2 else unknownsCount / 2 - 1

        val tridiagonal = tridiagonals.get()
        val rightSide = tridiagonal.rightSideBuffer
        val h = tridiagonal.h
        val threeDivH = 3.0 / h
        val twelveDivH = 4.0 * threeDivH
        for (i in 0 until equationsCount - 1) {
            val i21 = 2 * (i + 1)
            rightSide[i] = threeDivH * (parameterGetter(i21 + 2, systemIdx) - parameterGetter(i21 - 2, systemIdx))
            -twelveDivH * (parameterGetter(i21 + 1, systemIdx) - parameterGetter(i21 - 1, systemIdx))
        }
        rightSide[0] -= derivationGetter(0, systemIdx)

        var idx = equationsCount - 1
        val i21 = 2 * (idx + 1)
        if (even) {
            // TODO: Needs support for even count.
            rightSide[idx] = 0.0
        } else {
            rightSide[idx] = threeDivH * (parameterGetter(i21 + 2, systemIdx) - parameterGetter(i21 - 2, systemIdx))
            -twelveDivH * (parameterGetter(i21 + 1, systemIdx) - parameterGetter(i21 - 1,
                    systemIdx))
        }

        tridiagonal.solve()
        val quarter = 1.0 / 4
        for (i in 0 until equationsCount) {
            val evenI = 2 * (i + 1)
            derivationSetter(evenI, systemIdx, rightSide[i])

            val oddI = 2 * i + 1
            derivationSetter(oddI, systemIdx,
                    quarter * (threeDivH * (parameterGetter(oddI + 1, systemIdx) - parameterGetter(oddI - 1, systemIdx))
                            - derivationGetter(oddI + 1, systemIdx) - derivationGetter(oddI - 1, systemIdx)
                            )
            )
        }
        idx = equationsCount
        val oddI = 2 * idx + 1
        derivationSetter(oddI, systemIdx,
                quarter * (threeDivH * (parameterGetter(oddI + 1, systemIdx) - parameterGetter(oddI - 1, systemIdx))
                        - derivationGetter(oddI + 1, systemIdx) - derivationGetter(oddI - 1, systemIdx)
                        )
        )


    }

    override fun tridiagonal(knots: DoubleArray) = Tridiagonal.firstReducedTridiagonal(knots)
}