package com.example.rickandmortyapp.data.network

import com.example.rickandmortyapp.data.model.CharacterResponse
import com.example.rickandmortyapp.utils.EMPTY_STRING
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom

class RaMApiClient(
    private val client: HttpClient
) {
    suspend fun getCharacters(
        page: Int = 1,
        name: String = EMPTY_STRING,
        gender: String = EMPTY_STRING,
        status: String = EMPTY_STRING
    ): CharacterResponse {
        val url = URLBuilder().apply {
            takeFrom(RaMApiConfig.CHARACTER_ENDPOINT)
            parameters.append(RaMApiConfig.PARAM_PAGE, page.toString())
            if (name.isNotBlank()) parameters.append(RaMApiConfig.PARAM_NAME, name)
            if (gender.isNotBlank()) parameters.append(RaMApiConfig.PARAM_GENDER, gender)
            if (status.isNotBlank()) parameters.append(RaMApiConfig.PARAM_STATUS, status)
        }.buildString()

        return client.get(url).body()
    }
}