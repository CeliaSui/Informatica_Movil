package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Facebook(
    @Json(name = "title")
    val title: String,
    @Json(name = "content")
    val content: String?
)