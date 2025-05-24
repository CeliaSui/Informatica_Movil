package es.uniovi.appasturiasbodegas.domain.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import es.uniovi.appasturiasbodegas.data.ApiResult
import es.uniovi.appasturiasbodegas.data.BodegaRepository
import es.uniovi.appasturiasbodegas.model.Articles
import es.uniovi.appasturiasbodegas.model.Bodega
import es.uniovi.appasturiasbodegas.model.BodegaDAO
import es.uniovi.appasturiasbodegas.ui.AppUIState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BodegaViewModel(private val bodegaDAO: BodegaDAO) : ViewModel() {
    private val _appUIStateObservable = MutableLiveData<AppUIState>()
    val appUIStateObservable: LiveData<AppUIState> get() = _appUIStateObservable
    private val repository = BodegaRepository(bodegaDAO)

    // Variables para almacenar los filtros de zona y concejo
    private var filtroZona: String? = null
    private var filtroConcejo: String? = null

    // Bloque init para asegurar que se obtengan los datos al crear el ViewModel
    init {
        getBodegaData()
    }

    // Función para obtener los datos de las bodegas desde la API y guardarlos en la base de datos
    fun getBodegaData()
    {
        // Limpiar el estado de la UI antes de realizar la llamada a la API
        viewModelScope.launch {
            repository.updateBodegaData() // Llamada a la API para actualizar los datos de las bodegas
                .map {  result ->
                    when (result) {
                        // Si la llamada a la API fue exitosa, guardamos los datos en la base de datos
                        is ApiResult.Success -> {
                            val bodegas = result.data!!.articles.article.map { article ->
                                Bodega(articles = Articles(listOf(article)))
                            }
                            repository.insertBodegas(bodegas)
                            AppUIState.Success(bodegas)
                        }
                        // Si hubo un error al obtener los datos, mostramos un mensaje de error
                        is ApiResult.Error -> {
                            Log.e("MY_DEBUG", "Error al obtener los datos: ${result.message}")
                            AppUIState.Error(result.message.toString())
                        }
                        // Si la llamada a la API está en curso, mostramos un estado de carga
                        is ApiResult.Loading -> {
                            AppUIState.Loading(true)
                        }
                    }
                }
                .collect { state ->
                    _appUIStateObservable.value = state
                }
        }
    }

    // LiveData para obtener de la base de datos todas las bodegas
    fun getBodegas() = bodegaDAO.getAllBodegas().asLiveData()

    // Búsqueda por nombre y filtro por zona y concejo
    // Funcion de búsqueda por nombre
    fun searchBodegas(query: String): LiveData<List<Bodega>> {
        return bodegaDAO.getAllBodegas()
            .map { bodegas ->
                // Filtrar por nombre y evitar duplicados
                val filteredBodegas = bodegas.filter { bodega ->
                    bodega.articles.article.any { article ->
                        article.nombre.content.contains(query, ignoreCase = true)
                    }
                }
                // Eliminar duplicados (si existen) comparando los objetos
                filteredBodegas.distinctBy { it.id } // Usamos el id para identificar duplicados
            }
            .asLiveData()
    }

    private val _bodegasFiltradas = MutableLiveData<List<Bodega>>() // Variable para recoger todas las bodegas filtradas
    val bodegasFiltradas: LiveData<List<Bodega>> get() = _bodegasFiltradas // Poder recoger mediante una variable las bodegas filtradas

    // Función para obtener zonas y concejos
    fun getZonasYConcejos(): LiveData<Pair<List<String>, List<String>>> {
        // Obtener las bodegas desde la base de datos y mapearlas a zonas y concejos
        return getBodegas().map { bodegas ->
            val zonas = mutableSetOf<String>()
            val concejos = mutableSetOf<String>()
            // Ir bodega a bodega y de cada bodega a cada artículo para obtener la zona y el concejo
            bodegas.forEach { bodega ->
                bodega.articles.article.forEach { article ->
                    zonas.add(article.contacto.zona.content) // Añadir la zona al conjunto
                    concejos.add(article.contacto.concejo.content) // Añadir el concejo al conjunto
                }
            }
            Pair(zonas.toList(), concejos.toList()) // Devolver un par de listas con zonas y concejos únicos
        }
    }

    // Función para aplicar el filtro
    fun aplicarFiltro(zona: String?, concejo: String?) {
        // Guardar los filtros aplicados para poder limpiar el filtro después o volver a aplicarlo tras un cambio en la UI
        filtroZona = zona
        filtroConcejo = concejo
        // Obtener las bodegas desde la base de datos y aplicar el filtro
        bodegaDAO.getAllBodegas()
            .map { bodegas ->
                // Si no hay filtro, devolver todas las bodegas
                if (filtroZona.isNullOrEmpty() && filtroConcejo.isNullOrEmpty())
                {
                    bodegas
                }
                else
                {
                    // Filtrar las bodegas según la zona y el concejo
                    bodegas.filter { bodega ->
                        bodega.articles.article.any { article ->
                            // Comprobar si coincide la zona y el concejo comparando el contenido con el de la bodega. Deja pasar si no hay filtro
                            val coincideZona = filtroZona == null || filtroZona == "" || article.contacto.zona.content == filtroZona
                            val coincideConcejo = filtroConcejo == null || filtroConcejo == "" || article.contacto.concejo.content == filtroConcejo
                            coincideZona && coincideConcejo // Devolver true si coincide la zona y el concejo
                        }
                    }
                }
            }
            .asLiveData()
            .observeForever { filtradas ->
                _bodegasFiltradas.postValue(filtradas) // Actualizar las bodegas filtradas
            }
    }

}