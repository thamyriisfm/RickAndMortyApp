package com.example.rickandmortyapp.data.network

object RaMApiConfig {
    const val BASE_URL = "https://rickandmortyapi.com/api"
    const val CHARACTER_ENDPOINT = "$BASE_URL/character"

    const val PARAM_PAGE = "page"
    const val PARAM_NAME = "name"
    const val PARAM_GENDER = "gender"
    const val PARAM_STATUS = "status"
}