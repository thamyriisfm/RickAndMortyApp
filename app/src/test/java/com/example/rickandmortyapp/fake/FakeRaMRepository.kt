package com.example.rickandmortyapp.fake

import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.model.CharacterResponse
import com.example.rickandmortyapp.data.model.Info
import com.example.rickandmortyapp.data.repository.RaMRepositoryInterface

class FakeRaMRepository : RaMRepositoryInterface {

    var callCount = 0
    var shouldThrowError = false
    var shouldReturnEmpty = false

    private val characters = listOf(
        CharacterRaM(id = 1, name = "Rick Sanchez", status = "Alive", species = "Human", gender = "Male"),
        CharacterRaM(id = 2, name = "Morty Smith", status = "Alive", species = "Human", gender = "Male"),
        CharacterRaM(id = 3, name = "Beth Smith", status = "Alive", species = "Human", gender = "Female"),
        CharacterRaM(id = 4, name = "Jerry Smith", status = "Alive", species = "Human", gender = "Male"),
    )

    override suspend fun getCharacters(
        page: Int,
        name: String,
        gender: String,
        status: String
    ): CharacterResponse {
        if (shouldThrowError) throw Exception("Erro de rede simulado")
        callCount++

        val filtered = if (shouldReturnEmpty) {
            emptyList()
        } else {
            characters.filter { character ->
                val matchesName = name.isBlank() ||
                        character.name?.contains(name, ignoreCase = true) == true
                val matchesGender = gender.isBlank() ||
                        character.gender?.equals(gender, ignoreCase = true) == true
                val matchesStatus = status.isBlank() ||
                        character.status?.equals(status, ignoreCase = true) == true
                matchesName && matchesGender && matchesStatus
            }
        }

        return CharacterResponse(
            info = Info(count = filtered.size, pages = 1, next = null, prev = null),
            results = filtered
        )
    }
}