package es.uniovi.appasturiasbodegas.model

import com.squareup.moshi.Json

data class Articles(
    @Json(name = "article")
    val article: List<Article>
)