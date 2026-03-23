package com.example.rickandmortyapp.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.rickandmortyapp.core.UiState
import com.example.rickandmortycore.coreUi.components.CharacterListScreen
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListScreen(
    modifier: Modifier = Modifier,
    onCharacterClicked: (CharacterDisplayModel) -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(com.example.rickandmortyapp.R.string.rick_morty),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        CharacterListScreen(
            characters = (uiState as? UiState.Success)?.data ?: emptyList(),
            isLoading = uiState is UiState.Loading,
            isEmpty = uiState is UiState.Empty,
            errorMessage = (uiState as? UiState.Error)?.message,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.searchCharacter(it) },
            onCharacterClicked = onCharacterClicked,
            isLoadingMore = isLoading,
            hasMorePages = hasMorePages,
            onLoadMore = { viewModel.loadNextPage() },
            filter = filter,
            onFilterChange = { viewModel.updateFilter(it) },
            isConnected = isConnected,
            modifier = modifier.padding(innerPadding)
        )
    }
}