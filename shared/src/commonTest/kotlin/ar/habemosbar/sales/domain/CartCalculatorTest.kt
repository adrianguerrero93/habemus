package ar.habemosbar.sales.domain

import ar.habemosbar.sales.domain.model.CartItem
import ar.habemosbar.sales.domain.model.CustomerType
import ar.habemosbar.sales.domain.model.Product
import ar.habemosbar.sales.domain.repository.ProductRepository
import ar.habemosbar.sales.domain.usecase.CartCalculator
import kotlin.test.Test
import kotlin.test.assertEquals

// In-memory repository for testing
class InMemoryProductRepository(
    private val products: List<Product>
) : ProductRepository {
    override suspend fun getAllProducts(): List<Product> = products
    override suspend fun getProductById(id: Long): Product? = products.find { it.id == id }
}

class CartCalculatorTest {
    private val calculator = CartCalculator()

    @Test
    fun testEmptyCart() {
        val calculation = calculator.calculateCart(emptyList(), CustomerType.CONSUMIDOR_FINAL)
        assertEquals(0.0, calculation.total)
        assertEquals(0, calculation.itemCount)
    }

    @Test
    fun testCartWithSingleItem() {
        val product = Product(1L, "LATA BAUM", 2645.07, 1763.38)
        val items = listOf(CartItem(product, 1))
        
        val calculation = calculator.calculateCart(items, CustomerType.CONSUMIDOR_FINAL)
        assertEquals(2645.07, calculation.total)
        assertEquals(1, calculation.itemCount)
    }

    @Test
    fun testCartRespectCustomerType() {
        val product = Product(1L, "LATA BAUM", 2645.07, 1763.38)
        val items = listOf(CartItem(product, 1))
        
        val calcConsumer = calculator.calculateCart(items, CustomerType.CONSUMIDOR_FINAL)
        val calcRetail = calculator.calculateCart(items, CustomerType.COMERCIO)
        
        assertEquals(2645.07, calcConsumer.total)
        assertEquals(1763.38, calcRetail.total)
    }
}
