package com.example.rickandmortyapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val info: Info?,
    val results: List<CharacterRaM>?
)

@Serializable
data class Info(
    val count: Int?,
    val next: String?,
    val pages: Int?,
    val prev: String?
)