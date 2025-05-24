package es.uniovi.appasturiasbodegas.domain.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.uniovi.appasturiasbodegas.data.BodegaRepository

class BodegaDetailsViewModelFactory(
    private val bodegaRepository: BodegaRepository,
    private val bodegaId: Int
) : ViewModelProvider.Factory {
    // Esta clase es responsable de crear instancias de BodegaDetailsViewModel.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BodegaDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BodegaDetailsViewModel(bodegaRepository, bodegaId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}