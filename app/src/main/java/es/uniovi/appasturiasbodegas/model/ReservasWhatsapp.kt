package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class ReservasWhatsapp(
    @Json(name = "title")
    val title: String
)