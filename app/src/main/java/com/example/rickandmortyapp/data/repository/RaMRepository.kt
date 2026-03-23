package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.CharacterResponse
import com.example.rickandmortyapp.data.network.RaMApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RaMRepository(
    private val apiClient: RaMApiClient
) : RaMRepositoryInterface {

    override suspend fun getCharacters(
        page: Int,
        name: String,
        gender: String,
        status: String
    ): CharacterResponse = withContext(Dispatchers.IO) {
        apiClient.getCharacters(page, name, gender, status)
    }
}