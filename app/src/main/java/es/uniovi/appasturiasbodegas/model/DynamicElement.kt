package es.uniovi.appasturiasbodegas.model

data class DynamicElement(
    val dynamic_content: DynamicContent,
    val index_type: String,
    val instance_id: String,
    val name: String,
    val type: String
)