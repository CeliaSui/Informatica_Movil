package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class OtrosCanales(
    @Json(name = "NombreCanal")
    val nombreCanal: NombreCanal,
    @Json(name = "title")
    val title: String
)