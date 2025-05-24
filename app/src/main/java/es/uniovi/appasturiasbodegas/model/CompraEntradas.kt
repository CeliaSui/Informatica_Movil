package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class CompraEntradas(
    @Json(name = "title")
    val title: String
)