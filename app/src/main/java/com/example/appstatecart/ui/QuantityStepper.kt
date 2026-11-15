package com.example.appstatecart.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.appstatecart.R

@Composable
fun QuantityStepper(
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (quantity == 0) {
        FilledTonalButton(onClick = onAdd, modifier = modifier) {
            Text(stringResource(R.string.add))
        }
        return
    }
    val decrease = stringResource(R.string.decrease)
    val increase = stringResource(R.string.increase)
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onRemove, modifier = Modifier.semantics { contentDescription = decrease }) {
            Text("−", style = MaterialTheme.typography.titleLarge)
        }
        Text("$quantity", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onAdd, modifier = Modifier.semantics { contentDescription = increase }) {
            Text("+", style = MaterialTheme.typography.titleLarge)
        }
    }
}
