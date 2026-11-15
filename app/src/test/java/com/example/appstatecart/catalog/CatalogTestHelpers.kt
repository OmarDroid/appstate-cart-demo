package com.example.appstatecart.catalog

/** productId -> quantityInCart, in display order. Fails loudly if the state is still Loading. */
val CatalogUiState.quantities: Map<String, Int>
    get() = (this as CatalogUiState.Success).items.associate { it.productId to it.quantityInCart }
