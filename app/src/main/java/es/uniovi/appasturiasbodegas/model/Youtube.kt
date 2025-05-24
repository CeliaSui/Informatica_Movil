package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Youtube(
    @Json(name = "title")
    val title: String,
    @Json(name = "content")
    val content: String?
)