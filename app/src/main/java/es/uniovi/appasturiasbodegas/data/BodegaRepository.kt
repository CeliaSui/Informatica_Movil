package es.uniovi.appasturiasbodegas.data

import android.util.Log
import es.uniovi.appasturiasbodegas.model.Bodega
import es.uniovi.appasturiasbodegas.model.BodegaDAO
import es.uniovi.appasturiasbodegas.network.RestApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first

// Clase que actúa como repositorio para acceder a los datos de las bodegas
class BodegaRepository(private val bodegaDAO: BodegaDAO) {

    // Obtener una bodega por su ID
    fun getBodegaById(id: Int): Flow<Bodega> {
        return bodegaDAO.getBodegaById(id)
    }

    // Obtener todas las bodegas
    fun getBodegas(): Flow<List<Bodega>> {
        return bodegaDAO.getAllBodegas()
    }

    // Insertar una lista de bodegas en la base de datos
    suspend fun insertBodegas(bodegas: List<Bodega>) {
        // Leer todo lo que ya hay en la base de datos
        val existentes = bodegaDAO.getAllBodegas().first()
        // Filtrar los que coinciden por articles
        val aInsertar = bodegas.filter { nueva ->
            existentes.none { vieja ->
                vieja.articles.article == nueva.articles.article
            }
        }
        // Solo meter los nuevos
        if (aInsertar.isNotEmpty()) {
            bodegaDAO.insertBodegas(aInsertar)
        }
    }

    // Obtener bodegas favoritas
    fun getFavoritos(): Flow<List<Bodega>> {
        return bodegaDAO.getFavoriteBodegas()
    }

    // Actualizar el estado de favorito de una bodega
    suspend fun setFavorito(id: Int, favorito: Boolean) {
        bodegaDAO.updateFavorite(id, favorito)
    }

    // Actualizar los datos de las bodegas desde la API
    fun updateBodegaData() =
        flow {

            // Se realiza la petición al servicio
            // Emitimos el estado de carga antes de realizar la petición
            emit(ApiResult.Loading(null))
            try {

                // Respuesta correcta
                val bodegaData = RestApi.retrofitService.getBodegaDataInNetwork()
                Log.d("JSON_DATA", "Datos obtenidos del JSON en updateBodegaData(): $bodegaData")
                // Se emite el estado Succes y se incluyen los datos recibidos
                emit(ApiResult.Success(bodegaData))
            } catch (e: Exception) {
                // Error en la red
                // Se emite el estado de Error con el mensaje que lo explica
                emit(ApiResult.Error(e.toString()))
            }
            // El flujo se ejecuta en el hilo I/O
        }.flowOn(Dispatchers.IO)

}