package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Informacion(
    @Json(name = "Productos")
    val productos: Productos,
    @Json(name = "Visitas")
    val visitas: Visitas,
    @Json(name = "Denominacion")
    val denominacion: Denominacion,
    @Json(name = "Observaciones")
    val observaciones: Observaciones,
    @Json(name = "BreveDescripcion")
    val breveDescripcion: BreveDescripcion,
    @Json(name = "Descripcion")
    val descripcion: Descripcion,
    @Json(name = "Horario")
    val horario: Horario,
    @Json(name = "Tarifas")
    val tarifas: Tarifas,
    @Json(name = "title")
    val title: String,
    val dynamic_element: DynamicElement?
)