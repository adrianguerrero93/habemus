# 🍺 Beer Sales Calculator - Documentación

## Quick Reference

| Documento | Propósito |
|-----------|-----------|
| **[ARCHITECTURE.md](./ARCHITECTURE.md)** | Decisiones de diseño, estructura, Backend-ready |
| **[DEVELOPMENT.md](./DEVELOPMENT.md)** | Guía paso-a-paso para agregar features |
| **[EXAMPLES.md](./EXAMPLES.md)** | Ejemplos reales: descuentos, test, migración backend |
| **[.github/copilot-instructions.md](./.github/copilot-instructions.md)** | Configuración para futuros Copilot sessions |

---

## 📦 Estado Actual

**MVP Completado**
```
Domain Layer      ✅ Completo (Models + Logica + Tests)
Data Layer        ✅ Completo (Room + Seeding)
Presentation VM   ✅ Completo (StateFlow + State)
---
Presentation UI   ⏳ Pendiente (Compose Screen)
Repository Tests  ⏳ Pendiente (Integration tests)
Backend           ⏳ Future (HTTP API)
```

**Tests**: 16 unitarios ✅ PASSING

---

## 🚀 Inicio Rápido

```bash
# Compilar
./gradlew :shared:build

# Tests
./gradlew :shared:test

# Explorar
ls -la shared/src/commonMain/kotlin/com/habemus/sales/domain/
```

---

## 🏗️ Arquitectura (1 minuto)

```
Presentation (ViewModel + UI)
        ↓
Domain (Models + Use Cases)
        ↓
Data (Room + API future)
```

**Regla clave**: Domain CERO dependencias externas → testeable sin Android

---

## 📋 Archivos Creados

### Domain (commonMain) - 7 archivos
- `CustomerType.kt` - Enum
- `Product.kt` - Modelo con getPrice()
- `CartItem.kt` - Validación + getSubtotal()
- `SalesCalculation.kt` - Totales (computed)
- `ProductRepository.kt` - Interface (sin Room)
- `CartCalculator.kt` - Use case puro
- **Tests**: DomainModelTests.kt + CartCalculatorTest.kt

### Data (androidMain) - 6 archivos
- `ProductEntity.kt` - @Entity
- `ProductDAO.kt` - @Dao
- `AppDatabase.kt` - @Database
- `ProductSeed.kt` - 13 productos Baum
- `LocalProductDataSource.kt` - Room access
- `ProductRepositoryImpl.kt` - Maps Entity → Domain

### Presentation (androidMain) - 2 archivos
- `SalesUiState.kt` (commonMain) - State immutable
- `SalesViewModel.kt` (androidMain) - StateFlow + viewModelScope

### Documentación - 4 archivos
- `ARCHITECTURE.md` - Decisiones técnicas
- `DEVELOPMENT.md` - Guía de desarrollo
- `EXAMPLES.md` - Real-world examples
- `README_DOCS.md` - Este archivo

**Total: 22 archivos, ~3000 líneas de código, BUILD ✅, TESTS ✅**

---

## 🧪 Testing

### Unitarios (Domain) - Multiplataforma
```bash
./gradlew :shared:test
```
✅ 16 tests, 100ms, sin Android

**Qué testean**:
- `Product.getPrice()` respeta CustomerType
- `CartItem.getSubtotal()` cálculo correcto
- `SalesCalculation.total` suma correcta
- Validación cantidad >= 0
- Multi-item mixed prices

### Integración (Repository) - Future
```bash
./gradlew :shared:connectedAndroidTest
```
Room in-memory database, test ProductRepositoryImpl

---

## 🔄 Flujo de Datos (User Action)

```
1. User selecciona cantidad → updateQuantity(productId, qty)
        ↓
2. ViewModel → _uiState.update() { cartItems = ... }
        ↓
3. UI observa uiState.total (computed property)
        ↓
4. Compose recompone y muestra nuevo total
```

---

## 🛠️ Próximos Steps

### Corto Plazo (Essential)
1. [ ] **Compose UI Screen** - Lista + input + totales
2. [ ] **Repository Tests** - Room in-memory
3. [ ] **Domain validation** - precios > 0, etc

### Mediano Plazo (Nice-to-have)
4. [ ] **Persistencia CartItem** - Tabla extra + DAO
5. [ ] **Formateo moneda** - ARS $
6. [ ] **ViewModel factory** - Inyección de dependencias

### Largo Plazo (Backend)
7. [ ] **Retrofit setup** - HTTP client
8. [ ] **RemoteProductDataSource** - API calls
9. [ ] **Cache-first strategy** - Local fallback
10. [ ] **Sincronización** - Historial de ventas

---

## 💡 Key Design Decisions

| Aspecto | Decisión | Por qué |
|---------|----------|--------|
| **Domain independence** | Cero imports Android | Reutilizable en backend, testeable |
| **Repository pattern** | Interface en domain, impl en data | Cambiar Room → API sin tocar domain |
| **Seeding** | Automático en primer load | Base siempre poblada, sin SQL manual |
| **State immutable** | SalesUiState data class | StateFlow reactivo, fácil testing |
| **suspend functions** | En lugar de callbacks | Structured concurrency, mejor manejo de coroutines |

---

## 🚨 Gotchas & Anti-patterns

❌ **NO hacer**:
```kotlin
// DON'T: Lógica en UI
fun CalculateTotal() {
    var total = 0.0
    for (...) total += ...  // ← FRÁGIL
}

// DON'T: Activity como repository
class MyActivity : AppCompatActivity(), DataFetcher { ... }

// DON'T: Callback hell
fun getData(callback: (Data) -> Unit) { ... }  // ← use suspend
```

✅ **HACER**:
```kotlin
// DO: Domain puro
data class SalesCalculation { val total: Double ... }

// DO: Repository pattern
interface ProductRepository { suspend fun getAll(): List<Product> }

// DO: Suspend functions
class ProductRepositoryImpl : ProductRepository {
    override suspend fun getAll() = ...
}
```

---

## 📚 Recursos

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Room Persistence](https://developer.android.com/training/data-storage/room)
- [ViewModel + StateFlow](https://developer.android.com/topic/architecture/ui-layer/state-holders#state-holders)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-basics.html)

---

## 🎯 Checklist para PRs

Antes de hacer push:
- [ ] Domain: Cero imports Android/framework
- [ ] Tests: Nuevas features con unit tests
- [ ] Build: `./gradlew :shared:build` ✅
- [ ] Tests: `./gradlew :shared:test` ✅
- [ ] Naming: Clases _Impl, _Entity, suspend functions

---

## 🐛 Debugging Tips

```bash
# Ver estructura
find shared/src -name "*.kt" -path "*/sales/*" | head -20

# Limpiar build
./gradlew clean :shared:build

# Single test
./gradlew :shared:test --tests "CartCalculatorTest"

# Con verbose
./gradlew :shared:test --info 2>&1 | grep -i "calculator"

# Room inspection (si aplica)
adb shell "sqlite3 /data/data/com.habemus.*/databases/*.db .tables"
```

---

## 📞 Soporte

### Preguntas frecuentes

**Q: ¿Por qué Room en androidMain y no en commonMain?**
A: Room es Android-only. iOS usaría SQLite directly. Así que data/local/db es plataforma-específica.

**Q: ¿Cómo agrego un nuevo producto?**
A: Edita `ProductSeed.kt`, agranda el list. Al next load, `ensureSeeded()` lo carga.

**Q: ¿Cómo testeo sin emulator?**
A: Los domain tests corren en JVM puro. `./gradlew :shared:test` no requiere Android.

**Q: ¿Cómo cambio a API REST?**
A: Ver [EXAMPLES.md](./EXAMPLES.md) sección "Cambiar Room → Backend". Cero cambios en Domain/Tests.

---

**Última actualización**: 2026-02-21  
**Maintainer**: Copilot (Senior Android/KMP Engineer)  
**Status**: MVP ✅ | Ready for scaling 🚀
