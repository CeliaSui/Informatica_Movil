package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Archivo(
    @Json(name = "ArchivoTitulo")
    val archivoTitulo: ArchivoTitulo,
    @Json(name = "title")
    val title: String,
    @Json(name = "value")
    val value: String
)