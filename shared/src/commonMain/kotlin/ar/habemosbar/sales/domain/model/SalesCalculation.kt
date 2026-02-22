package ar.habemosbar.sales.domain.model

data class SalesCalculation(
    val items: List<CartItem>,
    val customerType: CustomerType
) {
    val subtotal: Double
        get() = items.sumOf { it.getSubtotal(customerType) }

    val total: Double
        get() = subtotal

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}
