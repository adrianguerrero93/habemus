package ar.habemosbar.sales.data.local.repository

import ar.habemosbar.sales.data.local.datasource.LocalProductDataSource
import ar.habemosbar.sales.data.local.db.ProductEntity
import ar.habemosbar.sales.domain.model.Product
import ar.habemosbar.sales.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val localDataSource: LocalProductDataSource
) : ProductRepository {

    override suspend fun getAllProducts(): List<Product> {
        localDataSource.ensureSeeded()
        return localDataSource.getAllProducts().map { it.toDomainModel() }
    }

    override suspend fun getProductById(id: Long): Product? {
        return localDataSource.getProductById(id)?.toDomainModel()
    }

    private fun ProductEntity.toDomainModel(): Product =
        Product(
            id = id,
            name = name,
            priceConsumerFinal = priceConsumerFinal,
            priceRetail = priceRetail
        )
}
