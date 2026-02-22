# Beer Sales Calculator MVP - Guía de Desarrollo

> Arquitectura limpia, testeable, backend-ready. Kotlin Multiplatform.

## Estado Actual

✅ **Domain completo**: Modelos, lógica de cálculo, interfaces  
✅ **Data completo**: Room + seeding automático  
✅ **Presentation (ViewModel)**: StateFlow, manejo de estado  
❌ **Presentation (UI)**: Compose Screen (pendiente)  
❌ **Tests Data**: Repository tests con Room en-memoria (pendiente)  

## Quick Start

```bash
# Compilar
./gradlew :shared:build

# Tests
./gradlew :shared:test

# Explora la estructura
find shared/src -name "*.kt" | grep sales | head -20
```

## Mapeo de Responsabilidades

### Domain (`shared/src/commonMain/kotlin/.../domain`)
**Regla**: Cero dependencias externas.

- **model/**: Datos + lógica de negocio
  - `Product.kt` - Modelo con `getPrice(CustomerType)`
  - `CartItem.kt` - Validación, `getSubtotal()`
  - `SalesCalculation.kt` - Totales (computed properties)
  - `CustomerType.kt` - Enum de tipos de cliente

- **repository/**: Interfaz de acceso a datos
  - `ProductRepository.kt` - Define contrato (sin Room)

- **usecase/**: Lógica pura
  - `CartCalculator.kt` - Cálculo de carrito

### Data (`shared/src/androidMain/kotlin/.../data/local`)
**Regla**: Aquí vive Room. Mapeo entre entities y domain models.

- **db/**: Room internals
  - `ProductEntity.kt` - @Entity (id, name, precioConsumerFinal, precioRetail)
  - `ProductDAO.kt` - @Dao (getAllProducts, getById, count)
  - `AppDatabase.kt` - @Database
  - `ProductSeed.kt` - 13 productos Baum hardcoded

- **datasource/**: Acceso a persistencia
  - `LocalProductDataSource.kt` - ensureSeeded(), getAllProducts()

- **repository/**: Implementación del contrato
  - `ProductRepositoryImpl.kt` - Usa LocalProductDataSource, mapea Entity → Product

### Presentation (`shared/src/commonMain` + `androidMain`)
**Regla**: ViewModel solo en Android (depende de lifecycle). UiState en común.

- **commonMain/presentation/**
  - `SalesUiState.kt` - Data class inmutable (mapeo a UI)

- **androidMain/presentation/viewmodel/**
  - `SalesViewModel.kt` - ViewModel + StateFlow (Android-specific)

## Flujo de Datos

```
┌─────────────────────────────────────────────────────────┐
│                      UI (Compose)                       │
│                   observa uiState                       │
│                 updateQuantity(productId)               │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   SalesViewModel                        │
│            MutableStateFlow<SalesUiState>               │
│   • loadProducts() → getAllProducts()                   │
│   • updateQuantity(pid, qty) → state.cartItems         │
│   • setCustomerType() → estado                         │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│            ProductRepository (interface)                │
│                  (Domain Layer)                         │
│   suspend fun getAllProducts(): List<Product>          │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│            ProductRepositoryImpl                        │
│                  (Data Layer)                          │
│   usa LocalProductDataSource                          │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│         LocalProductDataSource (Room)                   │
│   • ensureSeeded() → carga ProductSeed si empty        │
│   • getAllProducts() → ProductDAO.getAllProducts()     │
└─────────────────────────────────────────────────────────┘
```

## Testing Strategy

### Unitarios (Domain) ✅ Completados
```bash
./gradlew :shared:test
```

- `DomainModelTests.kt`: Product, CartItem, SalesCalculation
- `CartCalculatorTest.kt`: Use case (sin Room, sin framework)
- **Ventaja**: Rápidos (~100ms), no requieren Android

### Integración (Repository) 📋 Pendiente
```kotlin
// Idea: Room en-memoria para tests
val db = Room.inMemoryDatabaseBuilder(
    context, 
    AppDatabase::class.java
).build()

val repository = ProductRepositoryImpl(
    LocalProductDataSource(db.productDAO())
)

// Test: val products = repository.getAllProducts()
```

## Cómo Agregar Lógica

### 1. Nueva regla de negocio → Domain
```kotlin
// shared/src/commonMain/kotlin/.../domain/usecase/

class DiscountCalculator {
    fun calculateDiscount(subtotal: Double, customerType: CustomerType): Double {
        return when (customerType) {
            CustomerType.COMERCIO -> subtotal * 0.1  // 10% descuento
            CustomerType.CONSUMIDOR_FINAL -> 0.0
        }
    }
}

// Tests en commonTest (sin Android)
class DiscountCalculatorTest {
    @Test
    fun testRetailDiscount() {
        val calc = DiscountCalculator()
        val discount = calc.calculateDiscount(1000.0, CustomerType.COMERCIO)
        assertEquals(100.0, discount)
    }
}
```

### 2. Nueva tabla → Data
```kotlin
// shared/src/androidMain/kotlin/.../data/local/db/

@Entity(tableName = "cart_history")
data class CartHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subtotal: Double,
    val total: Double,
    val createdAt: Long = System.currentTimeMillis()
)

// Agregar a AppDatabase
@Database(
    entities = [ProductEntity::class, CartHistoryEntity::class],
    version = 2  // incrementar versión
)
```

### 3. Exposer datos → Repository
```kotlin
// Domain interface (shared/src/commonMain)
interface SalesRepository {
    suspend fun saveCart(subtotal: Double, total: Double)
    suspend fun getCartHistory(): List<CartHistoryItem>
}

// Data impl (shared/src/androidMain)
class SalesRepositoryImpl(
    private val db: AppDatabase
) : SalesRepository {
    override suspend fun saveCart(subtotal: Double, total: Double) {
        db.cartHistoryDAO().insert(CartHistoryEntity(subtotal, total))
    }
}
```

### 4. Actualizar ViewModel
```kotlin
class SalesViewModel(
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository  // ← nuevo
) : ViewModel() {

    fun saveAndClearCart() {
        viewModelScope.launch {
            val state = _uiState.value
            salesRepository.saveCart(state.subtotal, state.total)
            _uiState.update { it.copy(cartItems = emptyMap()) }
        }
    }
}
```

## Integración Backend (Futuro)

### Paso 1: Crear API Service
```kotlin
// shared/src/androidMain/.../data/remote/

interface ProductService {
    @GET("/api/products")
    suspend fun getProducts(): List<ProductApiResponse>
}

data class ProductApiResponse(
    val id: Long,
    val name: String,
    val priceConsumerFinal: Double,
    val priceRetail: Double
)
```

### Paso 2: Remote DataSource
```kotlin
class RemoteProductDataSource(
    private val service: ProductService
) {
    suspend fun getProducts(): List<Product> =
        service.getProducts().map { it.toDomain() }
}
```

### Paso 3: Update Repository
```kotlin
class ProductRepositoryImpl(
    private val local: LocalProductDataSource,
    private val remote: RemoteProductDataSource
) : ProductRepository {
    override suspend fun getAllProducts(): List<Product> = try {
        remote.getProducts()  // Intenta backend
            .also { local.saveProducts(it) }  // Cache
    } catch (e: Exception) {
        local.getAllProducts()  // Fallback
    }
}
```

**Resultado**: Cero cambios en Domain, Presentation, Tests.

## Debugging

### Inspeccionar estado
```kotlin
// En ViewModel test o Compose preview
val state = viewModel.uiState.value
println("Total: ${state.total}")
println("Items: ${state.itemCount}")
println("Productos: ${state.products.size}")
```

### Room queries
```bash
./gradlew appDatabaseSql  # (si aplica)
# O abrir Android Studio → Device File Explorer → 
# data/data/com.habemus.*/databases/
```

## Estructura de Commits (Recomendado)

```
feat(domain): Add DiscountCalculator use case

- Calcula descuento según tipo de cliente
- Tests incluidos
- Domain-only, sin dependencias
```

```
feat(data): Add CartHistory entity and DAO

- Persistencia de historial de ventas
- Versión DB incrementada a 2
- Migraciones (manual si aplica)
```

```
feat(presentation): Add saveCart button and logic

- Botón en UI para guardar carrito
- Persiste subtotal + total
- Limpia carrito después
```

## Próximos Steps (Prioridad)

1. **Compose UI Screen** (visual)
   - List de products con cantidades
   - Selector de CustomerType (radio buttons)
   - Totales visibles
   - Botones Clear, Save

2. **Repository Unit Tests**
   - Room in-memory
   - Test ProductRepositoryImpl
   - Test seeding

3. **Domain Expansion**
   - Validación de precios (> 0)
   - Lógica de descuentos (si aplica)

4. **Backend Preparation**
   - Retrofit + OkHttp setup
   - RemoteProductDataSource
   - Cache-first strategy

## Referencias

- [ARCHITECTURE.md](./ARCHITECTURE.md) - Decisiones de diseño
- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Clean Architecture by Robert Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

**Última actualización**: 2026-02-21  
**Status**: MVP Domain + Data ✅ | Presentation (ViewModel) ✅ | UI (pending)
