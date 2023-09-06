package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemPhotoBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage

class PhotoAdapter(
    private var photos: List<CustomImage>
    ) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        // bind photo image to holder
        Glide.with(holder.binding.ivPhoto.context).
        load(photos[position].imageData) //.asbitmap? check if needed
            .into(holder.binding.ivPhoto)
    }

    override fun getItemCount() = photos.size

    // Para atualizar diretamente a lista de Imagens
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<CustomImage>) {
        this.photos = items
        notifyDataSetChanged()
    }
}