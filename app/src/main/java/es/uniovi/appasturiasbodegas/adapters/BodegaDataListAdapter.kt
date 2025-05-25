package es.uniovi.appasturiasbodegas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import es.uniovi.appasturiasbodegas.databinding.ListItemBinding
import es.uniovi.appasturiasbodegas.model.Bodega

// Clase adaptadora para mostrar una lista de bodegas en un RecyclerView.
class BodegaDataListAdapter(
    private val onItemClicked: (Int) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : ListAdapter<Bodega, BodegaViewHolder>(Bodega.DIFF_CALLBACK) {

    // Adapter para la lista de bodegas que muestra información básica y permite marcar como favorita.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodegaViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BodegaViewHolder(binding, onItemClicked, onFavClick) //, onFavClick)
    }

    override fun onBindViewHolder(holder: BodegaViewHolder, position: Int) {
        // Obtener la bodega de la lista y enlazarlo al BodegaViewHolder
        val bodega = getItem(position)
        if (bodega != null) { // Verifica que la bodega no sea nula antes de hacer el bind
            holder.bind(bodega)
        }
    }
}