package com.example.rickandmortyapp.extensions

import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.utils.EMPTY_STRING
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel

fun CharacterRaM.toDisplayModel() = CharacterDisplayModel(
    id = id ?: 0,
    name = name ?: EMPTY_STRING,
    status = status ?: EMPTY_STRING,
    species = species ?: EMPTY_STRING,
    gender = gender ?: EMPTY_STRING,
    image = image ?: EMPTY_STRING,
    origin = origin?.name ?: EMPTY_STRING,
    location = location?.name ?: EMPTY_STRING,
    episodeCount = episode?.count() ?: 0
)

fun CharacterDisplayModel.toCharacterRaM() = CharacterRaM(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image
)