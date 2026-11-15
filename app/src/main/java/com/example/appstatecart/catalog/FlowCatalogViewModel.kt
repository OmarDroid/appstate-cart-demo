package com.example.appstatecart.catalog

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appstatecart.cart.CartManager
import com.example.appstatecart.data.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FlowCatalogViewModel(
    private val repository: ProductRepository,
    private val cartManager: CartManager,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val uiState: StateFlow<CatalogUiState> =
        combine(
            _query.debounce(SearchDebounce)
                .distinctUntilChanged()
                .flatMapLatest { repository.products(it) },
            snapshotFlow { cartManager.cart.value },
        ) { products, cart -> catalogUiState(products, cart) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState.Loading)

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onAddToCart(productId: String) = cartManager.add(productId)

    fun onRemoveFromCart(productId: String) = cartManager.remove(productId)
}
