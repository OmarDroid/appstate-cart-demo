package com.example.appstatecart

import androidx.appstate.statestore.StateStore
import androidx.appstate.transform.testing.transformTest
import com.example.appstatecart.cart.CartManager
import com.example.appstatecart.cart.CartUiState
import com.example.appstatecart.cart.CartViewModel
import com.example.appstatecart.catalog.CatalogViewModel
import com.example.appstatecart.catalog.quantities
import com.example.appstatecart.fakes.FakeProductRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SharedCartTest {

    @Test
    fun `catalog and cart view models see the same cart`() = transformTest {
        val stateStore = StateStore()
        val repository = FakeProductRepository()
        // Two separate CartManagers: the StateStore, not the manager, is the source of truth.
        val catalog = CatalogViewModel(repository, CartManager(stateStore), backgroundScope, testDispatcher)
        val cart = CartViewModel(repository, CartManager(stateStore), backgroundScope, testDispatcher)
        advance()

        catalog.onAddToCart("banana")
        advance()

        assertThat(catalog.uiState.value.quantities["banana"]).isEqualTo(1)
        val lines = (cart.uiState.value as CartUiState.Content).lines
        assertThat(lines.map { it.productId to it.quantity }).containsExactly("banana" to 1)

        cart.onRemove("banana")
        advance()

        assertThat(catalog.uiState.value.quantities["banana"]).isEqualTo(0)
        assertThat(cart.uiState.value).isEqualTo(CartUiState.Empty)
    }
}
