package com.example.appstatecart.data

import com.example.appstatecart.domain.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun products(query: String): Flow<List<Product>>
}
