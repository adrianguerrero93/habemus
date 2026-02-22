package com.habemus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

external fun encodeURIComponent(uri: String): String

@JsName("open")
external fun windowOpen(url: String, target: String)

fun formatPrice(value: Double): String {
    return (value * 100).toInt().let { cents ->
        "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    }
}

fun generateOrderSummary(cartItems: List<CartItem>, isRetail: Boolean): String {
    if (cartItems.isEmpty()) return "Carrito vacío"
    
    val lines = mutableListOf<String>()
    var total = 0.0
    
    cartItems.forEach { item ->
        val price = if (isRetail) item.product.priceRetail else item.product.priceCommerce
        val subtotal = price * item.quantity
        total += subtotal
        
        val productName = item.product.name
            .replace("LATA BAUM ", "")
            .replace(" 473 CC", "")
        
        lines.add("${productName}: \$${formatPrice(price)} × ${item.quantity} = \$${formatPrice(subtotal)}")
    }
    
    lines.add("")
    lines.add("Total: \$${formatPrice(total)}")
    
    return lines.joinToString("\n")
}

fun shareToWhatsApp(text: String) {
    val encodedText = encodeURIComponent(text)
    val whatsappUrl = "https://wa.me/?text=$encodedText"
    windowOpen(whatsappUrl, "_blank")
}

data class Product(
    val id: Long,
    val name: String,
    val priceRetail: Double,
    val priceCommerce: Double
)

data class CartItem(val product: Product, var quantity: Int)

val PRODUCTS = listOf(
    Product(1, "LATA BAUM BLONDE 473 CC", 2645.07, 1763.38),
    Product(2, "LATA BAUM SCOTTISH 473 CC", 2697.97, 1798.64),
    Product(3, "LATA BAUM PORTER 473 CC", 2805.27, 1870.18),
    Product(4, "LATA BAUM HONEY 473 CC", 2805.27, 1870.18),
    Product(5, "LATA BAUM IRON ALE 473 CC", 2805.27, 1870.18),
    Product(6, "LATA BAUM OLD ALE 473 CC", 3210.33, 2140.22),
    Product(7, "LATA BAUM GLADSTONE 473 CC", 3210.33, 2140.22),
    Product(8, "LATA BAUM FUCK IPA 473 CC", 3975.75, 2650.50),
    Product(9, "LATA BAUM APA 473 CC", 3504.33, 2336.22),
    Product(10, "LATA BAUM CALIFORNIA 473 CC", 4684.31, 3122.87),
    Product(11, "LATA BAUM ALGEBRA GIN TONIC 473 CC", 3778.62, 2519.08),
    Product(12, "LATA BAUM LEMON 473 CC", 2697.97, 1798.64),
    Product(13, "LATA BAUM LAGER DORADA 473 CC", 1849.80, 1233.20),
)

@Composable
fun AppWithViewModel() {
    val cart = remember { mutableStateOf<Map<Long, CartItem>>(emptyMap()) }
    val isRetail = remember { mutableStateOf(false) } // Comercio por defecto
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF003D99)),
                color = Color(0xFF003D99)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Calculadora 714",
                        color = Color.White,
                        fontSize = 28.sp,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        "Habemus Bar SRL - Distribución",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Customer Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tipo de cliente:", fontSize = 14.sp)
                Button(
                    onClick = { isRetail.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isRetail.value) Color(0xFF003D99) else Color.LightGray
                    )
                ) {
                    Text("Comercio", color = Color.White)
                }
                Button(
                    onClick = { isRetail.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRetail.value) Color(0xFF003D99) else Color.LightGray
                    )
                ) {
                    Text("Consumidor Final", color = Color.White)
                }
            }
            
            // Products List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PRODUCTS) { product ->
                    ProductRow(
                        product = product,
                        isRetail = isRetail.value,
                        currentQty = cart.value[product.id]?.quantity ?: 0,
                        onQtyChange = { newQty ->
                            val newCart = cart.value.toMutableMap()
                            if (newQty > 0) {
                                newCart[product.id] = CartItem(product, newQty)
                            } else {
                                newCart.remove(product.id)
                            }
                            cart.value = newCart
                        }
                    )
                }
            }
            
            // Totals
            val total = cart.value.values.sumOf { item ->
                val price = if (isRetail.value) item.product.priceRetail else item.product.priceCommerce
                price * item.quantity
            }
            val itemCount = cart.value.values.sumOf { it.quantity }
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF003D99)),
                color = Color(0xFF003D99)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Items: $itemCount", color = Color.White, fontSize = 16.sp)
                            Text("Total: \$${formatPrice(total)}", color = Color.White, fontSize = 18.sp)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val cartList = cart.value.values.sortedBy { it.product.name }
                                    val summary = generateOrderSummary(cartList, isRetail.value)
                                    shareToWhatsApp(summary)
                                },
                                enabled = cart.value.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF25D366),
                                    disabledContainerColor = Color(0xFFCCCCCC)
                                )
                            ) {
                                Text("Compartir", color = Color.White)
                            }
                            Button(
                                onClick = { cart.value = emptyMap() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                            ) {
                                Text("Limpiar", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductRow(
    product: Product,
    isRetail: Boolean,
    currentQty: Int,
    onQtyChange: (Int) -> Unit
) {
    val price = if (isRetail) product.priceRetail else product.priceCommerce
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                product.name,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Text(
                "\$${formatPrice(price)}",
                fontSize = 14.sp,
                color = Color(0xFF003D99),
                style = MaterialTheme.typography.labelMedium
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentQty > 0) onQtyChange(currentQty - 1) },
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("-", color = Color.Black)
            }
            
            Text(
                currentQty.toString(),
                modifier = Modifier.width(30.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 14.sp
            )
            
            Button(
                onClick = { onQtyChange(currentQty + 1) },
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003D99))
            ) {
                Text("+", color = Color.White)
            }
        }
    }
}
