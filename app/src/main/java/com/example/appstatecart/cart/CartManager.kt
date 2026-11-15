package com.example.appstatecart.cart

import androidx.appstate.datastore.PersistToDataStore
import androidx.appstate.statestore.StateStore
import androidx.appstate.statestore.StateStoreKey
import androidx.compose.runtime.State
import com.example.appstatecart.domain.Cart
import kotlinx.serialization.Serializable

@Serializable
private data object CartKey : StateStoreKey<Cart>(defaultValue = Cart()), PersistToDataStore

class CartManager(private val stateStore: StateStore) {

    val cart: State<Cart> = stateStore.getState(CartKey)

    fun add(productId: String) = stateStore.updateState(CartKey) { it.add(productId) }

    fun remove(productId: String) = stateStore.updateState(CartKey) { it.remove(productId) }

    fun clear() = stateStore.setState(CartKey, Cart())
}
