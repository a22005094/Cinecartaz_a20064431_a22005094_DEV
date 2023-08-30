package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemThumbnailImgSelecionadaBinding

// - A utilizar em conjunto com RecyclerViews de imagens.
// - Não esquecer de utilizar um tipo de dados Parcelable à parte, caso tenha de ser passado entre Fragments/Activities.


class SelectedImageAdapter(private var images: List<Bitmap> = listOf()) :
    RecyclerView.Adapter<SelectedImageAdapter.SelectedImageViewHolder>() {


    // Esta classe interna está "(...) apenas a definir a estrutura do que será carregado em cada elemento da RecycleView" (Ficha6)
    class SelectedImageViewHolder(val binding: ItemThumbnailImgSelecionadaBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        return SelectedImageViewHolder(
            ItemThumbnailImgSelecionadaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        holder.binding.imgViewThumbnail.setImageBitmap(images[position])
    }


    override fun getItemCount(): Int = images.size


    // Para atualizar diretamente a lista de Imagens
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<Bitmap>) {
        this.images = items
        notifyDataSetChanged()
    }

}