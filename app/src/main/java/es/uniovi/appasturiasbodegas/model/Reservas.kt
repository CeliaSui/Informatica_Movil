package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Reservas(
    @Json(name = "ReservasEmail")
    val reservasEmail: ReservasEmail,
    @Json(name = "VentaOnline")
    val ventaOnline: VentaOnline,
    @Json(name = "ReservasPhone")
    val reservasPhone: ReservasPhone,
    @Json(name = "title")
    val title: String,
    @Json(name = "ReservasWhatsapp")
    val reservasWhatsapp: ReservasWhatsapp,
    @Json(name = "CompraEntradas")
    val compraEntradas: CompraEntradas
)