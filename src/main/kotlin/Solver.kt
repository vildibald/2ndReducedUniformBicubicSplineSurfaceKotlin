interface Solver {
    fun solve(derivationCount: Int,
              tridiagonals: Tridiagonals,
              parameterGetter: (Int, Int) -> Double,
              derivationGetter: (Int, Int) -> Double,
              derivationSetter: (Int, Int, Double) -> Unit,
              systemIdx: Int
    )

    fun tridiagonal(knots: DoubleArray): Tridiagonal

}