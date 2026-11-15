package com.example.appstatecart.catalog

import androidx.appstate.statestore.StateStore
import androidx.appstate.transform.testing.TransformTestScope
import androidx.appstate.transform.testing.transformTest
import com.example.appstatecart.cart.CartManager
import com.example.appstatecart.fakes.FakeProductRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CatalogViewModelTest {

    private val repository = FakeProductRepository()
    private val cartManager = CartManager(StateStore())

    private fun TransformTestScope.createViewModel() = CatalogViewModel(
        repository = repository,
        cartManager = cartManager,
        coroutineScope = backgroundScope,
        dispatcher = testDispatcher,
    )

    @Test
    fun `shows loading, then the products`() = transformTest {
        val viewModel = createViewModel()
        assertThat(viewModel.uiState.value).isEqualTo(CatalogUiState.Loading)

        advance() // collectAsState starts collecting; the block recomposes with the products

        assertThat(viewModel.uiState.value.quantities)
            .containsExactly("apple", 0, "banana", 0, "baguette", 0).inOrder()
    }

    @Test
    fun `query change switches the results after the debounce`() = transformTest {
        val viewModel = createViewModel()
        advance()

        viewModel.onQueryChange("ba")
        advance() // recompose: the debounce LaunchedEffect restarts with "ba"
        advanceTimeBy(SearchDebounce)
        advance() // debouncedQuery = "ba": a new flow is remembered, old results still shown
        assertThat(viewModel.uiState.value.quantities.keys).containsExactly("apple", "banana", "baguette")

        advance() // the new flow's first value is applied
        assertThat(viewModel.uiState.value.quantities.keys).containsExactly("banana", "baguette").inOrder()
    }

    @Test
    fun `cart changes made through CartManager show up in the catalog`() = transformTest {
        val viewModel = createViewModel()
        advance()

        cartManager.add("banana")
        cartManager.add("banana")
        advance()

        assertThat(viewModel.uiState.value.quantities)
            .containsExactly("apple", 0, "banana", 2, "baguette", 0).inOrder()
    }

    @Test
    fun `adding from the catalog writes to the shared cart`() = transformTest {
        val viewModel = createViewModel()
        advance()

        viewModel.onAddToCart("apple")
        advance()

        assertThat(cartManager.cart.value.lines).containsExactly("apple", 1)
        assertThat(viewModel.uiState.value.quantities["apple"]).isEqualTo(1)
    }
}
