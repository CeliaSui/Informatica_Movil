package es.uniovi.appasturiasbodegas.domain.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.uniovi.appasturiasbodegas.data.BodegaRepository

class BodegaFavoritosViewModelFactory(
    private val bodegaRepository: BodegaRepository
) : ViewModelProvider.Factory {
    // Esta clase es responsable de crear instancias de BodegaFavoritosViewModel.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BodegaFavoritosViewModel::class.java)) {
            return BodegaFavoritosViewModel(bodegaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}