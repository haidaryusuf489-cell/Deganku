package com.deganku.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deganku.app.core.security.PinSecurity
import com.deganku.app.data.*
import com.deganku.app.domain.CoconutEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class CartItem(
    val product: ProductEntity,
    val qty: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.get(application)

    val products = db.productDao().observeActive()
    val coconuts = db.coconutDao().observeAll()
    val fridge = db.coconutDao().fridgeCount()

    private val _session = MutableStateFlow<UserEntity?>(null)
    val session: StateFlow<UserEntity?> = _session.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _salesTotal = MutableStateFlow(0L)
    val salesTotal: StateFlow<Long> = _salesTotal.asStateFlow()

    init {
        viewModelScope.launch {
            if (db.userDao().first() == null) {
                val (salt, hash) = PinSecurity.create("1234")
                db.userDao().upsert(
                    UserEntity(
                        id = 1L,
                        name = "Owner",
                        role = "OWNER",
                        pinSalt = salt,
                        pinHash = hash
                    )
                )
                seedProducts()
            }
            refreshSalesTotal()
        }
    }

    fun loginDirect(pin: String): Boolean {
        val user = db.userDao().first() ?: return false
        val ok = PinSecurity.verify(pin, user.pinSalt, user.pinHash)
        if (ok) _session.value = user
        return ok
    }

    fun logout() { _session.value = null }

    fun addToCart(product: ProductEntity) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            current[index] = current[index].copy(qty = current[index].qty + 1)
        } else {
            current.add(CartItem(product, 1))
        }
        _cart.value = current
    }

    fun totalCartValue(): Long = _cart.value.sumOf { it.product.price * it.qty.toLong() }

    fun checkout(cash: Long) {
        val items = _cart.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            val user = _session.value ?: return@launch
            val subtotal = items.sumOf { it.product.price * it.qty.toLong() }
            val hpp = (subtotal * 0.6).toLong()
            val profit = subtotal - hpp
            val saleId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            db.saleDao().insert(
                SaleEntity(
                    id = saleId,
                    createdAt = now,
                    userId = user.id,
                    payment = PaymentMethod.CASH,
                    subtotal = subtotal,
                    hpp = hpp,
                    profit = profit,
                    paidCash = cash,
                    changeCash = maxOf(0L, cash - subtotal)
                )
            )

            items.forEach { item ->
                db.saleDao().insertItem(
                    SaleItemEntity(
                        saleId = saleId,
                        productId = item.product.id,
                        productNameSnapshot = item.product.name,
                        priceSnapshot = item.product.price,
                        quantity = item.qty,
                        hppSnapshot = hpp / maxOf(1, items.size)
                    )
                )
            }

            _cart.value = emptyList()
            refreshSalesTotal()
        }
    }

    fun refreshSalesTotal() = viewModelScope.launch {
        val from = startOfDayMillis()
        val to = from + 86_400_000L
        _salesTotal.value = db.saleDao().revenue(from, to)
    }

    private fun startOfDayMillis(): Long {
        val now = System.currentTimeMillis()
        val day = java.util.Calendar.getInstance().apply { timeInMillis = now }
        day.set(java.util.Calendar.HOUR_OF_DAY, 0)
        day.set(java.util.Calendar.MINUTE, 0)
        day.set(java.util.Calendar.SECOND, 0)
        day.set(java.util.Calendar.MILLISECOND, 0)
        return day.timeInMillis
    }

    fun seedProducts() = viewModelScope.launch {
        if (db.productDao().get(1L) == null) {
            db.productDao().upsert(ProductEntity(1L, "Es Degan", 5000L, true))
            db.productDao().upsert(ProductEntity(2L, "Es Degan Gula Aren", 5000L, true))
            db.productDao().upsert(ProductEntity(3L, "Es Degan Sirup", 6000L, true))
            db.productDao().upsert(ProductEntity(4L, "Es Degan Fanta", 8000L, true))
            db.productDao().upsert(ProductEntity(5L, "Jus Degan", 10000L, true))
        }
    }

    fun addDemoCoconut() = viewModelScope.launch {
        val n = System.currentTimeMillis()
        db.coconutDao().upsert(
            CoconutEntity(
                id = UUID.randomUUID().toString(),
                code = "KLP-${n % 100000}",
                type = "Kelapa",
                purpose = CoconutPurpose.INGREDIENT,
                purchasePrice = 6000L,
                purchaseAt = n,
                status = CoconutStatus.BELUM_DIBUKA
            )
        )
    }

    fun openCoconut(id: String, water: Int, meat: Int) = viewModelScope.launch {
        CoconutEngine(db).open(id, water, meat)
    }
}
