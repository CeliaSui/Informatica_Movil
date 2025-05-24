package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class RedesSociales(
    @Json(name = "Rss")
    val rss: Rss,
    @Json(name = "OtrosCanales")
    val otrosCanales: OtrosCanales,
    @Json(name = "Youtube")
    val youtube: Youtube,
    @Json(name = "Pinterest")
    val pinterest: Pinterest,
    @Json(name = "Twitter")
    val twitter: Twitter,
    @Json(name = "Instagram")
    val instagram: Instagram,
    @Json(name = "title")
    val title: String,
    @Json(name = "Facebook")
    val facebook: Facebook
)