package ar.habemosbar.sales.presentation.viewmodel

import ar.habemosbar.sales.domain.model.CustomerType
import ar.habemosbar.sales.domain.repository.ProductRepository
import ar.habemosbar.sales.presentation.SalesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SalesViewModel(
    private val productRepository: ProductRepository
) {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    suspend fun loadProducts() {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val products = productRepository.getAllProducts()
            _uiState.update { state ->
                state.copy(
                    products = products,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun updateQuantity(productId: Long, quantity: Int) {
        _uiState.update { state ->
            if (quantity <= 0) {
                state.copy(cartItems = state.cartItems - productId)
            } else {
                state.copy(cartItems = state.cartItems + (productId to quantity))
            }
        }
    }

    fun setCustomerType(customerType: CustomerType) {
        _uiState.update { it.copy(customerType = customerType) }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyMap()) }
    }
}
