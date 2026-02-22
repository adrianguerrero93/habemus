package ar.habemosbar.sales.domain.usecase

import ar.habemosbar.sales.domain.model.CartItem
import ar.habemosbar.sales.domain.model.CustomerType
import ar.habemosbar.sales.domain.model.SalesCalculation

class CartCalculator {
    fun calculateCart(items: List<CartItem>, customerType: CustomerType): SalesCalculation =
        SalesCalculation(items, customerType)
}
