package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.CharacterRaM
import kotlinx.coroutines.flow.Flow

interface FavouriteRepositoryInterface {
    fun getAll(): Flow<List<CharacterRaM>>
    fun isFavourite(id: Long): Flow<Boolean>
    suspend fun add(character: CharacterRaM)
    suspend fun remove(character: CharacterRaM)
}