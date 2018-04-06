import kotlin.math.sin
import kotlin.math.sqrt

private fun testVector(from: Double, to: Double, size: Int): DoubleArray {
    val offset = (to - from) / (size - 1)
    return DoubleArray(size, {
        from + it * offset
    })
}

fun surfaceBenchmark(numIterations: Int, numKnots: Int, inParallel: Boolean = false):
        ComparisonBenchmarkResult {
    val function = InterpolativeMathFunction { x, y -> sin(sqrt(x * x + y * y)) }
    val full = SplineAlgorithm(function, FullSolver())
    val firstReduced = SplineAlgorithm(function, FirstReducedSolver())
    val secondReduced = SplineAlgorithm(function, SecondReducedSolver())

    val calculatedResults = ArrayList<Double>(numIterations)
    val fullTimes = ArrayList<Long>(numIterations)
    val firstReducedTimes = ArrayList<Long>(numIterations)
    val secondReducedTimes = ArrayList<Long>(numIterations * 4)

    val vector = testVector(-2.0, 2.0, numKnots)

    for (i in 0 until numIterations + 5) {
        val (spline, time) = full.calculate(vector, vector, inParallel)
        calculatedResults.add(spline.dxy(1, 1))
        fullTimes.add(time)
    }

    for (i in 0 until numIterations + 5) {
        val (spline, time) = firstReduced.calculate(vector, vector, inParallel)
        calculatedResults.add(spline.dxy(1, 1))
        firstReducedTimes.add(time)
    }

    for (i in 0 until numIterations + 5) {
        val (spline, time) = secondReduced.calculate(vector, vector, inParallel)
        calculatedResults.add(spline.dxy(1, 1))
        secondReducedTimes.add(time)
    }

    val fullTime = fullTimes.takeLast(numIterations).sum() / numIterations
    val firstReducedTime = firstReducedTimes.takeLast(numIterations).sum() / numIterations
    val secondReducedTime = secondReducedTimes.takeLast(numIterations).sum() / numIterations

    println("Ignore ${calculatedResults[1]}")
    return ComparisonBenchmarkResult().add(fullTime).add(firstReducedTime).add(secondReducedTime)

}

fun printSurfaceResult(result: ComparisonBenchmarkResult) {
    println("Full: ${result[0]} \n" +
            "1. Reduced: ${result[1]} \n" +
            "2. Reduced: ${result[2]} \n" +
            "Difference F/FR: ${result.ratio(0, 1)} \n" +
            "Difference F/SR: ${result.ratio(0, 2)} \n")
}

fun main(args: Array<String>) {
    while (true) {
        println("3: Spline surface benchmark.")
        println("4: Spline surface options.")
        println("Q: End program.")
        val input = readLine() ?: " "

        when (input) {
            "3" -> performSurfaceBenchmark()
            "4" -> performSurfaceBenchmarkWithOptions()
            "Q" -> return
        }
    }
}

fun performSurfaceBenchmark() {
    try {
        println("Spline surface benchmark")
        println("Enter number of iterations: ")
        val numIterations = (readLine() ?: " ").toInt()
        println("Enter number of knots: ")
        val numKnots = (readLine() ?: " ").toInt()
        printSurfaceResult(surfaceBenchmark(numIterations, numKnots))
    } catch (nfe: NumberFormatException) {
    }
}

fun performSurfaceBenchmarkWithOptions() {
    try {
        println("Spline surface benchmark (options)")
        println("Enter number of iterations: ")
        val numIterations = (readLine() ?: " ").toInt()
        println("Enter number of knots: ")
        val numKnots = (readLine() ?: " ").toInt()
        println("Enable parallelism? [Y/N]: N")
        val inParallel = readLine() ?: " " in "Yy"
        printSurfaceResult(surfaceBenchmark(numIterations, numKnots, inParallel))
    } catch (nfe: NumberFormatException) {
    }
}
