package com.deganku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deganku.app.ui.MainViewModel

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState)
        setContent{DegankuApp()}
    }
}

@Composable
fun DegankuApp(vm:MainViewModel=viewModel()){
    val session by vm.session.collectAsState()
    if(session==null) LoginScreen(vm) else MainScreen(vm,session!!.user)
}
@Composable
fun LoginScreen(vm:MainViewModel){
    var pin by remember{mutableStateOf("")};var error by remember{mutableStateOf(false)}
    Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.Center){
        Text("DEGANKU",style=MaterialTheme.typography.headlineLarge)
        Text("Kasir & stok usaha es kelapa")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(pin,{pin=it},"PIN",singleLine=true)
        Spacer(Modifier.height(12.dp))
        Button(onClick={error=!vmLogin(vm,pin)},Modifier.fillMaxWidth()){Text("MASUK")}
        if(error)Text("PIN salah",color=MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Text("PIN awal demo: 1234")
    }
}
fun vmLogin(vm:MainViewModel,pin:String):Boolean{
    var ok=false
    kotlinx.coroutines.runBlocking {ok=vm.login(pin)}
    return ok
}
@Composable
fun MainScreen(vm:MainViewModel,user:com.deganku.app.data.UserEntity){
    var tab by remember{mutableIntStateOf(0)}
    Scaffold(bottomBar={
        NavigationBar{
            NavigationBarItem(tab==0,{tab=0},icon={},label={Text("Kasir")})
            NavigationBarItem(tab==1,{tab=1},icon={},label={Text("Stok")})
            NavigationBarItem(tab==2,{tab=2},icon={},label={Text("Laporan")})
            NavigationBarItem(tab==3,{tab=3},icon={},label={Text("Lainnya")})
        }
    }){p->
        Box(Modifier.padding(p).fillMaxSize()){
            when(tab){
                0->Cashier(vm)
                1->Stock(vm,user.role)
                2->Reports(vm,user.role)
                else->Column(Modifier.padding(20.dp)){
                    Text("Akun: ${user.name} (${user.role})")
                    Spacer(Modifier.height(12.dp))
                    Button({vm.logout()}){Text("Keluar")}
                }
            }
        }
    }
}
@Composable
fun Cashier(vm:MainViewModel){
    val products by vm.products.collectAsState(initial=emptyList())
    Column(Modifier.padding(16.dp)){
        Text("Kasir",style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        products.forEach{p->Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){
            Row(Modifier.padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){
                Text(p.name);Text("Rp${p.price}")
            }
        }}
        Text("Mesin penjualan terintegrasi pada layer domain; UI transaksi lengkap dilanjutkan pada hardening berikutnya.")
    }
}
@Composable
fun Stock(vm:MainViewModel,role:String){
    val coconuts by vm.coconuts.collectAsState(initial=emptyList())
    val fridge by vm.fridge.collectAsState(initial=0)
    Column(Modifier.padding(16.dp)){
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
            Text("Stok",style=MaterialTheme.typography.headlineMedium)
            Text("Kulkas: $fridge")
        }
        if(role=="OWNER"||role=="STOCK_ADMIN"){
            Button({vm.addDemoCoconut()}){Text("+ Kelapa Bahan")}
        }
        LazyColumn{items(coconuts){c->
            Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){
                Column(Modifier.padding(12.dp)){
                    Text("${c.code} • ${c.purpose}")
                    Text("${c.status} • Rp${c.purchasePrice}")
                    if(c.purpose.name=="INGREDIENT" && c.status.name=="BELUM_DIBUKA" && (role=="OWNER"||role=="STOCK_ADMIN")){
                        Button({vm.openCoconut(c.id,800,250)}){Text("Buka Degan")}
                    }
                }
            }
        }}
    }
}
@Composable
fun Reports(vm:MainViewModel,role:String){
    Column(Modifier.padding(16.dp)){
        Text("Laporan",style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Role: $role")
        if(role=="OWNER"){
            Text("Dashboard profit tersedia melalui SaleDao/Report layer.")
        }else{
            Text("Profit disembunyikan untuk role ini.")
        }
    }
}
