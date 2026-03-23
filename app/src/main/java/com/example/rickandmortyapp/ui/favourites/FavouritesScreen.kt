package com.example.rickandmortyapp.ui.favourites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.rickandmortyapp.core.UiState
import com.example.rickandmortycore.coreUi.components.CharacterListScreen
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel

@Composable
fun FavouritesScreen(
    onCharacterClicked: (CharacterDisplayModel) -> Unit,
    viewModel: FavouritesViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filter by viewModel.filter.collectAsState()


    CharacterListScreen(
        modifier = modifier,
        characters = (uiState as? UiState.Success)?.data ?: emptyList(),
        isLoading = uiState is UiState.Loading,
        isEmpty = uiState is UiState.Empty,
        errorMessage = (uiState as? UiState.Error)?.message,
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.searchCharacter(it) },
        onCharacterClicked = onCharacterClicked,
        filter = filter,
        onFilterChange = { viewModel.updateFilter(it) },
        showSortOptions = true
    )
}