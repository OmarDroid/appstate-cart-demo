package com.example.appstatecart.cart

import androidx.appstate.statestore.StateStore
import androidx.appstate.transform.testing.TransformTestScope
import androidx.appstate.transform.testing.transformTest
import com.example.appstatecart.fakes.FakeProductRepository
import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.milliseconds
import org.junit.Test

class CartViewModelTest {

    private val cartManager = CartManager(StateStore())

    private fun TransformTestScope.createViewModel() = CartViewModel(
        repository = FakeProductRepository(),
        cartManager = cartManager,
        coroutineScope = backgroundScope,
        dispatcher = testDispatcher,
    )

    private val CartUiState.content get() = this as CartUiState.Content

    @Test
    fun `empty cart shows the empty state`() = transformTest {
        val viewModel = createViewModel()

        // No advance(): the first composition runs synchronously inside transform().
        assertThat(viewModel.uiState.value).isEqualTo(CartUiState.Empty)
    }

    @Test
    fun `lines, item count and subtotal follow the cart`() = transformTest {
        cartManager.add("apple")
        cartManager.add("apple")
        cartManager.add("baguette")
        val viewModel = createViewModel()
        assertThat(viewModel.uiState.value).isEqualTo(CartUiState.Loading)

        advance()

        val content = viewModel.uiState.value.content
        assertThat(content.lines.map { it.productId to it.quantity })
            .containsExactly("apple" to 2, "baguette" to 1).inOrder()
        assertThat(content.itemCount).isEqualTo(3)
        assertThat(content.subtotalLabel).isEqualTo("$5.50")
        assertThat(content.totalLabel).isEqualTo("$5.50")
    }

    @Test
    fun `clearing the cart switches to the empty state`() = transformTest {
        cartManager.add("apple")
        val viewModel = createViewModel()
        advance()

        viewModel.onClear()
        advance()

        assertThat(viewModel.uiState.value).isEqualTo(CartUiState.Empty)
    }

    @Test
    fun `promo code is validated only after the debounce`() = transformTest {
        cartManager.add("baguette")
        val viewModel = createViewModel()
        advance()

        viewModel.onPromoCodeChange("save10")
        advance()
        assertThat(viewModel.uiState.value.content.promoStatus).isEqualTo(PromoStatus.Checking)

        advanceTimeBy(PromoDebounce - 1.milliseconds)
        advance()
        assertThat(viewModel.uiState.value.content.promoStatus).isEqualTo(PromoStatus.Checking)

        advanceTimeBy(1.milliseconds)
        advance()
        val content = viewModel.uiState.value.content
        assertThat(content.promoStatus).isEqualTo(PromoStatus.Valid)
        assertThat(content.subtotalLabel).isEqualTo("$3.10")
        assertThat(content.totalLabel).isEqualTo("$2.79")
    }

    @Test
    fun `unknown promo code leaves the total unchanged`() = transformTest {
        cartManager.add("baguette")
        val viewModel = createViewModel()
        advance()

        viewModel.onPromoCodeChange("FREE")
        advance()
        advanceTimeBy(PromoDebounce)
        advance()

        val content = viewModel.uiState.value.content
        assertThat(content.promoStatus).isEqualTo(PromoStatus.Invalid)
        assertThat(content.totalLabel).isEqualTo("$3.10")
    }
}
