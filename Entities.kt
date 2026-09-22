package com.deganku.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CoconutPurpose { WHOLE, INGREDIENT }
enum class CoconutStatus { BELUM_DIBUKA, TERBUKA, DISIMPAN_DI_KULKAS, HABIS, DIBUANG_RUSAK, TERJUAL_UTUH }
enum class PaymentMethod { CASH, QRIS, TRANSFER }

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val role: String,
    val pinSalt: String,
    val pinHash: String
)

@Entity(tableName = "coconuts")
data class CoconutEntity(
    @PrimaryKey val id: String,
    val code: String,
    val type: String,
    val purpose: CoconutPurpose,
    val purchasePrice: Long,
    val purchaseAt: Long,
    val status: CoconutStatus,
    val initialWaterMl: Int = 0,
    val remainingWaterMl: Int = 0,
    val initialMeatGram: Int = 0,
    val remainingMeatGram: Int = 0,
    val openedAt: Long? = null,
    val storedAt: Long? = null,
    val finishedAt: Long? = null
)

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val unit: String,
    val quantity: Int,
    val unitCost: Long
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val price: Long,
    val active: Boolean = true
)

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Long = 0,
    val productId: Long,
    val waterMl: Int,
    val meatGram: Int,
    val iceGram: Int = 0,
    val sugarPalmGram: Int = 0,
    val syrupMl: Int = 0,
    val sodaMl: Int = 0,
    val cupQty: Int = 1,
    val strawQty: Int = 1
)

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val userId: Long,
    val payment: PaymentMethod,
    val subtotal: Long,
    val hpp: Long,
    val profit: Long,
    val paidCash: Long = 0,
    val changeCash: Long = 0,
    val cancelled: Boolean = false
)

@Entity(tableName = "sale_items")
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: String,
    val productId: Long,
    val productNameSnapshot: String,
    val priceSnapshot: Long,
    val quantity: Int,
    val hppSnapshot: Long
)

@Entity(tableName = "material_usage")
data class MaterialUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: String,
    val sourceType: String,
    val sourceId: String,
    val amount: Int,
    val unit: String,
    val costSnapshot: Long
)

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey val id: Long = 0,
    val userId: Long,
    val openedAt: Long,
    val openingCash: Long,
    val closedAt: Long? = null,
    val actualCash: Long? = null,
    val variance: Long? = null
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val category: String,
    val amount: Long,
    val isPrive: Boolean = false
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val action: String,
    val createdAt: Long,
    val detail: String
)
