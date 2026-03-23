package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.local.FavouriteDao
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.extensions.toCharacterRaM
import com.example.rickandmortyapp.extensions.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavouriteRepository(
    private val dao: FavouriteDao
) : FavouriteRepositoryInterface {

    override fun getAll(): Flow<List<CharacterRaM>> =
        dao.getAll()
            .flowOn(Dispatchers.IO)
            .map { list -> list.map { it.toCharacterRaM() } }

    override fun isFavourite(id: Long): Flow<Boolean> =
        dao.isFavourite(id)
            .flowOn(Dispatchers.IO)

    override suspend fun add(character: CharacterRaM) =
        withContext(Dispatchers.IO) {
            dao.insert(character.toEntity())
        }

    override suspend fun remove(character: CharacterRaM) =
        withContext(Dispatchers.IO) {
            dao.delete(character.toEntity())
        }

}