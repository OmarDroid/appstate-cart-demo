package com.example.appstatecart.cart

import androidx.appstate.transform.transform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appstatecart.data.ProductRepository
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

class CartViewModel(
    private val repository: ProductRepository,
    private val cartManager: CartManager,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel(coroutineScope) {

    var promoCode by mutableStateOf("")
        private set

    val uiState: State<CartUiState> =
        transform<CartUiState>(CartUiState.Loading, viewModelScope, dispatcher) {
            val cart = cartManager.cart.value
            if (cart.lines.isEmpty()) return@transform CartUiState.Empty

            val products = remember { repository.products(query = "") }
                .collectAsState(initial = null).value
                ?: return@transform CartUiState.Loading

            cartUiState(products, cart, promoStatus(promoCode))
        }

    fun onPromoCodeChange(code: String) {
        promoCode = code
    }

    fun onAdd(productId: String) = cartManager.add(productId)

    fun onRemove(productId: String) = cartManager.remove(productId)

    fun onClear() = cartManager.clear()
}

// A plain @Composable helper: validates the code only after the user stops typing.
@Composable
private fun promoStatus(code: String): PromoStatus? {
    if (code.isBlank()) return null
    return produceState(initialValue = PromoStatus.Checking, code) {
        value = PromoStatus.Checking
        delay(PromoDebounce)
        value = if (code.equals(VALID_PROMO_CODE, ignoreCase = true)) PromoStatus.Valid else PromoStatus.Invalid
    }.value
}

val PromoDebounce = 300.milliseconds
const val VALID_PROMO_CODE = "SAVE10"
