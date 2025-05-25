package es.uniovi.appasturiasbodegas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.uniovi.appasturiasbodegas.R
import es.uniovi.appasturiasbodegas.databinding.ItemImageBinding

// Clase adaptadora para mostrar una lista de imágenes en un ViewPager.
class ImagePagerAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {
    // Adapter para el ViewPager que muestra varias imágenes de una bodega.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // ViewHolder para cada imagen en el ViewPager.
        fun bind(imageUrl: String) {
            Glide.with(binding.imageView.context)
                .load(imageUrl)
                .error(R.drawable.image_default)
                .into(binding.imageView)
        }
    }
}