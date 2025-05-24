package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class ReservasEmailX(
    @Json(name = "title")
    val title: String
)