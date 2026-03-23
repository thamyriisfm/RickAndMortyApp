package com.example.rickandmortycore.coreUi.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rickandmortyapp.core.ui.R
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel
import com.example.rickandmortycore.coreUi.model.CharacterFilter
import com.example.rickandmortycore.coreUi.utils.EMPTY_STRING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    modifier: Modifier = Modifier,
    characters: List<CharacterDisplayModel> = emptyList(),
    isLoading: Boolean,
    isEmpty: Boolean,
    errorMessage: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCharacterClicked: (CharacterDisplayModel) -> Unit,
    isLoadingMore: Boolean = false,
    hasMorePages: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    filter: CharacterFilter? = null,
    onFilterChange: ((CharacterFilter) -> Unit)? = null,
    isConnected: Boolean = true,
    showSortOptions: Boolean = false
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val shouldShowSortOptions = showSortOptions || filter?.showFavourites == true

    if (onLoadMore != null) {
        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val total = listState.layoutInfo.totalItemsCount
                lastVisible >= total - 3
            }
        }
        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) onLoadMore()
        }
    }

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {

            if (!isConnected) NoInternetBanner()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange(EMPTY_STRING) }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                if (filter != null && onFilterChange != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    BadgedBox(
                        badge = { if (filter.isActive) Badge() }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.filters),
                                tint = if (filter.isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                isEmpty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!isConnected) stringResource(R.string.no_saved_favourites)
                            else stringResource(R.string.no_characters_found),
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { onSearchQueryChange(searchQuery) }) {
                                Text(stringResource(R.string.try_again))
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(state = listState) {
                        items(characters) { character ->
                            ListItem(
                                character = character,
                                onCharacterClicked = onCharacterClicked
                            )
                        }
                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        if (!hasMorePages && !isLoadingMore && filter?.showFavourites == false) {
                            item {
                                Text(
                                    text = stringResource(R.string.all_characters_loaded),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet && filter != null && onFilterChange != null) {
        FilterBottomSheet(
            currentFilter = filter,
            isConnected = isConnected,
            showSortOptions = shouldShowSortOptions,
            onFilterChange = { onFilterChange(it) },
            onDismiss = { showFilterSheet = false }
        )
    }
}