package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class MasInformacion(
    @Json(name="title")
    val title: String
)