package es.uniovi.appasturiasbodegas.domain.mapa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uniovi.appasturiasbodegas.data.BodegaRepository
import es.uniovi.appasturiasbodegas.model.Bodega
import kotlinx.coroutines.launch

class MapViewModel(private val bodegaRepository: BodegaRepository) : ViewModel() {
    private val _bodegas = MutableLiveData<List<Bodega>>()
    val bodegas: LiveData<List<Bodega>> get() = _bodegas

    // Cargar las bodegas al iniciar el ViewModel
    fun loadBodegas() {
        viewModelScope.launch {
            bodegaRepository.getBodegas().collect { bodegas ->
                _bodegas.value = bodegas
            }
        }
    }
}