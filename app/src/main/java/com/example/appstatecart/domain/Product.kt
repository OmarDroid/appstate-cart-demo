package com.example.appstatecart.domain

data class Product(
    val id: String,
    val name: String,
    val priceCents: Long,
    val category: Category,
)

enum class Category { FRUIT, BAKERY, DAIRY, DRINKS }
