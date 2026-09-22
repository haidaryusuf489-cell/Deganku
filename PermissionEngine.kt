package com.deganku.app.domain

object PermissionEngine{
    const val SALE_CREATE="SALE_CREATE"
    const val SHIFT_OPEN="SHIFT_OPEN"
    const val SHIFT_CLOSE="SHIFT_CLOSE"
    const val COCONUT_OPEN="COCONUT_OPEN"
    const val COCONUT_USE="COCONUT_USE"
    const val STOCK_ADJUST="STOCK_ADJUST"
    const val PURCHASE_CREATE="PURCHASE_CREATE"
    const val PRICE_EDIT="PRICE_EDIT"
    const val RECIPE_EDIT="RECIPE_EDIT"
    const val REPORT_PROFIT="REPORT_PROFIT"
    const val USER_MANAGE="USER_MANAGE"
    const val BACKUP_RESTORE="BACKUP_RESTORE"
    fun allowed(role:String,key:String)=when(role){
        "OWNER"->true
        "CASHIER"->key in setOf(SALE_CREATE,SHIFT_OPEN,SHIFT_CLOSE,COCONUT_USE)
        "STOCK_ADMIN"->key in setOf(COCONUT_OPEN,COCONUT_USE,STOCK_ADJUST,PURCHASE_CREATE)
        else->false
    }
}
