package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class CanalUrl(
    @Json(name="title")
    val title: String
)