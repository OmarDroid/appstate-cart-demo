package com.example.appstatecart.data

import com.example.appstatecart.domain.Category
import com.example.appstatecart.domain.Product
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InMemoryProductRepository : ProductRepository {

    override fun products(query: String): Flow<List<Product>> = flow {
        delay(250.milliseconds) // pretend this is a network call
        emit(catalogue.filter { it.matches(query) })
    }

    private fun Product.matches(query: String): Boolean =
        name.contains(query, ignoreCase = true) ||
            category.name.contains(query, ignoreCase = true)

    private val catalogue = listOf(
        Product("apple", "Apple", 120, Category.FRUIT),
        Product("banana", "Banana", 90, Category.FRUIT),
        Product("mango", "Mango", 250, Category.FRUIT),
        Product("baguette", "Baguette", 310, Category.BAKERY),
        Product("croissant", "Croissant", 195, Category.BAKERY),
        Product("sourdough", "Sourdough loaf", 520, Category.BAKERY),
        Product("milk", "Whole milk", 149, Category.DAIRY),
        Product("yogurt", "Greek yogurt", 299, Category.DAIRY),
        Product("cold-brew", "Cold brew", 375, Category.DRINKS),
        Product("lemonade", "Lemonade", 260, Category.DRINKS),
    )
}
