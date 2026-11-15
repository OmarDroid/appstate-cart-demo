package com.example.appstatecart.catalog

import androidx.appstate.transform.transform
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appstatecart.cart.CartManager
import com.example.appstatecart.data.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

class CatalogViewModel(
    private val repository: ProductRepository,
    private val cartManager: CartManager,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel(coroutineScope) {

    var query by mutableStateOf("")
        private set

    val uiState: State<CatalogUiState> =
        transform<CatalogUiState>(CatalogUiState.Loading, viewModelScope, dispatcher) {
            val latestQuery = query
            var debouncedQuery by remember { mutableStateOf(latestQuery) }
            LaunchedEffect(latestQuery) {
                delay(SearchDebounce)
                debouncedQuery = latestQuery
            }
            val products by remember(debouncedQuery) { repository.products(debouncedQuery) }
                .collectAsState(initial = null)
            catalogUiState(products, cartManager.cart.value)
        }

    fun onQueryChange(newQuery: String) {
        query = newQuery
    }

    fun onAddToCart(productId: String) = cartManager.add(productId)

    fun onRemoveFromCart(productId: String) = cartManager.remove(productId)
}
