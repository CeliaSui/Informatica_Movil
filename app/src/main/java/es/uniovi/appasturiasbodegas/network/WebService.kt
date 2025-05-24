package es.uniovi.appasturiasbodegas.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import es.uniovi.appasturiasbodegas.adapters.EmailAdapter
import es.uniovi.appasturiasbodegas.model.Bodega
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// URL base del servicio REST que proporciona los datos de las bodegas, llagares y queserías de Asturias.
private val BASE_URL = "http://orion.edv.uniovi.es/~arias/json/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(EmailAdapter()) // Añadir el adaptador para manejar el campo email porque es un campo que puede ser una lista y esto es necesario para que se procese correctamente.
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RestApiService {
    @GET("BodegasLlagaresQueserias.json") // Endpoint que devuelve los datos de las bodegas, llagares y queserías en formato JSON.
    suspend fun getBodegaDataInNetwork(): Bodega // Devuelve una lista de bodegas, llagares y queserías
}

// Implementación Singleton del servicio
object RestApi {
    val retrofitService: RestApiService by lazy {
        retrofit.create(RestApiService::class.java)
    }
}