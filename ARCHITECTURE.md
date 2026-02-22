# Arquitectura: Beer Sales Calculator (MVP)

## 1. Decisiones Clave de Arquitectura

### 1.1 Inversión de Dependencias (Domain-First)
```
Presentation → Domain
   ↓            ↑
  Data ────────→
```

- **Domain** (core): Models, interfaces, use cases. CERO dependencias externas.
- **Data**: Implementaciones (Room), DataSources, Repositories.
- **Presentation**: ViewModel, UI State, Compose UI.

**Por qué**: La lógica de negocio vive en Domain, testeable sin Android framework. Si el futuro requiere backend, Domain se reutiliza 1:1.

### 1.2 Repository Pattern + Data Source
```
SalesViewModel
    ↓
ProductRepository (interface) ← Domain
    ↓
ProductRepositoryImpl ← Data
    ↓
LocalProductDataSource
    ↓
Room (AppDatabase + ProductDAO)
```

**Por qué**: 
- Repository = abstracción de datos (interfaz en domain)
- Data source = detalles de persistencia (Room, API calls, cache)
- Cambiar Room → API REST = solo cambiar DataSource + Repository impl

### 1.3 Seeding Automático
```kotlin
LocalProductDataSource.ensureSeeded() 
// Ejecuta en el primer getAllProducts()
// Si count() == 0, carga ProductSeed
```

**Por qué**: Base poblada automáticamente. Cero SQL manual. En producción, el backend reemplaza esto.

## 2. Estructura de Archivos

```
shared/
├── src/commonMain/kotlin/com/habemus/sales/
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Product.kt           # id, name, precioConsumidor, precioComercio
│   │   │   ├── CustomerType.kt      # CONSUMIDOR_FINAL | COMERCIO
│   │   │   ├── CartItem.kt          # product + quantity
│   │   │   └── SalesCalculation.kt  # items + total
│   │   ├── repository/
│   │   │   └── ProductRepository.kt # interface (sin Room, sin framework)
│   │   └── usecase/
│   │       └── CartCalculator.kt    # lógica de cálculo pura
│   │
│   └── presentation/
│       ├── SalesUiState.kt          # estado inmutable (data class)
│       ├── screen/
│       │   └── (UI Compose - next phase)
│       └── (más campos si es necesario)
│
├── src/androidMain/kotlin/com/habemus/sales/
│   ├── data/
│   │   └── local/
│   │       ├── db/
│   │       │   ├── ProductEntity.kt
│   │       │   ├── ProductDAO.kt
│   │       │   ├── AppDatabase.kt
│   │       │   └── ProductSeed.kt   # datos iniciales (Baum)
│   │       ├── datasource/
│   │       │   └── LocalProductDataSource.kt
│   │       └── repository/
│   │           └── ProductRepositoryImpl.kt
│   │
│   └── presentation/
│       └── viewmodel/
│           └── SalesViewModel.kt    # StateFlow, viewModelScope
│
└── src/commonTest/kotlin/com/habemus/sales/
    ├── domain/
    │   ├── DomainModelTests.kt      # Product, CartItem, SalesCalculation
    │   └── CartCalculatorTest.kt    # use case testing
    └── data/
        └── (Repository tests con Room en memoria - next phase)
```

## 3. Modelos del Dominio

### Product (Domain Model)
```kotlin
data class Product(
    val id: Long,
    val name: String,
    val priceConsumerFinal: Double,
    val priceRetail: Double
) {
    fun getPrice(customerType: CustomerType): Double
}
```

- CERO lógica de persistencia aquí
- `getPrice()` = strategy para seleccionar precio según tipo de cliente
- Facilmente serializable (si futuro es API REST)

### CartItem
```kotlin
data class CartItem(
    val product: Product,
    val quantity: Int
) {
    fun getSubtotal(customerType: CustomerType): Double
}
```

- Encapsula cálculo de subtotal
- Validación: cantidad >= 0

### SalesCalculation
```kotlin
data class SalesCalculation(
    val items: List<CartItem>,
    val customerType: CustomerType
) {
    val subtotal: Double // sumOf { item.getSubtotal(customerType) }
    val total: Double
    val itemCount: Int
}
```

- Resultado final del carrito
- Cálculos computed properties (no estado mutable)

## 4. Tests Unitarios (Domain)

**Ubicación**: `commonTest` (multiplataforma, sin Android)

### DomainModelTests.kt
```kotlin
// Product.getPrice() cambia según CustomerType ✓
// CartItem.getSubtotal() = product.price * quantity ✓
// SalesCalculation.total = suma de subtotales ✓
// CartItem(quantity < 0) → IllegalArgumentException ✓
```

**Ejecución**: 
```bash
./gradlew :shared:test
```

## 5. Repository Pattern + In-Memory para Tests

Interfaz en domain (sin Room):
```kotlin
interface ProductRepository {
    suspend fun getAllProducts(): List<Product>
    suspend fun getProductById(id: Long): Product?
}
```

Implementación en data (con Room):
```kotlin
class ProductRepositoryImpl(
    private val localDataSource: LocalProductDataSource
) : ProductRepository {
    override suspend fun getAllProducts(): List<Product> {
        localDataSource.ensureSeeded()
        return localDataSource.getAllProducts().map { it.toDomainModel() }
    }
}
```

Tests futuros: `InMemoryProductRepository` en test code = no Room, fast.

## 6. ViewModel (Android-specific)

**Ubicación**: `androidMain` (depende de ViewModel, Coroutines)

```kotlin
class SalesViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    init {
        loadProducts()  // launch en viewModelScope
    }

    fun updateQuantity(productId: Long, quantity: Int)
    fun setCustomerType(customerType: CustomerType)
    fun clearCart()
}
```

**UI State** (en commonMain, data class):
```kotlin
data class SalesUiState(
    val products: List<Product>,
    val cartItems: Map<Long, Int>,     // productId → quantity
    val customerType: CustomerType,
    val isLoading: Boolean,
    val error: String?
) {
    val subtotal: Double // computed
    val total: Double    // computed
    val itemCount: Int   // computed
}
```

## 7. Room Schema

### ProductEntity
```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false) val id: Long,
    val name: String,
    val priceConsumerFinal: Double,
    val priceRetail: Double
)
```

### ProductDAO
```kotlin
@Dao
interface ProductDAO {
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}
```

### AppDatabase
```kotlin
@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDAO(): ProductDAO
}
```

## 8. Seeding (ProductSeed.kt)

13 productos Baum con precios en dos tiers:
```kotlin
object ProductSeed {
    val products = listOf(
        "1" to ("LATA BAUM BLONDE 473 CC" to Pair(2645.07, 1763.38)),
        // consumidor, comercio
        ...
    )
}
```

Cargados automáticamente en primer `getAllProducts()`.

## 9. Flujo de Datos (Current State)

```
SalesViewModel (init)
    ↓
loadProducts()
    ↓
ProductRepository.getAllProducts()
    ↓
ProductRepositoryImpl.getAllProducts()
    ↓
LocalProductDataSource.ensureSeeded() → ProductSeed → Room
    ↓
Actualiza _uiState con productos
    ↓
UI observa uiState.products
```

Cuando user selecciona cantidad:
```
UI: updateQuantity(productId=1, quantity=2)
    ↓
SalesViewModel.updateQuantity()
    ↓
_uiState.update { copy(cartItems = ...) }
    ↓
UI recalcula total via state.total (computed property)
```

## 10. Testabilidad

✅ **Domain es testeable sin Android**: Kotlin Test, JUnit  
✅ **Cálculos aislados**: CartCalculator, SalesCalculation  
✅ **Inversión de dependencias**: Repository interface ← mockeable  
✅ **In-memory data sources**: sin Room en tests unitarios  
✅ **StateFlow inmutable**: fácil de probar estado

## 11. Preparado para Backend

### Paso 1: API Model
```kotlin
data class ProductApiResponse(...)

@Service
class ProductApiService(retrofit: Retrofit) {
    suspend fun getProducts(): List<ProductApiResponse>
}
```

### Paso 2: RemoteProductDataSource
```kotlin
class RemoteProductDataSource(service: ProductApiService) {
    suspend fun getAllProducts(): List<ProductEntity>
}
```

### Paso 3: Repository Strategy
```kotlin
class ProductRepositoryImpl(
    private val local: LocalProductDataSource,
    private val remote: RemoteProductDataSource
) : ProductRepository {
    override suspend fun getAllProducts() = try {
        remote.getAllProducts()  // Intenta backend
            .also { local.saveProducts(it) }  // Cache local
    } catch (e: Exception) {
        local.getAllProducts()  // Fallback offline
    }
}
```

**CERO cambios en Domain, Presentation, Tests**.

## 12. Guía de Compilación y Tests

```bash
# Compilar shared
./gradlew :shared:build

# Correr tests unitarios (domain)
./gradlew :shared:test

# Compilar composeApp
./gradlew :composeApp:assembleDebug

# Compilar para iOS (si aplica)
# (requiere Xcode)
```

## 13. Próximos Pasos (Out of Scope MVP)

- [ ] Compose UI Screen (calculadora visual)
- [ ] Repository tests (con Room en memoria)
- [ ] Persistencia de carrito (CartItem table)
- [ ] Validación de precios (precio > 0, etc)
- [ ] Formateo de moneda (ARS $)
- [ ] Sync con backend
- [ ] Historial de ventas
