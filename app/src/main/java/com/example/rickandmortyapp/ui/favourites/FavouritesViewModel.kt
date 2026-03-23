package com.example.rickandmortyapp.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.repository.FavouriteRepositoryInterface
import com.example.rickandmortyapp.extensions.toDisplayModel
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel
import com.example.rickandmortycore.coreUi.model.CharacterFilter
import com.example.rickandmortycore.coreUi.model.SortOption
import com.example.rickandmortyapp.core.UiState
import com.example.rickandmortyapp.utils.EMPTY_STRING
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val repository: FavouriteRepositoryInterface,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5000)
) : ViewModel() {

    private val _searchQuery = MutableStateFlow(EMPTY_STRING)
    val searchQuery = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(CharacterFilter())
    val filter = _filter.asStateFlow()

    val uiState: StateFlow<UiState<List<CharacterDisplayModel>>> = combine(
        repository.getAll(),
        _searchQuery,
        _filter
    ) { list, query, filter ->
        val filtered = list
            .filter { character ->
                val matchesQuery = query.isBlank() ||
                        character.name?.contains(query, ignoreCase = true) == true
                val matchesGender = filter.gender.isBlank() ||
                        character.gender?.equals(filter.gender, ignoreCase = true) == true
                val matchesStatus = filter.status.isBlank() ||
                        character.status?.equals(filter.status, ignoreCase = true) == true
                matchesQuery && matchesGender && matchesStatus
            }
            .sortedBy { character -> sortKey(character, filter.sortOption) }

        when {
            filtered.isEmpty() -> UiState.Empty
            else -> UiState.Success(
                filtered.map { it.toDisplayModel() }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = sharingStarted,
        initialValue = UiState.Loading
    )

    fun searchCharacter(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(newFilter: CharacterFilter) {
        _filter.value = newFilter
    }

    fun removeFavourite(character: CharacterRaM) {
        viewModelScope.launch { repository.remove(character) }
    }

    private fun sortKey(character: CharacterRaM, sort: SortOption): String =
        when (sort) {
            SortOption.NAME -> character.name ?: EMPTY_STRING
            SortOption.STATUS -> character.status ?: EMPTY_STRING
            SortOption.SPECIES -> character.species ?: EMPTY_STRING
        }
}