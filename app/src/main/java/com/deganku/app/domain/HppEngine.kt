package com.deganku.app.domain

data class HppResult(val waterCost: Long, val meatCost: Long, val total: Long)

object HppEngine {
    fun coconut(price: Long, waterYield: Int, meatYield: Int, water: Int, meat: Int): HppResult {
        require(waterYield > 0 && meatYield > 0)
        val waterRate = price * 60.0 / 100.0 / waterYield
        val meatRate = price * 40.0 / 100.0 / meatYield
        val wc = (waterRate * water).toLong()
        val mc = (meatRate * meat).toLong()
        return HppResult(wc, mc, wc + mc)
    }
}

data class CoconutUsage(val coconutId: String, val water: Int, val meat: Int)

class FifoEngine(private val db: com.deganku.app.data.AppDatabase) {
    suspend fun allocate(waterNeed: Int, meatNeed: Int): List<CoconutUsage> {
        var w = waterNeed
        var m = meatNeed
        val out = mutableListOf<CoconutUsage>()
        for (c in db.coconutDao().fifo()) {
            if (w <= 0 && m <= 0) break
            val useW = minOf(w, c.remainingWaterMl)
            val useM = minOf(m, c.remainingMeatGram)
            if (useW > 0 || useM > 0) {
                out += CoconutUsage(c.id, useW, useM)
                w -= useW
                m -= useM
            }
        }
        require(w == 0 && m == 0) { "Stok bahan kelapa tidak cukup." }
        return out
    }
}
