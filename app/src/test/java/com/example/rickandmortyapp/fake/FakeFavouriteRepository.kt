package com.example.rickandmortyapp.fake

import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.repository.FavouriteRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFavouriteRepository : FavouriteRepositoryInterface {

    private val favourites = MutableStateFlow<List<CharacterRaM>>(emptyList())

    override fun getAll(): Flow<List<CharacterRaM>> = favourites

    override fun isFavourite(id: Long): Flow<Boolean> =
        favourites.map { list -> list.any { it.id == id } }

    override suspend fun add(character: CharacterRaM) {
        favourites.value += character
    }

    override suspend fun remove(character: CharacterRaM) {
        favourites.value = favourites.value.filter { it.id != character.id }
    }
}