package es.uniovi.appasturiasbodegas.domain.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.uniovi.appasturiasbodegas.model.BodegaDAO

class BodegaViewModelFactory(private val bodegaDAO: BodegaDAO) : ViewModelProvider.Factory {
    // Esta clase es responsable de crear instancias de BodegaViewModel.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BodegaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BodegaViewModel(bodegaDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
