package com.example.appstatecart.cart

import com.example.appstatecart.domain.Cart
import com.example.appstatecart.domain.Product
import com.example.appstatecart.util.PriceFormatter

sealed interface CartUiState {
    data object Loading : CartUiState
    data object Empty : CartUiState
    data class Content(
        val lines: List<CartLineItem>,
        val itemCount: Int,
        val subtotalLabel: String,
        val promoStatus: PromoStatus?,
        val totalLabel: String,
    ) : CartUiState
}

data class CartLineItem(
    val productId: String,
    val name: String,
    val quantity: Int,
    val lineTotalLabel: String,
)

enum class PromoStatus { Checking, Valid, Invalid }

fun cartUiState(products: List<Product>, cart: Cart, promoStatus: PromoStatus?): CartUiState {
    val productsById = products.associateBy { it.id }
    val lines = cart.lines.mapNotNull { (productId, quantity) ->
        productsById[productId]?.let { product -> product to quantity }
    }
    val subtotalCents = lines.sumOf { (product, quantity) -> product.priceCents * quantity }
    val discountCents = if (promoStatus == PromoStatus.Valid) subtotalCents * PROMO_PERCENT_OFF / 100 else 0
    return CartUiState.Content(
        lines = lines.map { (product, quantity) ->
            CartLineItem(
                productId = product.id,
                name = product.name,
                quantity = quantity,
                lineTotalLabel = PriceFormatter.format(product.priceCents * quantity),
            )
        },
        itemCount = cart.itemCount,
        subtotalLabel = PriceFormatter.format(subtotalCents),
        promoStatus = promoStatus,
        totalLabel = PriceFormatter.format(subtotalCents - discountCents),
    )
}

private const val PROMO_PERCENT_OFF = 10
