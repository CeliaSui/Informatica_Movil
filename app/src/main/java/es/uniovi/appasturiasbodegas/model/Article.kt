package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Article(
    @Json(name = "Nombre")
    val nombre: Nombre,
    @Json(name = "Visualizador")
    val visualizador: Visualizador,
    @Json(name = "Informacion")
    val informacion: Informacion,
    @Json(name = "Geolocalizacion")
    val geolocalizacion: Geolocalizacion,
    @Json(name = "Contacto")
    val contacto: Contacto,
    @Json(name = "Descargas")
    val descargas: Descargas?,
    @Json(name = "RedesSociales")
    val redesSociales: RedesSociales,
    @Json(name = "Reservas")
    val reservas: Reservas?
)