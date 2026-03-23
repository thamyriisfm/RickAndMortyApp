package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.CharacterResponse
import com.example.rickandmortyapp.utils.EMPTY_STRING

interface RaMRepositoryInterface {
    suspend fun getCharacters(
        page: Int = 1,
        name: String = EMPTY_STRING,
        gender: String = EMPTY_STRING,
        status: String = EMPTY_STRING
    ): CharacterResponse
}