package com.deganku.app.domain

import com.deganku.app.data.*

data class CoconutUsage(val coconutId:String,val water:Int,val meat:Int)
class FifoEngine(private val db:AppDatabase){
    suspend fun allocate(waterNeed:Int,meatNeed:Int):List<CoconutUsage>{
        var w=waterNeed;var m=meatNeed
        val out=mutableListOf<CoconutUsage>()
        for(c in db.coconutDao().fifo()){
            if(w<=0 && m<=0) break
            val useW=minOf(w,c.remainingWaterMl)
            val useM=minOf(m,c.remainingMeatGram)
            if(useW>0||useM>0){out+=CoconutUsage(c.id,useW,useM);w-=useW;m-=useM}
        }
        require(w==0 && m==0){"Stok bahan kelapa tidak cukup. Buka kelapa secara manual terlebih dahulu."}
        return out
    }
}
