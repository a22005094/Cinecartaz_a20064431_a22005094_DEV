package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemThumbnailImgSelecionadaBinding

// A utilizar em conjunto com RecyclerViews de imagens.


// TODO - > Não esquecer que na criação de novos Adapters (ex. FilmeAdapter para a página de Lista), se utiliza
//        uma nova classe igual, mas do tipo "data class" e com o termo "Ui" no fim -- por exemplo, na calculadora
//        existia o Operation e o OperationUI; no exemplo do LOTR, existia também um Adapter c/ lista de "CharacterUI".
//        > O QUE EXISTE DE DIFERENTE NA CLASSE "UI"?
//          Nada!... EXCETO se quisermos passar o objeto entre Activities/Fragmentos, onde se tem de aplicar:
//          1) "@Parcelize"   - no inicio da classe
//          2) ": Parcelable" - classe Parcelable (Serializable)


class ImagemAdapter(private var imagens: List<Bitmap> = listOf()) :
    RecyclerView.Adapter<ImagemAdapter.ImagemViewHolder>() {

    // Esta classe interna está "(...) apenas a definir a estrutura do que será carregado em cada elemento da RecycleView" (Ficha6)
    class ImagemViewHolder(val binding: ItemThumbnailImgSelecionadaBinding) : RecyclerView.ViewHolder(binding.root)

    // Implementação
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagemViewHolder {
        // TODO test - LayoutInflater vai correr bem??? Não foi colocado no layout item_thumbnail_img_selecionada.xml ...
        return ImagemViewHolder(
            ItemThumbnailImgSelecionadaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagemViewHolder, position: Int) {
        holder.binding.imgViewThumbnail.setImageBitmap(imagens[position])
    }

    override fun getItemCount(): Int = imagens.size

    // Para atualizar diretamente a lista de Imagens
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<Bitmap>) {
        this.imagens = items
        notifyDataSetChanged()
    }

}