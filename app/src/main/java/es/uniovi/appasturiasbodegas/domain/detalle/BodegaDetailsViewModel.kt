package es.uniovi.appasturiasbodegas.domain.detalle

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import es.uniovi.appasturiasbodegas.data.BodegaRepository
import es.uniovi.appasturiasbodegas.model.Bodega

class BodegaDetailsViewModel(
    private val repository: BodegaRepository,
    private val bodegaId: Int
) : ViewModel() {
    // LiveData que observa bodegaId y obtiene los detalles de la bodega
    val mBodega: LiveData<Bodega> = repository.getBodegaById(bodegaId).asLiveData()

}