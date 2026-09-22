package com.deganku.app.domain

data class HppResult(val waterCost:Long,val meatCost:Long,val total:Long)
object HppEngine{
    fun coconut(price:Long,waterYield:Int,meatYield:Int,water:Int,meat:Int):HppResult{
        require(waterYield>0 && meatYield>0)
        val waterRate=price*60.0/100.0/waterYield
        val meatRate=price*40.0/100.0/meatYield
        val wc=(waterRate*water).toLong()
        val mc=(meatRate*meat).toLong()
        return HppResult(wc,mc,wc+mc)
    }
}
