package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemThumbnailSelectedPictureBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage

// - A utilizar em conjunto com RecyclerViews de imagens.
// - * Não esquecer de utilizar um tipo de dados Parcelable à parte, caso tenha de ser passado entre Fragments/Activities.

class SelectedImageAdapter(private var images: List<CustomImage> = listOf()) :
    RecyclerView.Adapter<SelectedImageAdapter.SelectedImageViewHolder>() {

    // Esta classe interna está "(...) apenas a definir a estrutura do que será carregado em cada elemento da RecycleView" (Ficha6)
    class SelectedImageViewHolder(val binding: ItemThumbnailSelectedPictureBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        return SelectedImageViewHolder(
            ItemThumbnailSelectedPictureBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val imgBytes = images[position]

        Glide
            .with(holder.binding.imgViewThumbnail.context)
            .asBitmap()
            .load(imgBytes.imageData)
            .into(holder.binding.imgViewThumbnail)

        // holder.binding.imgViewThumbnail.setImageBitmap(images[position])
    }

    override fun getItemCount(): Int = images.size

    // Para atualizar diretamente a lista de Imagens
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<CustomImage>) {
        this.images = items
        notifyDataSetChanged()
    }

}