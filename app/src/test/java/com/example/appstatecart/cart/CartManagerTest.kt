package com.example.appstatecart.cart

import androidx.appstate.statestore.StateStore
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CartManagerTest {

    private val cartManager = CartManager(StateStore())

    @Test
    fun `add increments the quantity of each product`() {
        cartManager.add("apple")
        cartManager.add("apple")
        cartManager.add("banana")

        assertThat(cartManager.cart.value.lines).containsExactly("apple", 2, "banana", 1)
    }

    @Test
    fun `remove decrements and drops the line at zero`() {
        cartManager.add("apple")
        cartManager.add("apple")
        cartManager.add("banana")

        cartManager.remove("apple")
        cartManager.remove("banana")

        assertThat(cartManager.cart.value.lines).containsExactly("apple", 1)
    }

    @Test
    fun `clear empties the cart`() {
        cartManager.add("apple")

        cartManager.clear()

        assertThat(cartManager.cart.value.lines).isEmpty()
    }

    @Test
    fun `managers built on the same StateStore share one cart`() {
        val stateStore = StateStore()
        val first = CartManager(stateStore)
        val second = CartManager(stateStore)

        first.add("apple")

        assertThat(second.cart.value.lines).containsExactly("apple", 1)
    }
}
