package es.uniovi.appasturiasbodegas.data
import es.uniovi.appasturiasbodegas.model.Articles
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Clase que define métodos para convertir objetos Articles a String y viceversa, para que Room pueda almacenarlos.
class Converters {
    // Se crea una instancia de Moshi, que es una librería para convertir objetos a JSON y viceversa.
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    // Se crea un adaptador específico para la clase Articles.
    private val adapter = moshi.adapter(Articles::class.java)
    // Convierte un objeto Articles a un String (JSON) para guardarlo en la base de datos.
    @TypeConverter
    fun fromArticles(articles: Articles): String {
        return adapter.toJson(articles)
    }

    // Convierte un String (JSON) a un objeto Articles al leerlo de la base de datos.
    @TypeConverter
    fun toArticles(json: String): Articles? {
        return adapter.fromJson(json)
    }
}