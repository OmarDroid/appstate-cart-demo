package com.example.appstatecart.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.appstatecart.R
import com.example.appstatecart.ui.QuantityStepper

@Composable
fun CatalogScreen(viewModel: CatalogViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = viewModel.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text(stringResource(R.string.search_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        // uiState is Compose State: read .value directly, no collectAsStateWithLifecycle.
        when (val state = viewModel.uiState.value) {
            CatalogUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is CatalogUiState.Success -> CatalogList(
                items = state.items,
                onAdd = viewModel::onAddToCart,
                onRemove = viewModel::onRemoveFromCart,
            )
        }
    }
}

@Composable
private fun CatalogList(
    items: List<CatalogItem>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) {
        Text(
            text = stringResource(R.string.catalog_empty),
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.padding(16.dp),
        )
        return
    }
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items, key = { it.productId }, contentType = { "product" }) { item ->
            ListItem(
                headlineContent = { Text(item.name) },
                supportingContent = { Text(item.priceLabel) },
                trailingContent = {
                    QuantityStepper(
                        quantity = item.quantityInCart,
                        onAdd = { onAdd(item.productId) },
                        onRemove = { onRemove(item.productId) },
                    )
                },
            )
        }
    }
}
