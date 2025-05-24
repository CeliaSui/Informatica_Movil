package es.uniovi.appasturiasbodegas.adapters

import com.squareup.moshi.*
import es.uniovi.appasturiasbodegas.model.Email
import java.lang.reflect.Type

// Adaptador para convertir entre una lista de objetos Email y su representación JSON.
class EmailAdapter : JsonAdapter<List<Email>>() {
    // Convierte un String (JSON) a un objeto Articles al leerlo de la base de datos.
    @FromJson
    override fun fromJson(reader: JsonReader): List<Email>? {
        return when (reader.peek()) {
            JsonReader.Token.BEGIN_ARRAY -> { // Si es un array, lee todos los objetos Email y los mete en una lista.
                // Si es un array, lee todos los objetos Email y los mete en una lista.
                val emails = mutableListOf<Email>()
                reader.beginArray()
                while (reader.hasNext()) {
                    emails.add(EmailAdapterHelper.parseEmail(reader))
                }
                reader.endArray()
                emails
            }
            JsonReader.Token.BEGIN_OBJECT -> { // Si es un objeto, lo lee como un único Email.
                // Si es un solo objeto, lo mete en una lista de un solo elemento.
                listOf(EmailAdapterHelper.parseEmail(reader))
            }
            else -> throw JsonDataException("Expected BEGIN_OBJECT or BEGIN_ARRAY but was ${reader.peek()}")
        }
    }

    // Convierte una lista de Email a JSON (siempre como array).
    @ToJson
    override fun toJson(writer: JsonWriter, value: List<Email>?) {
        value?.let { // Si la lista no es nula, escribe cada Email como un objeto JSON dentro de un array.
            writer.beginArray() // Comienza un array JSON
            for (email in it) { // Itera sobre cada Email en la lista
                EmailAdapterHelper.writeEmail(writer, email) // Escribe el Email como un objeto JSON
            }
            writer.endArray() // Termina el array JSON
        }
    }
}

// Helper object para manejar la lógica de parsing y escritura de Email.
object EmailAdapterHelper {
    // Métodos para parsear y escribir objetos Email en formato JSON.
    fun parseEmail(reader: JsonReader): Email {
        // Inicializa las variables para almacenar los campos del Email
        var title: String? = null
        var content: String? = null
        reader.beginObject() // Comienza a leer un objeto JSON
        while (reader.hasNext()) {
            when (reader.nextName()) {
                // Lee los campos del objeto Email y los asigna a las variables correspondientes
                "title" -> title = reader.nextString()
                "content" -> content = reader.nextString()
            }
        }
        reader.endObject() // Termina de leer el objeto JSON
        return Email(title = title ?: "", content = content ?: "") // Crea un nuevo objeto Email con los campos leídos, usando valores por defecto si son nulos
    }

    // Método para escribir un objeto Email en formato JSON.
    fun writeEmail(writer: JsonWriter, email: Email) {
        writer.beginObject() // Comienza un objeto JSON
        writer.name("title").value(email.title) // Escribe el campo "title" del Email
        writer.name("content").value(email.content) // Escribe el campo "content" del Email
        writer.endObject() // Termina el objeto JSON
    }
}
