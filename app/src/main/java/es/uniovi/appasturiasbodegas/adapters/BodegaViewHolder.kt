package es.uniovi.appasturiasbodegas.adapters

import android.animation.ObjectAnimator
import android.os.Build
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.appasturiasbodegas.R
import es.uniovi.appasturiasbodegas.databinding.ListItemBinding
import es.uniovi.appasturiasbodegas.model.Bodega
import org.json.JSONException
import org.json.JSONObject
import com.bumptech.glide.Glide

// Clase que maneja la vista de cada elemento de la lista de bodegas
class BodegaViewHolder(
        private val binding: ListItemBinding,
        private val onItemClicked: (Int) ->Unit,
        private val onFavClick: (Int, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

    init {
        // Hacemos que el itemView sea enfocable y clicable
        itemView.isFocusable = true
        itemView.isClickable = true
        // Seteamos el onClickListener en el itemView para que se ejecute cuando el usuario haga clic en un elemento
        itemView.setOnClickListener {
            // Pasamos el nombre de la asignatura usando el adaptador y la posición
            onItemClicked(adapterPosition)
        }
    }

    fun bind(bodega: Bodega)
    {
        val context = itemView.context
        val article = bodega.articles.article[0]

        val formattedText = context.getString(
            R.string.itemBodegaData, // Coger el TextView del list_item.xml
            getContentOrDefault(article.nombre.content),
            article.contacto.zona.title,
            getContentOrDefault(article.contacto.zona.content),
            article.contacto.concejo.title,
            getContentOrDefault(article.contacto.concejo.content)
        )

        // El texto está en html, por lo que hay que parsearlo
        binding.text1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }

        // Cambiar icono del botón favorito
        val icon = if (bodega.isFavorite) R.drawable.ic_fav_on else R.drawable.ic_fav_off
        binding.favButton.setImageResource(icon)

        // Click en botón favorito
        binding.favButton.setOnClickListener {
            val nuevoEstado = !bodega.isFavorite
            onFavClick(bodega.id, nuevoEstado) // Llama al ViewModel
            // Animación de escala (tamaño)
            val scaleUp = ObjectAnimator.ofFloat(binding.favButton, "scaleX", 1.5f)
            scaleUp.duration = 200
            scaleUp.start()

            val scaleDown = ObjectAnimator.ofFloat(binding.favButton, "scaleX", 1f)
            scaleDown.duration = 200
            scaleDown.start()
            // Animación de escala (tamaño) en Y
            val scaleUpY = ObjectAnimator.ofFloat(binding.favButton, "scaleY", 1.5f)
            scaleUpY.duration = 200
            scaleUpY.start()

            val scaleDownY = ObjectAnimator.ofFloat(binding.favButton, "scaleY", 1f)
            scaleDownY.duration = 200
            scaleDownY.start()
        }

        // Imagenes de la Bodega
        val imageView = binding.bodegaImage
        val slides = article.visualizador.slide
        if (slides.isNotEmpty()) {
            val slideValue = slides[0].value // Obtenemos la primera imagen solo para mostrarla
            try {
                val parsed = JSONObject(slideValue)
                val uuid = parsed.optString("uuid")
                val groupId = parsed.optString("groupId")

                if (uuid.isNotEmpty() && groupId.isNotEmpty()) {
                    val imageUrl = "https://www.turismoasturias.es/documents/$groupId/$uuid"
                    Glide.with(imageView.context).load(imageUrl).override(500,300).error(R.drawable.image_default).into(imageView)
                } else {
                    Log.e("DEBUG", "UUID not found in parsed JSON")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "JSON parse error", e)
            }
        }
    }

    fun getContentOrDefault(content: String?): String {
        val context = binding.root.context
        if (content.isNullOrEmpty()) {
            return context.getString(R.string.map_no_horario).replace("\n", "<br/>")
        } else {
            return content.replace("\n", "<br/>").takeIf { it.isNotEmpty() } ?: "<br/>"
        }
    }
}