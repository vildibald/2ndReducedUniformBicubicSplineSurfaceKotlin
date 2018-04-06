class SplineAlgorithm(private val function: InterpolativeMathFunction,
                      private val solver: Solver) {

    fun calculate(xVector: DoubleArray, yVector: DoubleArray, inParallel: Boolean = false):
            Pair<Spline, Long> {
        val spline = Spline(xVector, yVector)
        val (xTridiagonals, yTridiagonals) = initialize(spline, inParallel)
        val timer = Timer.start()
        fillDx(spline, xTridiagonals)
        fillDy(spline, yTridiagonals)
        fillDxy(spline, xTridiagonals)
        fillDyx(spline, yTridiagonals)
        val executionTime = timer.executionTime
        return spline to executionTime
    }

    private fun fillDx(spline: Spline, xTridiagonals: Tridiagonals) {
        val parameterGetter = { i: Int, j: Int -> spline.z(i, j) }
        val derivationGetter = { i: Int, j: Int -> spline.dx(i, j) }
        val derivationSetter = { i: Int, j: Int, value: Double -> spline.setDx(i, j, value) }
        fillD(spline.columnCount, spline.rowCount, xTridiagonals, parameterGetter,
                derivationGetter, derivationSetter)
    }

    private fun fillDy(spline: Spline, yTridiagonals: Tridiagonals) {
        val parameterGetter = { i: Int, j: Int -> spline.z(j, i) }
        val derivationGetter = { i: Int, j: Int -> spline.dy(j, i) }
        val derivationSetter = { i: Int, j: Int, value: Double -> spline.setDy(j, i, value) }
        fillD(spline.rowCount, spline.columnCount, yTridiagonals, parameterGetter,
                derivationGetter, derivationSetter)
    }

    private fun fillDxy(spline: Spline, xTridiagonals: Tridiagonals) {
        val parameterGetter = { i: Int, j: Int -> spline.dy(i, j) }
        val derivationGetter = { i: Int, j: Int -> spline.dxy(i, j) }
        val derivationSetter = { i: Int, j: Int, value: Double -> spline.setDxy(i, j, value) }
        fillD(spline.columnCount, spline.rowCount, xTridiagonals, parameterGetter,
                derivationGetter, derivationSetter)
    }

    private fun fillDyx(spline: Spline, yTridiagonals: Tridiagonals) {
        val parameterGetter = { i: Int, j: Int -> spline.dx(j, i) }
        val derivationGetter = { i: Int, j: Int -> spline.dxy(j, i) }
        val derivationSetter = { i: Int, j: Int, value: Double -> spline.setDxy(j, i, value) }
        fillD(spline.rowCount, spline.columnCount, yTridiagonals, parameterGetter,
                derivationGetter, derivationSetter)
    }

    private fun initialize(spline: Spline, inParallel: Boolean): Pair<Tridiagonals, Tridiagonals> {
        spline.initialize(function)
        return initializeTridiagonals(spline.x, spline.y, inParallel)
    }

    private fun initializeTridiagonals(xVector: DoubleArray, yVector: DoubleArray, inParallel: Boolean): Pair<Tridiagonals, Tridiagonals> {
        val xTridiagonals = Tridiagonals(solver.tridiagonal(xVector)).parallel(inParallel)
        val yTridiagonals = Tridiagonals(solver.tridiagonal(yVector)).parallel(inParallel)
        return xTridiagonals to yTridiagonals
    }

    private fun fillD(systemCount: Int, derivationCount: Int, tridiagonals: Tridiagonals,
                      parameterGetter: (Int, Int) -> Double, derivationGetter: (Int, Int) -> Double,
                      derivationSetter: (Int, Int, Double) -> Unit) {
        loop(0, systemCount, 1, tridiagonals.isParallel, {
            solver.solve(derivationCount, tridiagonals, parameterGetter, derivationGetter,
                    derivationSetter, it)
        })
    }

}