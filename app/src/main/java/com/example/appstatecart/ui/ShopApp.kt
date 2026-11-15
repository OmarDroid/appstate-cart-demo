package com.example.appstatecart.ui

import androidx.activity.compose.BackHandler
import androidx.appstate.transform.transform
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstatecart.AppContainer
import com.example.appstatecart.R
import com.example.appstatecart.cart.CartScreen
import com.example.appstatecart.cart.CartViewModel
import com.example.appstatecart.catalog.CatalogScreen
import com.example.appstatecart.catalog.CatalogViewModel

@Composable
fun ShopApp(container: AppContainer, modifier: Modifier = Modifier) {
    var showCart by rememberSaveable { mutableStateOf(false) }

    // The @Composable overload: no scope to pass, it lives as long as ShopApp is composed.
    val cartCount by transform(initialValue = 0) {
        container.cartManager.cart.value.itemCount
    }

    BackHandler(enabled = showCart) { showCart = false }

    Scaffold(
        modifier = modifier,
        topBar = {
            ShopTopBar(
                showingCart = showCart,
                cartCount = cartCount,
                onCartClick = { showCart = true },
                onBackClick = { showCart = false },
            )
        },
    ) { padding ->
        if (showCart) {
            CartScreen(
                viewModel = viewModel { CartViewModel(container.productRepository, container.cartManager) },
                modifier = Modifier.padding(padding),
            )
        } else {
            CatalogScreen(
                viewModel = viewModel { CatalogViewModel(container.productRepository, container.cartManager) },
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopTopBar(
    showingCart: Boolean,
    cartCount: Int,
    onCartClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(stringResource(if (showingCart) R.string.cart_title else R.string.app_name)) },
        navigationIcon = {
            if (showingCart) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        },
        actions = {
            if (!showingCart) {
                IconButton(onClick = onCartClick) {
                    BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = stringResource(R.string.open_cart))
                    }
                }
            }
        },
        modifier = modifier,
    )
}
