package ar.habemosbar.sales.domain.model

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    init {
        require(quantity >= 0) { "Quantity must be non-negative" }
    }

    fun getSubtotal(customerType: CustomerType): Double =
        product.getPrice(customerType) * quantity
}
