internal class SecondReducedSolver : Solver {
    override fun solve(derivationCount: Int, tridiagonals: Tridiagonals, p: (Int, Int) -> Double,
                       dget: (Int, Int) -> Double, dset: (Int, Int, Double) -> Unit, systemIdx:
                       Int) {
        val tridiagonal = tridiagonals.get()
        val rightSide = tridiagonal.rightSideBuffer
        val unknownsCount = derivationCount
        val equationsCount = tridiagonal.equationCount
        val h = tridiagonal.h
        val threeDivH = 3.0 / h

        for (i in 0 until equationsCount - 1) {
            val i41 = 4 * (i + 1)
            rightSide[i] = threeDivH * (p(i41 + 4, systemIdx) - p(i41 - 4, systemIdx)
                    - 4 * (p(i41 + 3, systemIdx) - p(i41 - 3, systemIdx))
                    + 14 * (p(i41 + 2, systemIdx) - p(i41 - 2, systemIdx))
                    - 52 * (p(i41 + 1, systemIdx) - p(i41 - 1, systemIdx))
                    )

        }
        val d0 = dget(0, systemIdx)
        rightSide[0] -= d0
        val dN = dget(derivationCount - 1, systemIdx)

        var idx = equationsCount - 1
        val i41 = 4 * (idx + 1)
        val remainder = unknownsCount % 4
        when (remainder) {
        // TODO: Implement remainder cases properly
            3, 2, 1 -> rightSide[idx] = threeDivH * (p(i41 + 4, systemIdx) - p(i41 - 4, systemIdx)
                    - 4 * (p(i41 + 3, systemIdx) - p(i41 - 3, systemIdx))
                    + 14 * (p(i41 + 2, systemIdx) - p(i41 - 2, systemIdx))
                    - 52 * (p(i41 + 1, systemIdx) - p(i41 - 1, systemIdx))
                    )
        }
        rightSide[idx] -= dN

        tridiagonal.solve()
        val minusOneDiv14 = -1.0 / 14
        val twelveDivH = 4.0 * threeDivH
        val oneDiv4 = 1.0 / 4
        for (i in 0 until equationsCount - 1) {
            idx = 4 * (i + 1);
            dset(idx, systemIdx, rightSide[i])

            idx -= 2
            dset(idx, systemIdx,
                    minusOneDiv14 * (
                            threeDivH * (p(idx + 2, systemIdx) - p(idx - 2, systemIdx))
                                    - twelveDivH * (p(idx + 1, systemIdx) - p(idx - 1, systemIdx))
                                    - dget(idx - 2, systemIdx) - dget(idx + 2, systemIdx)
                            )
            )

            --idx
            dset(idx, systemIdx,
                    oneDiv4 * (
                            threeDivH * (p(idx + 1, systemIdx) - p(idx - 1, systemIdx))
                                    - dget(idx - 1, systemIdx) - dget(idx + 1, systemIdx)
                            )
            )

            idx += 2
            dset(idx, systemIdx,
                    oneDiv4 * (
                            threeDivH * (p(idx + 1, systemIdx) - p(idx - 1, systemIdx))
                                    - dget(idx - 1, systemIdx) - dget(idx + 1, systemIdx)
                            )
            )
        }
        idx = 4 * (equationsCount + 1)

        idx -= 2
        dset(idx, systemIdx,
                minusOneDiv14 * (
                        threeDivH * (p(idx + 2, systemIdx) - p(idx - 2, systemIdx))
                                - twelveDivH * (p(idx + 1, systemIdx) - p(idx - 1, systemIdx))
                                - dget(idx - 2, systemIdx) - dget(idx + 2, systemIdx)
                        )
        )

        --idx;
        dset(idx, systemIdx,
                oneDiv4 * (
                        threeDivH * (p(idx + 1, systemIdx) - p(idx - 1, systemIdx))
                                - dget(idx - 1, systemIdx) - dget(idx + 1, systemIdx)
                        )
        )

        idx += 2;
        dset(idx, systemIdx,
                oneDiv4 * (
                        threeDivH * (p(idx + 1, systemIdx) - p(idx - 1, systemIdx))
                                - dget(idx - 1, systemIdx) - dget(idx + 1, systemIdx)
                        )
        )


    }

    override fun tridiagonal(knots: DoubleArray) = Tridiagonal.secondReducedTridiagonal(knots)
}