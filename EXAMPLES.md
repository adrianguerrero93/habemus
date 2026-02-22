# Ejemplos Prácticos - Beer Sales Calculator

## 1. Cómo la Lógica Evita Cambios Grandes Futuros

### Scenario: "Mañana quiero agregar impuestos"

**Opción A: Sin arquitectura limpia (frágil)**
```kotlin
// UI calcula todo
fun calculateTotal(): Double {
    var total = 0.0
    for (item in cartItems) {
        total += item.price * item.qty
    }
    total *= 1.21  // IVA hardcoded en UI ← FRÁGIL
    return total
}
```

**Problema**: 
- Lógica mezclada con UI
- Cambiar impuesto requiere tocar Compose
- No testeable
- Copiar lógica a otros lugares → inconsistencia

**Opción B: Con arquitectura (flexible)**
```kotlin
// Domain (pure)
data class SalesCalculation(
    val items: List<CartItem>,
    val customerType: CustomerType,
    val taxRate: Double = 0.21  // ← configurable
) {
    val subtotal: Double
        get() = items.sumOf { it.getSubtotal(customerType) }
    
    val tax: Double
        get() = subtotal * taxRate
    
    val total: Double
        get() = subtotal + tax
}

// Test (commonTest, sin Android)
class SalesCalculationTest {
    @Test
    fun testWithTax() {
        val calc = SalesCalculation(
            items = listOf(CartItem(product, 1)),
            customerType = CustomerType.CONSUMIDOR_FINAL,
            taxRate = 0.21
        )
        assertEquals(subtotal * 1.21, calc.total)
    }
}

// ViewModel (presentation)
private fun calculateSalesWithTax() {
    _uiState.update { state ->
        state.copy(
            calculation = SalesCalculation(
                items = state.cartItemsList,
                customerType = state.customerType,
                taxRate = 0.21  // ← desde config
            )
        )
    }
}
```

**Resultado**:
- Lógica aislada en Domain
- Test puro, rápido, sin Android
- ViewModel se adapta
- UI no cambia (usa `state.total`)

---

## 2. Cambiar Room → Backend (Cero cambios Domain)

### HOY: Room local
```kotlin
class LocalProductDataSource(
    private val productDAO: ProductDAO
) {
    suspend fun getAllProducts(): List<ProductEntity> =
        productDAO.getAllProducts()
}

class ProductRepositoryImpl(
    private val local: LocalProductDataSource
) : ProductRepository {
    override suspend fun getAllProducts() =
        local.getAllProducts().map { it.toDomain() }
}
```

### MAÑANA: Agregar backend (CERO cambios en Domain)
```kotlin
// Crear RemoteProductDataSource
class RemoteProductDataSource(
    private val apiService: ProductApiService
) {
    suspend fun getAllProducts(): List<ProductApiResponse> =
        apiService.getProducts()
}

// Actualizar Repository (misma interfaz)
class ProductRepositoryImpl(
    private val local: LocalProductDataSource,
    private val remote: RemoteProductDataSource
) : ProductRepository {
    override suspend fun getAllProducts(): List<Product> = try {
        // Intenta backend
        remote.getAllProducts()
            .also { localDataSource.saveProducts(it) }  // Cache
    } catch (e: Exception) {
        // Fallback: offline
        local.getAllProducts()
    }
}

// Domain NO cambió ← ← ← IMPORTANTE
// ViewModel NO cambió ← ← ← IMPORTANTE
// Tests NO cambiaron ← ← ← IMPORTANTE
```

---

## 3. Testing: Domain puro vs Integration

### Test Domain (Fast, Pure)
```kotlin
// shared/src/commonTest/.../domain/ProductTest.kt
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductTest {
    @Test
    fun testGetPriceConsumerFinal() {
        val product = Product(
            id = 1L,
            name = "LATA BAUM BLONDE",
            priceConsumerFinal = 2645.07,
            priceRetail = 1763.38
        )

        val price = product.getPrice(CustomerType.CONSUMIDOR_FINAL)
        
        assertEquals(2645.07, price)
    }

    @Test
    fun testGetPriceRetail() {
        val product = Product(
            id = 1L,
            name = "LATA BAUM BLONDE",
            priceConsumerFinal = 2645.07,
            priceRetail = 1763.38
        )

        val price = product.getPrice(CustomerType.COMERCIO)
        
        assertEquals(1763.38, price)
    }
}

// Ejecución
// ./gradlew :shared:test
// Duration: ~100ms, NO requiere Android emulator/device
```

### Test Repository (Integration, future)
```kotlin
// shared/src/androidTest/.../data/ProductRepositoryTest.kt
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import kotlinx.coroutines.test.runTest

class ProductRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: ProductRepository

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        repository = ProductRepositoryImpl(
            LocalProductDataSource(db.productDAO())
        )
    }

    @Test
    fun testGetAllProductsSeeds() = runTest {
        val products = repository.getAllProducts()
        
        assertEquals(13, products.size)
        assertEquals("LATA BAUM BLONDE 473 CC", products[0].name)
    }

    @Test
    fun testGetProductById() = runTest {
        repository.getAllProducts()  // Trigger seeding
        val product = repository.getProductById(1L)
        
        assertNotNull(product)
        assertEquals("LATA BAUM BLONDE 473 CC", product?.name)
    }

    @After
    fun teardown() {
        db.close()
    }
}

// Ejecución (requiere emulator/device)
// ./gradlew :shared:connectedAndroidTest
```

---

## 4. Real-world Scenario: Agregar Descuentos

### Paso 1: Modelar en Domain
```kotlin
// domain/model/DiscountPolicy.kt
sealed class DiscountPolicy {
    abstract fun calculateDiscount(subtotal: Double, customerType: CustomerType): Double
    
    object NoDiscount : DiscountPolicy() {
        override fun calculateDiscount(subtotal: Double, customerType: CustomerType) = 0.0
    }
    
    data class FlatPercent(val percent: Double) : DiscountPolicy() {
        override fun calculateDiscount(subtotal: Double, customerType: CustomerType) =
            subtotal * (percent / 100)
    }
    
    data class PerCustomerType(
        val consumerDiscount: Double,
        val retailDiscount: Double
    ) : DiscountPolicy() {
        override fun calculateDiscount(subtotal: Double, customerType: CustomerType) =
            when (customerType) {
                CustomerType.CONSUMIDOR_FINAL -> subtotal * (consumerDiscount / 100)
                CustomerType.COMERCIO -> subtotal * (retailDiscount / 100)
            }
    }
}

// domain/usecase/DiscountCalculator.kt
class DiscountCalculator(
    private val policy: DiscountPolicy = DiscountPolicy.NoDiscount
) {
    fun calculate(subtotal: Double, customerType: CustomerType): Double =
        policy.calculateDiscount(subtotal, customerType)
}
```

### Paso 2: Actualizar Domain Model
```kotlin
// domain/model/SalesCalculation.kt
data class SalesCalculation(
    val items: List<CartItem>,
    val customerType: CustomerType,
    val discountPolicy: DiscountPolicy = DiscountPolicy.NoDiscount
) {
    val subtotal: Double
        get() = items.sumOf { it.getSubtotal(customerType) }
    
    private val calculator = DiscountCalculator(discountPolicy)
    
    val discount: Double
        get() = calculator.calculate(subtotal, customerType)
    
    val total: Double
        get() = subtotal - discount
}
```

### Paso 3: Tests (pure)
```kotlin
// commonTest/domain/DiscountCalculatorTest.kt
class DiscountCalculatorTest {
    @Test
    fun testFlatPercentDiscount() {
        val policy = DiscountPolicy.FlatPercent(10.0)
        val calculator = DiscountCalculator(policy)
        
        val discount = calculator.calculate(1000.0, CustomerType.COMERCIO)
        assertEquals(100.0, discount)
    }

    @Test
    fun testPerCustomerTypeDiscount() {
        val policy = DiscountPolicy.PerCustomerType(
            consumerDiscount = 5.0,   // 5% para consumidor
            retailDiscount = 15.0     // 15% para comercio
        )
        val calc = DiscountCalculator(policy)
        
        val consumerDiscount = calc.calculate(1000.0, CustomerType.CONSUMIDOR_FINAL)
        val retailDiscount = calc.calculate(1000.0, CustomerType.COMERCIO)
        
        assertEquals(50.0, consumerDiscount)
        assertEquals(150.0, retailDiscount)
    }
    
    @Test
    fun testSalesCalculationWithDiscount() {
        val product = Product(1L, "BAUM", 2000.0, 1000.0)
        val calc = SalesCalculation(
            items = listOf(CartItem(product, 1)),
            customerType = CustomerType.COMERCIO,
            discountPolicy = DiscountPolicy.FlatPercent(10.0)
        )
        
        assertEquals(1000.0, calc.subtotal)
        assertEquals(100.0, calc.discount)
        assertEquals(900.0, calc.total)
    }
}
```

### Paso 4: Data (fetch policy from backend/config)
```kotlin
// data/local/db/DiscountPolicyEntity.kt
@Entity(tableName = "discount_policies")
data class DiscountPolicyEntity(
    @PrimaryKey val id: Long,
    val type: String,  // "none", "flat_percent", "per_customer"
    val value1: Double?,
    val value2: Double?
)

// Mapear a domain
fun DiscountPolicyEntity.toDomain(): DiscountPolicy =
    when (type) {
        "flat_percent" -> DiscountPolicy.FlatPercent(value1 ?: 0.0)
        "per_customer" -> DiscountPolicy.PerCustomerType(
            consumerDiscount = value1 ?: 0.0,
            retailDiscount = value2 ?: 0.0
        )
        else -> DiscountPolicy.NoDiscount
    }
```

### Paso 5: ViewModel adapts
```kotlin
class SalesViewModel(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPolicyRepository  // ← nuevo
) : ViewModel() {
    
    init {
        loadDiscountPolicy()
    }
    
    private fun loadDiscountPolicy() {
        viewModelScope.launch {
            val policy = discountPolicyRepository.getActivePolicy()
            _uiState.update { state ->
                state.copy(discountPolicy = policy)
            }
        }
    }
}
```

### Paso 6: UI displays discount
```kotlin
// Compose
@Composable
fun SalesCalculatorScreen(state: SalesUiState) {
    Column {
        Text("Subtotal: $${state.subtotal}")
        if (state.discount > 0) {
            Text("Descuento: -$${state.discount}", color = Color.Green)
        }
        Text("Total: $${state.total}", style = MaterialTheme.typography.headlineMedium)
    }
}
```

**Resultado**: 
- Domain preparado para descuentos
- Tests verifican lógica
- Data puede obtenerlos de BD o API
- ViewModel los carga
- UI los muestra
- **Cero duplicación**, arquitectura escalable

---

## 5. InMemoryRepository para Testing

```kotlin
// shared/src/commonTest/.../

class InMemoryProductRepository(
    private val products: List<Product> = emptyList()
) : ProductRepository {
    override suspend fun getAllProducts(): List<Product> = products
    override suspend fun getProductById(id: Long): Product? = 
        products.find { it.id == id }
}

// Uso en tests
class SalesViewModelTest {
    private val mockProducts = listOf(
        Product(1L, "BAUM BLONDE", 2645.07, 1763.38),
        Product(2L, "BAUM SCOTTISH", 2697.97, 1798.64),
    )
    
    private val repository = InMemoryProductRepository(mockProducts)
    
    @Test
    fun testLoadProducts() = runTest {
        val viewModel = SalesViewModel(repository)
        
        advanceUntilIdle()  // Esperar coroutines
        
        val state = viewModel.uiState.value
        assertEquals(2, state.products.size)
    }
}
```

---

## 6. Checklist para PRs

- [ ] **Domain**: Cero imports de Android/framework
- [ ] **Tests**: Nuevas features tienen tests en commonTest
- [ ] **Entity ↔ Domain**: Mapeos explícitos (toDomain(), toEntity())
- [ ] **Repository**: Expone interface (domain), no detalles (Room)
- [ ] **Persistencia**: Usa suspend fun, no callbacks
- [ ] **Naming**: Clases de data suffix "-Impl", entities suffix "-Entity"

---

**Estos ejemplos muestran por qué la arquitectura importa: cambios grandes se hacen sin refactors.**
