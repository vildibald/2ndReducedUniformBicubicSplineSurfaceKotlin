class Spline(val x: DoubleArray, val y: DoubleArray) {
    private val z: DoubleMatrix = DoubleMatrix(x.size, init = { DoubleArray(y.size) })
    private val dx: DoubleMatrix = DoubleMatrix(y.size, init = { DoubleArray(x.size) })
    private val dy: DoubleMatrix = DoubleMatrix(x.size, init = { DoubleArray(y.size) })
    private val dxy: DoubleMatrix = DoubleMatrix(x.size, init = { DoubleArray(y.size) })

    val rowCount: Int
        get() = x.size

    val columnCount: Int
        get() = y.size


    fun x(i: Int): Double = this.x[i]

    fun y(j: Int): Double = this.y[j]

    fun z(i: Int, j: Int): Double = this.z[i][j]

    fun dx(i: Int, j: Int): Double = this.dx[j][i]

    fun dy(i: Int, j: Int): Double = this.dy[i][j]

    fun dxy(i: Int, j: Int): Double = this.dxy[i][j]

    fun setZ(i: Int, j: Int, value: Double) {
        this.z[i][j] = value
    }

    fun setDx(i: Int, j: Int, value: Double) {
        this.dx[j][i] = value
    }

    fun setDy(i: Int, j: Int, value: Double) {
        this.dy[i][j] = value
    }

    fun setDxy(i: Int, j: Int, value: Double) {
        this.dxy[i][j] = value
    }

    fun print() {
        println("---------- Knot matrix ----------")
        for (i in 0 until rowCount) {
            println("Row $i:")
            for (j in 0 until columnCount) {
                println("$j: \n z: ${z(i, j)} \n dx: ${dx(i, j)} \n dy: ${dy(i, j)} \n dxy: ${dxy(i, j)}")
            }
        }
        println("-------------------------------")
    }

    fun initialize(mathFunction: InterpolativeMathFunction) {
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                val z = mathFunction.z(x(i), y(j))
                setZ(i, j, z)
            }
        }

        for (j in 0 until columnCount) {
            setDx(0, j, mathFunction.dx(x(0), y(j)))
            setDx(rowCount - 1, j, mathFunction.dx(x(rowCount - 1), y(j)))
        }

        for (i in 0 until rowCount) {
            setDy(i, 0, mathFunction.dy(x(i), y(0)))
            setDy(i, columnCount - 1, mathFunction.dy(x(i), y(columnCount - 1)))
        }

        setDxy(0, 0, mathFunction.dxy(x(0), y(0)))
        setDxy(rowCount - 1, 0, mathFunction.dxy(x(rowCount - 1), y(0)))
        setDxy(0, columnCount - 1, mathFunction.dxy(x(0), y(columnCount - 1)))
        setDxy(rowCount - 1, columnCount - 1, mathFunction.dxy(x(rowCount - 1), y(columnCount - 1)))
    }
}

