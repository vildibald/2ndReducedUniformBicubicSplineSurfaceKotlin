class MultiThreadPreparator {
    fun prepareList(tridiagonals: MutableList<Tridiagonal>) {
        val cpuCount = ThreadPool.logicalCpuCount
        for(i in 0 until cpuCount){
            tridiagonals.add(tridiagonals[0].copy())
        }
    }

}
