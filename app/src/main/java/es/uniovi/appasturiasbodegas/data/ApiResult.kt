package es.uniovi.appasturiasbodegas.data

import es.uniovi.appasturiasbodegas.state.AppState


sealed class ApiResult <out T> (val status: AppState, val data: T?, val message:String?) {

    data class Success<out R>(val _data: R): ApiResult<R>(
        status = AppState.SUCCESS,
        data = _data,
        message = null
    )

    data class Error(val exception: String): ApiResult<Nothing>(
        status = AppState.ERROR,
        data = null,
        message = exception
    )

    data class Loading<out R>(val _data: R?) : ApiResult<R>(
        status = AppState.LOADING,
        data = _data,
        message = "Loading..."
    )

}