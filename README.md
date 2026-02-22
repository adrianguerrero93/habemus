# 🍺 Habemus - Beer Sales Calculator

> Internal mobile app for beer sales calculation. Built with Kotlin Multiplatform (Android + iOS).

## Current Status

✅ **MVP (Domain + Data)** - Ready for UI phase  
✅ **16 Tests Passing**  
✅ **Build Successful**  
📚 **Comprehensive Documentation**

See [README_DOCS.md](./README_DOCS.md) for full documentation index.

---

## 🚀 Quick Start

```bash
# Compile shared module (domain + data + tests)
./gradlew :shared:build

# Run all tests (16 passing)
./gradlew :shared:test

# Build Android app
./gradlew :composeApp:assembleDebug
```

## 📚 Documentation

Complete documentation is available:
- **[README_DOCS.md](./README_DOCS.md)** - Documentation index + quick reference
- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - Design decisions + module structure  
- **[DEVELOPMENT.md](./DEVELOPMENT.md)** - Step-by-step development guide
- **[EXAMPLES.md](./EXAMPLES.md)** - Real-world code examples

## 📊 MVP Status

✅ **Domain Layer** (100%) - Models, interfaces, use cases, 100% tested  
✅ **Data Layer** (100%) - Room schema, seeding, repository implementation  
✅ **Presentation ViewModel** (100%) - StateFlow, immutable state  
⏳ **UI Screen** - Pending (next phase)

**Tests**: 16/16 passing | **Build**: ✅ SUCCESSFUL | **Compiler**: 0 errors

## 🏗️ Architecture

```
Domain Layer (Pure Kotlin, no Android deps)
    ↓
Data Layer (Room, auto-seeding)
    ↓
Presentation (ViewModel, StateFlow)
    ↓
UI (Compose - pending)
```

**Key Design**: 
- Domain is completely independent → testeable without Android
- Repository pattern → easy to swap Room ↔ API backend
- Immutable state → predictable StateFlow updates

## 📦 Project Structure

- **[/shared](./shared/src)** - Multiplatform shared code
  - `commonMain` - Domain layer + tests ✅
  - `androidMain` - Data layer (Room) + ViewModel ✅
  
- **[/composeApp](./composeApp/src)** - Compose Multiplatform UI (Android + iOS)
  
- **[/server](./server/src/main/kotlin)** - Ktor backend server
  
- **[/iosApp](./iosApp/iosApp)** - iOS native app

## 🧪 Testing

```bash
./gradlew :shared:test
```

16 unit tests covering:
- Product, CartItem, SalesCalculation models
- CartCalculator use case
- All tests pass in ~100ms without Android framework

## 🛠️ Next Steps

1. [ ] Implement Compose UI Screen (list + quantity inputs)
2. [ ] Add customer type selector
3. [ ] Repository integration tests
4. [ ] Backend API integration

## 📝 Included Features

✅ Product management with dual pricing (consumer + retail)  
✅ Cart calculation with real-time totals  
✅ Customer type selection  
✅ Automatic database seeding (13 Baum beer products)  
✅ Comprehensive unit tests  
✅ Clean architecture (backend-ready)

---

Learn more:
- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Compose Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html)
