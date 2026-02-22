package ar.habemosbar.sales.presentation

import ar.habemosbar.sales.domain.model.CartItem
import ar.habemosbar.sales.domain.model.CustomerType
import ar.habemosbar.sales.domain.model.Product

data class SalesUiState(
    val products: List<Product> = emptyList(),
    val cartItems: Map<Long, Int> = emptyMap(),
    val customerType: CustomerType = CustomerType.COMERCIO,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val cartItemsList: List<CartItem>
        get() = cartItems
            .mapNotNull { (productId, quantity) ->
                products.find { it.id == productId }?.let { product ->
                    CartItem(product, quantity)
                }
            }

    val subtotal: Double
        get() = cartItemsList.sumOf { it.getSubtotal(customerType) }

    val total: Double
        get() = subtotal
}
