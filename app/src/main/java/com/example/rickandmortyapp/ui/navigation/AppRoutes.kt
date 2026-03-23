package com.example.rickandmortyapp.ui.navigation

import com.example.rickandmortyapp.data.model.CharacterRaM
import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    @Serializable
    object Home : AppRoutes

    @Serializable
    object Favourites : AppRoutes

    @Serializable
    data class Details(val characterRaM: CharacterRaM) : AppRoutes
}