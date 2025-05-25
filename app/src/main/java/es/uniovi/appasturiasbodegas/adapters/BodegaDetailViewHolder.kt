package es.uniovi.appasturiasbodegas.adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.HtmlCompat
import es.uniovi.appasturiasbodegas.databinding.FragmentDetailBinding
import es.uniovi.appasturiasbodegas.model.Bodega
import org.json.JSONObject
import com.bumptech.glide.Glide
import es.uniovi.appasturiasbodegas.R

// Clase que maneja la vista de detalles de una bodega
class BodegaDetailViewHolder(private val binding: FragmentDetailBinding) {
    fun bind(bodega: Bodega) {
        val article = bodega.articles.article[0]

        // Añadir texto a cada uno de los TextView en Detalles
        binding.bodegaName.text = HtmlCompat.fromHtml("${getContentOrDefault(article.nombre.content)}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        // Hay algunas bodegas sin descripcion, asi que pongo la descripción breve en su lugar
        if(!isDescripcionVacia(article.informacion.descripcion.content)) {
            binding.bodegaDescription.text = HtmlCompat.fromHtml("${article.informacion.descripcion.title}: ${getContentOrDefault(article.informacion.descripcion.content)}", HtmlCompat.FROM_HTML_MODE_LEGACY)
            Log.d("DEBUG_DETAIL", "Descripcion: ${article.informacion.descripcion.content}")
        } else {
            binding.bodegaDescription.text = HtmlCompat.fromHtml("${article.informacion.breveDescripcion.title}: ${getContentOrDefault(article.informacion.breveDescripcion.content)}", HtmlCompat.FROM_HTML_MODE_LEGACY)
            Log.d("DEBUG_DETAIL", "BreveDescripcion: ${article.informacion.breveDescripcion.content}")
        }
        binding.bodegaHorario.text = HtmlCompat.fromHtml("${article.informacion.horario.title}: ${getContentOrDefault(article.informacion.horario.content)}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.bodegaDireccion.text = HtmlCompat.fromHtml("${article.contacto.zona.title}: ${getContentOrDefault(article.contacto.zona.content)}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.bodegaWeb.text = HtmlCompat.fromHtml("${article.contacto.web.title}: ${getContentOrDefault(article.contacto.web.content)}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.bodegaTelefono.text = HtmlCompat.fromHtml("${article.contacto.telefono.title}: ${getContentOrDefault(article.contacto.telefono.content)}", HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Al pulsar el botón de la web, abrir el navegador
        binding.bodegaWeb.setOnClickListener {
            val url = article.contacto.web.content
            if (url != null && url.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                val chooser = Intent.createChooser(intent, "Abrir con")
                binding.bodegaWeb.context.startActivity(chooser) // Dejar al usuario elegir con que navegador abrir
            } else {
                Log.e("DEBUG", "URL is empty or null")
            }
        }

        // Al pulsar el botón de teléfono, abrir la aplicación de teléfono
        /*
        Puede tener el siguiente formato:
         "Telefono": {
                "title": "Teléfono",
                "content": "985 894 576 / 618 467 577 (WhatsApp)"
            },
          Asi que hay que quitar el texto de WhatsApp y dependiendo de en qué núpero pulse, usar uno u otro
         */
        binding.bodegaTelefono.setOnClickListener {
            val phoneContent = article.contacto.telefono.content
            if (phoneContent != null && phoneContent.isNotEmpty()) {
                // Extraer lista con los números de teléfono del contenido
                // Expresión regular para números de teléfono
                val phoneRegex = Regex("""\d{3}[\s\-]?\d{3}[\s\-]?\d{3}""") // Formato: 985 894 576 o 985-894-576
                // Extraer los números de teléfono y eliminar espacios
                val phonesList = phoneRegex.findAll(phoneContent).map { it.value.replace(" ", "") }.toList()

                if (phonesList.isNotEmpty()) {
                    // Mostrar un diálogo con los números de teléfono para que el usuario elija
                    AlertDialog.Builder(binding.bodegaTelefono.context)
                        .setTitle(R.string.select_phone_number)
                        .setItems(phonesList.toTypedArray()) { _, which ->
                            val selectedPhone = phonesList[which] // Obtener el número de teléfono seleccionado
                            val intent = Intent(Intent.ACTION_DIAL).apply { // Abrir la aplicación de teléfono
                                data = Uri.parse("tel:$selectedPhone") // Intent para marcar el número
                            }
                            binding.bodegaTelefono.context.startActivity(intent) // Iniciar la actividad
                        }
                        .show()
                }
            } else {
                Log.e("DEBUG", "Phone number is empty or null")
            }
        }

        // Mostrar todas las imagenes de la bodega
        val imageView = binding.bodegaImage
        val slides = article.visualizador.slide
        if (slides.isNotEmpty()) {
            val imageUrls: List<String> = slides.mapNotNull { slide ->
                try {
                    val parsed = JSONObject(slide.value)
                    val uuid = parsed.optString("uuid")
                    val groupId = parsed.optString("groupId")
                    if (uuid.isNotEmpty() && groupId.isNotEmpty()) {
                        "https://www.turismoasturias.es/documents/$groupId/$uuid"

                    } else {
                        null
                    }
                } catch (e: Exception) {
                    Log.e("DEBUG", "JSON parse error", e)
                    null
                }
            }
            val adapter = ImagePagerAdapter(imageUrls)
            imageView.adapter = adapter
        }
    }

    // Función para obtener el contenido o un valor por defecto indicando que no hay contenido
    fun getContentOrDefault(content: String?): String {
        val context = binding.root.context
        if (content.isNullOrEmpty()) {
            return context.getString(R.string.map_no_horario)

        } else {
            return content
        }
    }

    // Función para comprobar si la descripción está vacía (hay html, pero al mostrarlo no se ve nada)
    fun isDescripcionVacia(content: String?): Boolean {
        if (content.isNullOrBlank()) return true
        // Elimina etiquetas HTML y espacios
        val text = content.replace(Regex("<.*?>"), "").replace("&nbsp;", "").trim()
        return text.isEmpty()
    }

}