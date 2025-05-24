package es.uniovi.appasturiasbodegas.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

// Extension funcion observe LiveData para observar una sola vez
// Se utiliza para evitar fugas de memoria y no tener que eliminar el observer manualmente
// Lo uso para observar LiveData que solo necesito una vez, en este caso en el HomeFragment y
// FavoritesFragment para observar una vez lo de zonas y concejos para aplicar el filtro
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}