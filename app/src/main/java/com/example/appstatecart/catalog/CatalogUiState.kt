package com.example.appstatecart.catalog

import com.example.appstatecart.domain.Cart
import com.example.appstatecart.domain.Product
import com.example.appstatecart.util.PriceFormatter
import kotlin.time.Duration.Companion.milliseconds

val SearchDebounce = 300.milliseconds

sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data class Success(val items: List<CatalogItem>) : CatalogUiState
}

data class CatalogItem(
    val productId: String,
    val name: String,
    val priceLabel: String,
    val quantityInCart: Int,
)

// Shared by CatalogViewModel and FlowCatalogViewModel: only the plumbing differs.
fun catalogUiState(products: List<Product>?, cart: Cart): CatalogUiState =
    if (products == null) {
        CatalogUiState.Loading
    } else {
        CatalogUiState.Success(
            products.map { product ->
                CatalogItem(
                    productId = product.id,
                    name = product.name,
                    priceLabel = PriceFormatter.format(product.priceCents),
                    quantityInCart = cart.quantityOf(product.id),
                )
            },
        )
    }
