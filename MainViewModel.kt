package com.deganku.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deganku.app.data.*
import com.deganku.app.core.security.PinSecurity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class Session(val user:UserEntity)
class MainViewModel(app:Application):AndroidViewModel(app){
    private val db=AppDatabase.get(app)
    val products=db.productDao().observeActive()
    val coconuts=db.coconutDao().observeAll()
    val fridge=db.coconutDao().fridgeCount()
    private val _session=MutableStateFlow<Session?>(null)
    val session=_session.asStateFlow()
    private val _message=MutableStateFlow("")
    val message=_message.asStateFlow()

    init{viewModelScope.launch{
        if(db.userDao().first()==null){
            val (s,h)=PinSecurity.create("1234")
            db.userDao().upsert(UserEntity(1,"Owner","OWNER",s,h))
            seed()
        }
    }}
    suspend fun login(pin:String):Boolean{
        val u=db.userDao().first() ?: return false
        val ok=PinSecurity.verify(pin,u.pinSalt,u.pinHash)
        if(ok)_session.value=Session(u)
        return ok
    }
    fun logout(){_session.value=null}
    fun openCoconut(id:String,water:Int,meat:Int)=viewModelScope.launch{
        com.deganku.app.domain.CoconutEngine(db).open(id,water,meat)
        _message.value="Kelapa berhasil dibuka"
    }
    fun seed()=viewModelScope.launch{
        if(db.productDao().get(1)==null){
            db.productDao().upsert(ProductEntity(1,"Es Degan",5000))
            db.productDao().upsert(ProductEntity(2,"Es Degan Gula Aren",5000))
            db.productDao().upsert(ProductEntity(3,"Es Degan Sirup",6000))
            db.productDao().upsert(ProductEntity(4,"Es Degan Fanta",8000))
            db.productDao().upsert(ProductEntity(5,"Jus Degan",10000))
        }
    }
    fun addDemoCoconut(){
        viewModelScope.launch{
            val n=System.currentTimeMillis()
            db.coconutDao().upsert(CoconutEntity(UUID.randomUUID().toString(),"KLP-${n%100000}",
                "Kelapa",CoconutPurpose.INGREDIENT,6000,n,CoconutStatus.BELUM_DIBUKA))
            _message.value="Kelapa bahan ditambahkan"
        }
    }
}
