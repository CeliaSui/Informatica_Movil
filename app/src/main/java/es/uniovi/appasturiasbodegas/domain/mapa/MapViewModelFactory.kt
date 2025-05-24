package es.uniovi.appasturiasbodegas.domain.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.uniovi.appasturiasbodegas.data.BodegaRepository

class MapViewModelFactory(private val bodegaRepository: BodegaRepository) : ViewModelProvider.Factory {
    // Esta clase es responsable de crear instancias de MapViewModel.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(bodegaRepository) as T
    }
}