package ar.habemosbar.sales.domain.repository

import ar.habemosbar.sales.domain.model.Product

interface ProductRepository {
    suspend fun getAllProducts(): List<Product>
    suspend fun getProductById(id: Long): Product?
}
