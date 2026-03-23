package com.example.rickandmortyapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.core.UiState
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.network.ConnectivityObserver
import com.example.rickandmortyapp.data.repository.FavouriteRepositoryInterface
import com.example.rickandmortyapp.data.repository.RaMRepositoryInterface
import com.example.rickandmortyapp.extensions.toDisplayModel
import com.example.rickandmortyapp.utils.CHARACTER_NOT_FOUND
import com.example.rickandmortyapp.utils.EMPTY_STRING
import com.example.rickandmortyapp.utils.ERROR
import com.example.rickandmortyapp.utils.UNKNOWN_ERROR
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel
import com.example.rickandmortycore.coreUi.model.CharacterFilter
import com.example.rickandmortycore.coreUi.model.SortOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RaMRepositoryInterface,
    private val favouriteRepository: FavouriteRepositoryInterface,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<CharacterDisplayModel>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _allCharacters = MutableStateFlow<List<CharacterRaM>>(emptyList())
    private val _allFavourites = MutableStateFlow<List<CharacterRaM>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages = _hasMorePages.asStateFlow()

    private val _searchQuery = MutableStateFlow(EMPTY_STRING)
    val searchQuery = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(CharacterFilter())
    val filter = _filter.asStateFlow()

    val isConnected: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private var currentPage = 1
    private var totalPages = 1
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            isConnected.collect { connected ->
                if (!connected) {
                    _filter.value = _filter.value.copy(showFavourites = true)
                }
            }
        }
        loadFromCurrentFilter()
    }

    private fun loadFromCurrentFilter() {
        if (_filter.value.showFavourites) {
            loadFavourites()
        } else {
            resetList()
            getList()
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            favouriteRepository.getAll().collect { favourites ->
                _allFavourites.value = favourites
                val filtered = applyLocalFilters(favourites)
                _uiState.value = when {
                    filtered.isEmpty() -> UiState.Empty
                    else -> UiState.Success(
                        filtered.map { it.toDisplayModel() }
                    )
                }
            }
        }
    }

    fun getList(page: Int = 1) {
        if (_isLoading.value || !_hasMorePages.value) return
        viewModelScope.launch {
            _isLoading.value = true
            if (page == 1) _uiState.value = UiState.Loading
            try {
                val response = repository.getCharacters(
                    page = page,
                    name = _searchQuery.value,
                    gender = _filter.value.gender,
                    status = _filter.value.status
                )
                totalPages = response.info?.pages ?: 1
                val newCharacters = response.results ?: emptyList()
                _allCharacters.value = if (page == 1) newCharacters
                else _allCharacters.value + newCharacters
                currentPage = page
                _hasMorePages.value = currentPage < totalPages
                _uiState.value = when {
                    _allCharacters.value.isEmpty() -> UiState.Empty
                    else -> UiState.Success(
                        _allCharacters.value.map { it.toDisplayModel() })
                }
            } catch (e: Exception) {
                Log.e(ERROR, e.toString())
                if (_searchQuery.value.isBlank()) {
                    _uiState.value = UiState.Error(UNKNOWN_ERROR)
                } else {
                    _uiState.value = UiState.Error(CHARACTER_NOT_FOUND)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        if (!_isLoading.value && _hasMorePages.value && !_filter.value.showFavourites) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val response = repository.getCharacters(
                        page = currentPage + 1,
                        name = _searchQuery.value,
                        gender = _filter.value.gender,
                        status = _filter.value.status
                    )
                    currentPage++
                    _allCharacters.value += (response.results ?: emptyList())
                    _hasMorePages.value = currentPage < totalPages
                    _uiState.value = UiState.Success(
                        _allCharacters.value.map { it.toDisplayModel() }
                    )
                } catch (e: Exception) {
                    Log.e("LOAD_MORE", e.toString())
                    if (_allCharacters.value.isNotEmpty()) {
                        _uiState.value = UiState.Success(
                            _allCharacters.value.map { it.toDisplayModel() }
                        )
                    } else {
                        _uiState.value = UiState.Error(e.message ?: "Error loading more")
                    }
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun searchCharacter(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(600)
            if (_filter.value.showFavourites) {
                favouriteRepository.getAll().collect { favourites ->
                    val filtered = applyLocalFilters(favourites)
                    _uiState.value = when {
                        filtered.isEmpty() -> UiState.Empty
                        else -> UiState.Success(
                            filtered.map { it.toDisplayModel() }
                        )
                    }
                }
            } else {
                resetList()
                getList()
            }
        }
    }

    fun updateFilter(newFilter: CharacterFilter) {
        val safeFilter = if (!isConnected.value && !newFilter.showFavourites) {
            newFilter.copy(showFavourites = true)
        } else {
            newFilter
        }
        _filter.value = safeFilter
        if (safeFilter.showFavourites) {
            loadFavourites()
        } else {
            loadFromCurrentFilter()
        }
    }

    private fun applyLocalFilters(list: List<CharacterRaM>): List<CharacterRaM> {
        return list
            .filter { character ->
                val matchesQuery = _searchQuery.value.isBlank() ||
                        character.name?.contains(_searchQuery.value, ignoreCase = true) == true
                val matchesGender = _filter.value.gender.isBlank() ||
                        character.gender?.equals(_filter.value.gender, ignoreCase = true) == true
                val matchesStatus = _filter.value.status.isBlank() ||
                        character.status?.equals(_filter.value.status, ignoreCase = true) == true
                matchesQuery && matchesGender && matchesStatus
            }
            .sortedBy { character -> sortKey(character, _filter.value.sortOption) }
    }

    private fun sortKey(character: CharacterRaM, sort: SortOption): String =
        when (sort) {
            SortOption.NAME -> character.name ?: EMPTY_STRING
            SortOption.STATUS -> character.status ?: EMPTY_STRING
            SortOption.SPECIES -> character.species ?: EMPTY_STRING
        }

    private fun resetList() {
        _allCharacters.value = emptyList()
        currentPage = 1
        totalPages = 1
        _hasMorePages.value = true
        _isLoading.value = false
    }
}