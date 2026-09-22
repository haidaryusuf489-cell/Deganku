package com.deganku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deganku.app.data.ProductEntity
import com.deganku.app.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DegankuApp() }
    }
}

@Composable
fun DegankuApp(vm: MainViewModel = viewModel()) {
    val session by vm.session.collectAsState()
    if (session == null) {
        LoginScreen(vm)
    } else {
        MainScreen(vm, session!!)
    }
}

@Composable
fun LoginScreen(vm: MainViewModel) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text("DEGANKU", style = MaterialTheme.typography.headlineLarge)
        Text("Kasir & stok usaha es kelapa")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = { pin = it },
            label = { Text("PIN") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { error = !vm.loginDirect(pin) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("MASUK")
        }
        if (error) {
            Text("PIN salah", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(12.dp))
        Text("PIN awal demo: 1234")
    }
}

@Composable
fun MainScreen(vm: MainViewModel, user: com.deganku.app.data.UserEntity) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { },
                    label = { Text("Kasir") }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { },
                    label = { Text("Stok") }
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    icon = { },
                    label = { Text("Laporan") }
                )
                NavigationBarItem(
                    selected = tab == 3,
                    onClick = { tab = 3 },
                    icon = { },
                    label = { Text("Akun") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                0 -> CashierScreen(vm)
                1 -> StockScreen(vm, user.role)
                2 -> ReportsScreen(vm, user.role)
                else -> AccountScreen(vm, user)
            }
        }
    }
}

@Composable
fun CashierScreen(vm: MainViewModel) {
    val products by vm.products.collectAsState(initial = emptyList())
    val cart by vm.cart.collectAsState()
    var cashText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kasir", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(products) { product ->
                ProductCard(product = product, onAdd = { vm.addToCart(product) })
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Keranjang", fontWeight = FontWeight.Bold)
                if (cart.isEmpty()) {
                    Text("Belum ada item.")
                } else {
                    cart.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.qty}x ${item.product.name}")
                            Text("Rp${item.product.price * item.qty}")
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Total: Rp${vm.totalCartValue()}")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = cashText,
                    onValueChange = { cashText = it },
                    label = { Text("Uang tunai") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val cash = cashText.toLongOrNull() ?: 0L
                        vm.checkout(cash)
                        cashText = ""
                    },
                    enabled = cart.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Checkout")
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: ProductEntity, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(product.name, fontWeight = FontWeight.SemiBold)
                Text("Rp${product.price}")
            }
            Button(onClick = onAdd) {
                Text("Tambah")
            }
        }
    }
}

@Composable
fun StockScreen(vm: MainViewModel, role: String) {
    val coconuts by vm.coconuts.collectAsState(initial = emptyList())
    val fridge by vm.fridge.collectAsState(initial = 0)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Stok", style = MaterialTheme.typography.headlineMedium)
            Text("Kulkas: $fridge")
        }
        Spacer(Modifier.height(12.dp))
        if (role == "OWNER" || role == "STOCK_ADMIN") {
            Button(onClick = { vm.addDemoCoconut() }) {
                Text("+ Kelapa Bahan")
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(coconuts) { coconut ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${coconut.code} • ${coconut.purpose}")
                        Text("${coconut.status} • Rp${coconut.purchasePrice}")
                        if (
                            coconut.purpose.name == "INGREDIENT" &&
                            coconut.status.name == "BELUM_DIBUKA" &&
                            (role == "OWNER" || role == "STOCK_ADMIN")
                        ) {
                            Button(
                                onClick = { vm.openCoconut(coconut.id, 800, 250) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Buka Degan")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportsScreen(vm: MainViewModel, role: String) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Laporan", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        if (role == "OWNER") {
            Text("Dashboard profit dan laporan harian sudah tersedia pada layer data.")
        } else {
            Text("Laporan profit disembunyikan untuk role ini.")
        }
        Spacer(Modifier.height(12.dp))
        Text("Total penjualan: Rp${vm.totalSalesValue()}")
    }
}

@Composable
fun AccountScreen(vm: MainViewModel, user: com.deganku.app.data.UserEntity) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Akun", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Nama: ${user.name}")
        Text("Role: ${user.role}")
        Spacer(Modifier.height(18.dp))
        Button(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) {
            Text("Keluar")
        }
    }
}
