package com.example.appstatecart.catalog

import androidx.appstate.statestore.StateStore
import androidx.compose.runtime.snapshots.Snapshot
import app.cash.turbine.test
import com.example.appstatecart.MainDispatcherRule
import com.example.appstatecart.cart.CartManager
import com.example.appstatecart.fakes.FakeProductRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class FlowCatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeProductRepository()
    private val cartManager = CartManager(StateStore())

    @Test
    fun `shows loading, then the products`() = runTest(mainDispatcherRule.testDispatcher.scheduler) {
        val viewModel = FlowCatalogViewModel(repository, cartManager)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(CatalogUiState.Loading)
            assertThat(awaitItem().quantities)
                .containsExactly("apple", 0, "banana", 0, "baguette", 0).inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `query change switches the results after the debounce`() = runTest(mainDispatcherRule.testDispatcher.scheduler) {
        val viewModel = FlowCatalogViewModel(repository, cartManager)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            viewModel.onQueryChange("ba") // runTest skips the debounce in virtual time

            assertThat(awaitItem().quantities.keys).containsExactly("banana", "baguette").inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cart changes made through CartManager show up in the catalog`() = runTest(mainDispatcherRule.testDispatcher.scheduler) {
        val viewModel = FlowCatalogViewModel(repository, cartManager)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            cartManager.add("banana")
            cartManager.add("banana")
            Snapshot.sendApplyNotifications() // snapshotFlow only hears about applied writes

            assertThat(awaitItem().quantities)
                .containsExactly("apple", 0, "banana", 2, "baguette", 0).inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
