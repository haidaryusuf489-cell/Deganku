package com.deganku.app.domain

import com.deganku.app.data.AppDatabase
import com.deganku.app.data.CoconutPurpose
import com.deganku.app.data.CoconutStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoconutEngine(private val db: AppDatabase) {
    suspend fun open(id: String, water: Int, meat: Int) {
        withContext(Dispatchers.IO) {
            val c = db.coconutDao().get(id) ?: error("Kelapa tidak ditemukan")
            require(c.purpose == CoconutPurpose.INGREDIENT) { "Kelapa utuh tidak boleh dibuka sebagai bahan" }
            require(c.status == CoconutStatus.BELUM_DIBUKA) { "Status kelapa tidak valid" }
            require(water >= 0 && meat >= 0)
            db.coconutDao().upsert(
                c.copy(
                    status = CoconutStatus.TERBUKA,
                    initialWaterMl = water,
                    remainingWaterMl = water,
                    initialMeatGram = meat,
                    remainingMeatGram = meat,
                    openedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun store(id: String) {
        val c = db.coconutDao().get(id) ?: error("Kelapa tidak ditemukan")
        require(c.status == CoconutStatus.TERBUKA)
        db.coconutDao().upsert(c.copy(status = CoconutStatus.DISIMPAN_DI_KULKAS, storedAt = System.currentTimeMillis()))
    }
}
