package com.example.appstatecart

import androidx.appstate.statestore.StateStore
import com.example.appstatecart.cart.CartManager
import com.example.appstatecart.data.InMemoryProductRepository
import com.example.appstatecart.data.ProductRepository

class AppContainer {
    val stateStore = StateStore()
    val cartManager = CartManager(stateStore)
    val productRepository: ProductRepository = InMemoryProductRepository()
}
