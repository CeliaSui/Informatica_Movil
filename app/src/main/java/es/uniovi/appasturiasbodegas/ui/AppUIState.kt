package es.uniovi.appasturiasbodegas.ui

import es.uniovi.appasturiasbodegas.model.Bodega
import es.uniovi.appasturiasbodegas.state.AppState

sealed class AppUIState(val state: AppState) {
    data class Success(val datos: List<Bodega>) : AppUIState(AppState.SUCCESS)
    data class Error(val message: String) : AppUIState(AppState.ERROR)
    data class Loading(val loading: Boolean = true) : AppUIState(AppState.LOADING)
}