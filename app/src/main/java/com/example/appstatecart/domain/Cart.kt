package com.example.appstatecart.domain

import kotlinx.serialization.Serializable

// @Serializable only because StateStore persists it to DataStore as JSON.
@Serializable
data class Cart(val lines: Map<String, Int> = emptyMap()) {

    val itemCount: Int get() = lines.values.sum()

    fun quantityOf(productId: String): Int = lines[productId] ?: 0

    fun add(productId: String): Cart =
        copy(lines = lines + (productId to quantityOf(productId) + 1))

    fun remove(productId: String): Cart {
        val quantity = quantityOf(productId)
        return if (quantity <= 1) copy(lines = lines - productId)
        else copy(lines = lines + (productId to quantity - 1))
    }
}
