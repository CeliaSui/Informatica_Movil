package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Visualizador(
    @Json(name = "title")
    val title: String,
    @Json(name = "Slide")
    val slide: List<Slide>
)