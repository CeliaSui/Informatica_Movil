package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Descargas(
    @Json(name = "title")
    val title: String,
    @Json(name = "Archivo")
    val archivo: Archivo
)