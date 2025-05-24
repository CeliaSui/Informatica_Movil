package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class SlideTitulo(
    @Json(name = "title")
    val title: String,
    @Json(name = "content")
    val content: String
)