package ar.habemosbar.sales.domain.model

data class Product(
    val id: Long,
    val name: String,
    val priceConsumerFinal: Double,
    val priceRetail: Double
) {
    fun getPrice(customerType: CustomerType): Double = when (customerType) {
        CustomerType.CONSUMIDOR_FINAL -> priceConsumerFinal
        CustomerType.COMERCIO -> priceRetail
    }
}
