typealias MathFunction = (Double, Double) -> Double

class InterpolativeMathFunction(val z: MathFunction) {
    val dx: MathFunction
    val dy: MathFunction
    val dxy: MathFunction

    init {
        val h = 0.001
        dx = { x, y ->
            (z(x + h, y) - z(x, y)) / h
        }
        dy = { x, y ->
            (z(x, y+h) - z(x, y)) / h
        }
        dxy = { x, y ->
            (dx(x, y+h) - dx(x, y)) / h
        }
    }

}