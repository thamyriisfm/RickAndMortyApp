package com.example.rickandmortyapp.ui.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.repository.FavouriteRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import com.example.rickandmortyapp.core.UiState
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repository: FavouriteRepositoryInterface,
    characterId: Long
) : ViewModel() {

    val uiState: StateFlow<UiState<Boolean>> = repository
        .isFavourite(characterId)
        .map<Boolean, UiState<Boolean>> { isFavourite -> UiState.Success(isFavourite) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun toggleFavourite(character: CharacterRaM) {
        viewModelScope.launch {
            try {
                val isFavourite = (uiState.value as? UiState.Success)?.data == true
                if (isFavourite) repository.remove(character)
                else repository.add(character)
            } catch (e: Exception) {
                Log.e("TOGGLE_FAVOURITE", e.toString())
            }
        }
    }
}