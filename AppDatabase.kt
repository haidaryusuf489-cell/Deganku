package com.deganku.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities=[UserEntity::class,CoconutEntity::class,MaterialEntity::class,ProductEntity::class,
        RecipeEntity::class,SaleEntity::class,SaleItemEntity::class,MaterialUsageEntity::class,
        ShiftEntity::class,ExpenseEntity::class,AuditLogEntity::class],
    version=1,
    exportSchema=true
)
abstract class AppDatabase:RoomDatabase(){
    abstract fun userDao():UserDao
    abstract fun coconutDao():CoconutDao
    abstract fun materialDao():MaterialDao
    abstract fun productDao():ProductDao
    abstract fun recipeDao():RecipeDao
    abstract fun saleDao():SaleDao
    abstract fun shiftDao():ShiftDao
    abstract fun expenseDao():ExpenseDao
    abstract fun auditDao():AuditDao

    companion object {
        @Volatile private var INSTANCE:AppDatabase?=null
        fun get(context:Context):AppDatabase = INSTANCE ?: synchronized(this){
            INSTANCE ?: Room.databaseBuilder(context,AppDatabase::class.java,"deganku.db").build().also{INSTANCE=it}
        }
    }
}
