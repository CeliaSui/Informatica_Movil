package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class NombreCanal(
    @Json(name = "title")
    val title: String,
    @Json(name = "value")
    val value: String,
    @Json(name = "CanalUrl")
    val canalUrl: CanalUrl
)