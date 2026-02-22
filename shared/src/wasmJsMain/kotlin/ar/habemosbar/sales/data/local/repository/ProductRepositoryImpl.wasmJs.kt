package ar.habemosbar.sales.data.local.repository

import ar.habemosbar.sales.domain.model.Product
import ar.habemosbar.sales.domain.repository.ProductRepository

// In-memory repository for Web (no persistence)
class ProductRepositoryImpl : ProductRepository {
    private val products = listOf(
        Product(1, "LATA BAUM BLONDE 473 CC", 1763.38, 2645.07),
        Product(2, "LATA BAUM SCOTTISH 473 CC", 1798.64, 2697.97),
        Product(3, "LATA BAUM PORTER 473 CC", 1870.18, 2805.27),
        Product(4, "LATA BAUM HONEY 473 CC", 1870.18, 2805.27),
        Product(5, "LATA BAUM IRON ALE 473 CC", 1870.18, 2805.27),
        Product(6, "LATA BAUM OLD ALE 473 CC", 2140.22, 3210.33),
        Product(7, "LATA BAUM GLADSTONE 473 CC", 2140.22, 3210.33),
        Product(8, "LATA BAUM FUCK IPA 473 CC", 2650.50, 3975.75),
        Product(9, "LATA BAUM APA 473 CC", 2336.22, 3504.33),
        Product(10, "LATA BAUM CALIFORNIA 473 CC", 3122.87, 4684.31),
        Product(11, "LATA BAUM ALGEBRA GIN TONIC 473 CC", 2519.08, 3778.62),
        Product(12, "LATA BAUM LEMON 473 CC", 1798.64, 2697.97),
        Product(13, "LATA BAUM LAGER DORADA 473 CC", 1233.20, 1849.80),
    )

    override suspend fun getAllProducts(): List<Product> = products
    override suspend fun getProductById(id: Long): Product? = products.find { it.id == id }
}
