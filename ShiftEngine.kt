package com.deganku.app.domain

object ShiftEngine{
    fun expectedCash(opening:Long,cashSales:Long,cashExpenses:Long,prive:Long):Long =
        opening+cashSales-cashExpenses-prive
    fun variance(expected:Long,actual:Long)=actual-expected
}
