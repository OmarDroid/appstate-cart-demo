package com.example.appstatecart.fakes

import com.example.appstatecart.data.ProductRepository
import com.example.appstatecart.domain.Category
import com.example.appstatecart.domain.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

val Apple = Product("apple", "Apple", 120, Category.FRUIT)
val Banana = Product("banana", "Banana", 90, Category.FRUIT)
val Baguette = Product("baguette", "Baguette", 310, Category.BAKERY)

class FakeProductRepository : ProductRepository {

    val catalogue = MutableStateFlow(listOf(Apple, Banana, Baguette))

    override fun products(query: String): Flow<List<Product>> =
        catalogue.map { products -> products.filter { it.name.contains(query, ignoreCase = true) } }
}
