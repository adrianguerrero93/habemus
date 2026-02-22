package ar.habemosbar.sales.domain

import ar.habemosbar.sales.domain.model.CartItem
import ar.habemosbar.sales.domain.model.CustomerType
import ar.habemosbar.sales.domain.model.Product
import ar.habemosbar.sales.domain.model.SalesCalculation
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

class CartItemTest {

    @Test
    fun testGetSubtotalConsumerFinal() {
        val product = Product(1L, "LATA BAUM", 2645.07, 1763.38)
        val cartItem = CartItem(product, 2)

        val subtotal = cartItem.getSubtotal(CustomerType.CONSUMIDOR_FINAL)
        assertEquals(5290.14, subtotal)
    }

    @Test
    fun testGetSubtotalRetail() {
        val product = Product(1L, "LATA BAUM", 2645.07, 1763.38)
        val cartItem = CartItem(product, 3)

        val subtotal = cartItem.getSubtotal(CustomerType.COMERCIO)
        assertEquals(5290.14, subtotal)
    }

    @Test
    fun testInvalidQuantityThrows() {
        val product = Product(1L, "LATA BAUM", 2645.07, 1763.38)
        
        try {
            CartItem(product, -1)
            throw AssertionError("Should have thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Quantity must be non-negative", e.message)
        }
    }
}

class SalesCalculationTest {

    @Test
    fun testEmptyCartTotal() {
        val calculation = SalesCalculation(emptyList(), CustomerType.CONSUMIDOR_FINAL)
        assertEquals(0.0, calculation.total)
        assertEquals(0.0, calculation.subtotal)
        assertEquals(0, calculation.itemCount)
    }

    @Test
    fun testSingleItemCalculation() {
        val product = Product(1L, "LATA BAUM BLONDE", 2645.07, 1763.38)
        val cartItem = CartItem(product, 2)
        val calculation = SalesCalculation(listOf(cartItem), CustomerType.CONSUMIDOR_FINAL)

        assertEquals(5290.14, calculation.total)
        assertEquals(2, calculation.itemCount)
    }

    @Test
    fun testMultipleItemsConsumerFinal() {
        val product1 = Product(1L, "LATA BAUM BLONDE", 2645.07, 1763.38)
        val product2 = Product(2L, "LATA BAUM SCOTTISH", 2697.97, 1798.64)
        
        val items = listOf(
            CartItem(product1, 2),
            CartItem(product2, 1)
        )
        val calculation = SalesCalculation(items, CustomerType.CONSUMIDOR_FINAL)

        val expected = (2645.07 * 2) + 2697.97
        assertEquals(expected, calculation.total)
        assertEquals(3, calculation.itemCount)
    }

    @Test
    fun testMultipleItemsRetail() {
        val product1 = Product(1L, "LATA BAUM BLONDE", 2645.07, 1763.38)
        val product2 = Product(2L, "LATA BAUM SCOTTISH", 2697.97, 1798.64)
        
        val items = listOf(
            CartItem(product1, 2),
            CartItem(product2, 1)
        )
        val calculation = SalesCalculation(items, CustomerType.COMERCIO)

        val expected = (1763.38 * 2) + 1798.64
        assertEquals(expected, calculation.total)
        assertEquals(3, calculation.itemCount)
    }
}
