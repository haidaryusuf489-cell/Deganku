package com.deganku.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface UserDao {
    @Query("SELECT * FROM users LIMIT 1") suspend fun first(): UserEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(user: UserEntity)
}
@Dao interface CoconutDao {
    @Query("SELECT * FROM coconuts ORDER BY purchaseAt ASC") fun observeAll(): Flow<List<CoconutEntity>>
    @Query("SELECT * FROM coconuts WHERE purpose = 'INGREDIENT' AND status IN ('TERBUKA','DISIMPAN_DI_KULKAS') ORDER BY openedAt ASC") suspend fun fifo(): List<CoconutEntity>
    @Query("SELECT * FROM coconuts WHERE id=:id") suspend fun get(id:String): CoconutEntity?
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(item:CoconutEntity)
    @Query("SELECT COUNT(*) FROM coconuts WHERE status='DISIMPAN_DI_KULKAS'") fun fridgeCount(): Flow<Int>
}
@Dao interface MaterialDao {
    @Query("SELECT * FROM materials ORDER BY name") fun observeAll(): Flow<List<MaterialEntity>>
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(item:MaterialEntity)
    @Query("UPDATE materials SET quantity = quantity - :qty WHERE id=:id AND quantity >= :qty") suspend fun consume(id:Long,qty:Int):Int
}
@Dao interface ProductDao {
    @Query("SELECT * FROM products WHERE active=1 ORDER BY name") fun observeActive(): Flow<List<ProductEntity>>
    @Query("SELECT * FROM products WHERE id=:id") suspend fun get(id:Long):ProductEntity?
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(item:ProductEntity)
}
@Dao interface RecipeDao {
    @Query("SELECT * FROM recipes WHERE productId=:productId LIMIT 1") suspend fun get(productId:Long):RecipeEntity?
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(item:RecipeEntity)
}
@Dao interface SaleDao {
    @Query("SELECT * FROM sales WHERE cancelled=0 ORDER BY createdAt DESC") fun observeAll():Flow<List<SaleEntity>>
    @Insert suspend fun insert(item:SaleEntity)
    @Insert suspend fun insertItem(item:SaleItemEntity)
    @Insert suspend fun insertUsage(item:MaterialUsageEntity)
    @Query("SELECT COALESCE(SUM(subtotal),0) FROM sales WHERE cancelled=0 AND createdAt BETWEEN :from AND :to") suspend fun revenue(from:Long,to:Long):Long
    @Query("SELECT COUNT(*) FROM sales WHERE cancelled=0 AND createdAt BETWEEN :from AND :to") suspend fun count(from:Long,to:Long):Int
    @Query("SELECT COALESCE(SUM(profit),0) FROM sales WHERE cancelled=0 AND createdAt BETWEEN :from AND :to") suspend fun profit(from:Long,to:Long):Long
}
@Dao interface ShiftDao {
    @Query("SELECT * FROM shifts WHERE userId=:userId AND closedAt IS NULL LIMIT 1") suspend fun active(userId:Long):ShiftEntity?
    @Insert suspend fun insert(item:ShiftEntity)
    @Query("UPDATE shifts SET closedAt=:closedAt, actualCash=:actualCash, variance=:variance WHERE id=:id") suspend fun close(id:Long,closedAt:Long,actualCash:Long,variance:Long)
}
@Dao interface ExpenseDao {
    @Insert suspend fun insert(item:ExpenseEntity)
    @Query("SELECT COALESCE(SUM(amount),0) FROM expenses WHERE isPrive=0 AND createdAt BETWEEN :from AND :to") suspend fun operational(from:Long,to:Long):Long
}
@Dao interface AuditDao { @Insert suspend fun insert(item:AuditLogEntity) }
