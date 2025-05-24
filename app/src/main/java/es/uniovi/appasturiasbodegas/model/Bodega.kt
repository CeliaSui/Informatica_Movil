package es.uniovi.appasturiasbodegas.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "bodega_table")
data class Bodega(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Json(name = "articles")
    val articles: Articles,
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false // Atributo para marcar como favorito
)
{
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Bodega>() {
            override fun areItemsTheSame(oldItem: Bodega, newItem: Bodega): Boolean {
                return oldItem.articles == newItem.articles
            }

            override fun areContentsTheSame(oldItem: Bodega, newItem: Bodega): Boolean {
                return oldItem == newItem
            }
        }
    }
}