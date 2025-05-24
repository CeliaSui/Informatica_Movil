package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Rss(
    @Json(name = "title")
    val title: String
)