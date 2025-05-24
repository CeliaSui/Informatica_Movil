package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class SlideUrl(
    @Json(name = "title")
    val title: String
)