package com.example.rickandmortyapp.extensions

import com.example.rickandmortyapp.data.local.FavouriteEntity
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.model.Location
import com.example.rickandmortyapp.utils.EMPTY_STRING


fun FavouriteEntity.toCharacterRaM() = CharacterRaM(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image,
    origin = originName?.let { Location(it, EMPTY_STRING) },
    location = locationName?.let { Location(it, EMPTY_STRING) },
    episode = List(episodeCount ?: 0) { EMPTY_STRING }
)

fun CharacterRaM.toEntity() = FavouriteEntity(
    id = id ?: 0,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image,
    originName = origin?.name,
    locationName = location?.name,
    episodeCount = episode?.count()
)