package com.example.appstatecart.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.appstatecart.R
import com.example.appstatecart.ui.QuantityStepper

@Composable
fun CartScreen(viewModel: CartViewModel, modifier: Modifier = Modifier) {
    when (val state = viewModel.uiState.value) {
        CartUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        CartUiState.Empty -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.cart_empty), style = MaterialTheme.typography.bodyLarge)
        }
        is CartUiState.Content -> CartContent(
            state = state,
            promoCode = viewModel.promoCode,
            onPromoCodeChange = viewModel::onPromoCodeChange,
            onAdd = viewModel::onAdd,
            onRemove = viewModel::onRemove,
            onClear = viewModel::onClear,
            modifier = modifier,
        )
    }
}

@Composable
private fun CartContent(
    state: CartUiState.Content,
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(state.lines, key = { it.productId }, contentType = { "line" }) { line ->
            ListItem(
                headlineContent = { Text(line.name) },
                supportingContent = { Text(line.lineTotalLabel) },
                trailingContent = {
                    QuantityStepper(
                        quantity = line.quantity,
                        onAdd = { onAdd(line.productId) },
                        onRemove = { onRemove(line.productId) },
                    )
                },
            )
        }
        item(key = "summary", contentType = "summary") {
            CartSummary(state, promoCode, onPromoCodeChange, onClear)
        }
    }
}

@Composable
private fun CartSummary(
    state: CartUiState.Content,
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalDivider()
        OutlinedTextField(
            value = promoCode,
            onValueChange = onPromoCodeChange,
            label = { Text(stringResource(R.string.promo_label)) },
            supportingText = state.promoStatus?.let { status -> { Text(promoMessage(status)) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        SummaryRow(pluralStringResource(R.plurals.subtotal_items, state.itemCount, state.itemCount), state.subtotalLabel)
        SummaryRow(stringResource(R.string.total), state.totalLabel)
        OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.clear_cart))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun promoMessage(status: PromoStatus): String = stringResource(
    when (status) {
        PromoStatus.Checking -> R.string.promo_checking
        PromoStatus.Valid -> R.string.promo_valid
        PromoStatus.Invalid -> R.string.promo_invalid
    },
)
