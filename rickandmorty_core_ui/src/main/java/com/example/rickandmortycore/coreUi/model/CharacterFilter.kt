package com.example.rickandmortycore.coreUi.model

import com.example.rickandmortycore.coreUi.utils.EMPTY_STRING

data class CharacterFilter(
    val showFavourites: Boolean = false,
    val gender: String = EMPTY_STRING,
    val status: String = EMPTY_STRING,
    val sortOption: SortOption = SortOption.NAME
) {
    val isActive: Boolean
        get() = showFavourites || gender.isNotBlank() || status.isNotBlank()
}