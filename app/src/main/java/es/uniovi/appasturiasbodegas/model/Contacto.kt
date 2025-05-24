package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Contacto(
    @Json(name = "Concejo")
    val concejo: Concejo,
    @Json(name = "Zona")
    val zona: Zona,
    @Json(name = "Email")
    val email: List<Email>,
    @Json(name = "Web")
    val web: Web,
    @Json(name = "Telefono")
    val telefono: Telefono,
    @Json(name = "Direccion")
    val direccion: Direccion,
    @Json(name = "title")
    val title: String,
    @Json(name = "MasInformacion")
    val masInformacion: MasInformacion
)