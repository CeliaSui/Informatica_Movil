package es.uniovi.appasturiasbodegas.domain.favoritos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import es.uniovi.appasturiasbodegas.data.BodegaRepository
import es.uniovi.appasturiasbodegas.model.Bodega
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BodegaFavoritosViewModel(
    private val bodegaRepository: BodegaRepository
) : ViewModel() {

    val bodegasFavoritos: LiveData<List<Bodega>> = bodegaRepository.getFavoritos().asLiveData()

    // Agregar una bodega a favoritos
    fun setFavorito(bodegaId: Int, isFavorito: Boolean) {
        viewModelScope.launch {
            bodegaRepository.setFavorito(bodegaId, isFavorito)
        }
    }

    // Lista filtrada por zona y concejo
    private val _bodegasFiltradas = MutableLiveData<List<Bodega>>()
    val bodegasFiltradas: LiveData<List<Bodega>> get() = _bodegasFiltradas

    // Funcion de búsqueda por nombre
    fun searchBodegas(query: String): LiveData<List<Bodega>> {
        return bodegasFavoritos.map { bodegas ->
            bodegas.filter { bodega ->
                bodega.articles.article.any { article ->
                    article.nombre.content.contains(query, ignoreCase = true)
                }
            }
        }
    }

    // Funcion de búsqueda por zona y concejo
    fun getZonasYConcejos(): LiveData<Pair<List<String>, List<String>>> {
        return bodegasFavoritos.map { bodegas ->
            val zonas = mutableSetOf<String>()
            val concejos = mutableSetOf<String>()

            // Recorrer las bodegas y extraer zonas y concejos
            bodegas.forEach { bodega ->
                bodega.articles.article.forEach { article ->
                    zonas.add(article.contacto.zona.content)
                    concejos.add(article.contacto.concejo.content)
                }
            }
            Pair(zonas.toList(), concejos.toList()) // Devolver un par de listas con zonas y concejos únicos
        }
    }

    // Aplicar filtro por zona y concejo
    fun aplicarFiltro(zona: String?, concejo: String?) {
        // Iterar sobre las bodegas favoritas y aplicar el filtro
        bodegasFavoritos.map { bodegas ->
            // Si no hay filtro, devolver todas las bodegas
            if (zona.isNullOrEmpty() && concejo.isNullOrEmpty())
            {
                bodegas
            }
            else
            {
                // Filtrar las bodegas según la zona y el concejo
                bodegas.filter { bodega ->
                    bodega.articles.article.any { article ->
                        // Comprobar si la zona y el concejo coinciden con los filtros aplicados
                        val coincideZona = zona.isNullOrEmpty() || article.contacto.zona.content == zona
                        val coincideConcejo = concejo.isNullOrEmpty() || article.contacto.concejo.content == concejo
                        coincideZona && coincideConcejo // Devolver true si coincide la zona y el concejo
                    }
                }
            }
        }.observeForever { filtradas ->
            _bodegasFiltradas.postValue(filtradas)
        }
    }

    // Limpiar el filtro para cuando se destruye el fragmento
    fun limpiarFiltro() {
        _bodegasFiltradas.value = bodegasFavoritos.value ?: emptyList()
    }
}