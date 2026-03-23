package com.example.rickandmortycore.coreUi.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rickandmortyapp.core.ui.R
import com.example.rickandmortycore.coreUi.model.CharacterFilter
import com.example.rickandmortycore.coreUi.model.SortOption
import com.example.rickandmortycore.coreUi.utils.EMPTY_STRING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: CharacterFilter,
    isConnected: Boolean,
    showSortOptions: Boolean = false,
    onFilterChange: (CharacterFilter) -> Unit,
    onDismiss: () -> Unit
) {
    val genderOptions = listOf(
        EMPTY_STRING, stringResource(R.string.male),
        stringResource(R.string.female), stringResource(R.string.genderless),
        stringResource(R.string.unknown)
    )
    val statusOptions = listOf(
        EMPTY_STRING, stringResource(R.string.alive),
        stringResource(R.string.dead), stringResource(R.string.unknown)
    )

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.filters), style = MaterialTheme.typography.titleMedium)

            HorizontalDivider()

            Text(stringResource(R.string.show), style = MaterialTheme.typography.labelLarge)
            FilterChip(
                selected = currentFilter.showFavourites,
                onClick = {
                    if (isConnected || !currentFilter.showFavourites) {
                        onFilterChange(
                            currentFilter.copy(showFavourites = !currentFilter.showFavourites)
                        )
                    }
                },
                label = { Text(stringResource(R.string.favourites)) },
                leadingIcon = {
                    Icon(
                        imageVector = if (currentFilter.showFavourites) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.favourites),
                        tint = if (currentFilter.showFavourites) Color.Red else Color.Gray
                    )
                }
            )

            HorizontalDivider()

            Text(stringResource(R.string.status), style = MaterialTheme.typography.labelLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                statusOptions.forEach { value ->
                    FilterChip(
                        selected = currentFilter.status == value,
                        onClick = { onFilterChange(currentFilter.copy(status = value)) },
                        label = { Text(value.ifBlank { stringResource(R.string.all) }) }
                    )
                }
            }

            HorizontalDivider()

            Text(stringResource(R.string.gender), style = MaterialTheme.typography.labelLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                genderOptions.forEach { value ->
                    FilterChip(
                        selected = currentFilter.gender == value,
                        onClick = { onFilterChange(currentFilter.copy(gender = value)) },
                        label = { Text(value.ifBlank { stringResource(R.string.all) }) }
                    )
                }
            }

            if (showSortOptions) {
                HorizontalDivider()
                Text(stringResource(R.string.sort_by), style = MaterialTheme.typography.labelLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SortOption.entries.forEach { option ->
                        FilterChip(
                            selected = currentFilter.sortOption == option,
                            onClick = { onFilterChange(currentFilter.copy(sortOption = option)) },
                            label = {
                                Text(
                                    when (option) {
                                        SortOption.NAME -> stringResource(R.string.name)
                                        SortOption.STATUS -> stringResource(R.string.status)
                                        SortOption.SPECIES -> stringResource(R.string.species)
                                    }
                                )
                            }
                        )
                    }
                }
            }

            if (currentFilter.isActive) {
                HorizontalDivider()
                OutlinedButton(
                    onClick = {
                        onFilterChange(
                            CharacterFilter(showFavourites = !isConnected)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.clear_filters))
                }
            }
        }
    }
}