package com.example.rickandmortycore.coreUi.model

import com.example.rickandmortycore.coreUi.utils.EMPTY_STRING

data class CharacterDisplayModel(
    val id: Long = 0,
    val name: String = EMPTY_STRING,
    val status: String = EMPTY_STRING,
    val species: String = EMPTY_STRING,
    val gender: String = EMPTY_STRING,
    val image: String = EMPTY_STRING,
    val origin: String = EMPTY_STRING,
    val location: String = EMPTY_STRING,
    val episodeCount: Int = 0
)