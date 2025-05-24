package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Geolocalizacion(
    @Json(name = "Coordenadas")
    val coordenadas: Coordenadas,
    @Json(name = "title")
    val title: String
)