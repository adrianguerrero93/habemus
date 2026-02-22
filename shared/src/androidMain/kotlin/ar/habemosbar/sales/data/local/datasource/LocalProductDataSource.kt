package ar.habemosbar.sales.data.local.datasource

import ar.habemosbar.sales.data.local.db.ProductEntity
import ar.habemosbar.sales.data.local.db.SimpleProductDatabase

class LocalProductDataSource(
    private val database: SimpleProductDatabase
) {
    suspend fun ensureSeeded() {
        // Data is seeded automatically in SimpleProductDatabase.onCreate
    }

    suspend fun getAllProducts(): List<ProductEntity> =
        database.getAllProducts()

    suspend fun getProductById(id: Long): ProductEntity? =
        database.getAllProducts().find { it.id == id }
}
