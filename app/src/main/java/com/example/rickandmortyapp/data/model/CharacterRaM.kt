package com.example.rickandmortyapp.data.model

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CharacterRaM(
    val id: Long? = null,
    val name: String? = null,
    val status: String? = null,
    val species: String? = null,
    val type: String? = null,
    val gender: String? = null,
    val origin: Location? = null,
    val location: Location? = null,
    val image: String? = null,
    val episode: List<String>? = null,
    val url: String? = null,
    val created: String? = null
)

@Serializable
data class Location(
    val name: String,
    val url: String
)

val CharacterType = object : NavType<CharacterRaM>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): CharacterRaM? =
        Json.decodeFromString(bundle.getString(key) ?: return null)

    override fun parseValue(value: String): CharacterRaM =
        Json.decodeFromString(Uri.decode(value))

    override fun put(bundle: Bundle, key: String, value: CharacterRaM) =
        bundle.putString(key, Json.encodeToString(value))

    override fun serializeAsValue(value: CharacterRaM): String =
        Uri.encode(Json.encodeToString(value))
}