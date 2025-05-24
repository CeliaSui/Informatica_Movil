package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Slide(
    @Json(name = "SlideUrl")
    val slideUrl: SlideUrl,
    @Json(name = "SlideTitulo")
    val slideTitulo: SlideTitulo,
    @Json(name = "title")
    val title: String,
    @Json(name = "value")
    val value: String
)