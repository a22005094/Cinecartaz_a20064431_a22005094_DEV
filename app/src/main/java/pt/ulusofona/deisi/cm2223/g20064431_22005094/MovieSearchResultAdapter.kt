package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemMovieSearchResultBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.ImageUtils

// A utilizar na página "Movie Picker" quando se regista um Filme.

class MovieSearchResultAdapter(
    private val onClick: (OMDBMovie) -> Unit, private var movies: List<OMDBMovie> = listOf()
) : RecyclerView.Adapter<MovieSearchResultAdapter.MovieResultViewHolder>() {

    // Esta classe interna está "(...) apenas a definir a estrutura do que será carregado em cada elemento da RecycleView" (Ficha6)
    class MovieResultViewHolder(val binding: ItemMovieSearchResultBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieResultViewHolder {
        return MovieResultViewHolder(
            ItemMovieSearchResultBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: MovieResultViewHolder, position: Int) {
        val moviePos: OMDBMovie = movies[position]

        // * Listener para click
        holder.itemView.setOnClickListener {
            // 1. Preencher dados da Imagem no objeto OMDBMovie (se tiver Poster),
            //    para que sejam gravados em BD caso este Filme seja inserido
            if (moviePos.posterUrl.lowercase() != "n/a") {

                // Aproveitar o evento de seleção do Filme para já armazenar também os dados da sua imagem (Poster).
                // À partida, será acessível pela propriedade [holder.binding.ivPoster.drawable.toBitmap()].
                // No entanto, como precaução, se o carregamento da imagem falhar, predefine para o asset "No Image".

                // TODO - será isto?? confirmar...

                val imgData: ByteArray =
                    if (holder.binding.ivPoster.drawable != null) {
                        ImageUtils.convertBitmapToByteArray(holder.binding.ivPoster.drawable.toBitmap())!!
                    } else {

                        // TODO - não está a funcionar - não encontra o asset NO IMAGE, investigar!!

                        ImageUtils.convertUriToByteArray(
                            Uri.parse("file:///android_asset/$ASSET_PLACEHOLDER_NO_IMAGE"),
                            holder.binding.ivPoster.context.contentResolver
                        )!!
                    }

                val movieImg = CustomImage(
                    refId = moviePos.imdbId,
                    imageName = moviePos.posterUrl,
                    imageData = imgData
                )

                moviePos.poster = movieImg
            }

            // 2. Processar callback recebida
            onClick(moviePos)
        }

        // * Poster do filme
        //  Nota: Existem filmes que não trazem imagem (Poster).
        //  Se não tiver, carrega uma asset personalizada que adicionámos para indicar que este está em falta.
        //  Caso contrário, carrega a imagem que está no PosterURL do objeto.

        if (moviePos.posterUrl.isEmpty() || moviePos.posterUrl == "n/a") {
            // Filme sem poster
            Glide.with(holder.binding.ivPoster.context)
                .load(Uri.parse("file:///android_asset/$ASSET_PLACEHOLDER_NO_IMAGE")).into(holder.binding.ivPoster)

        } else {
            Glide.with(holder.binding.ivPoster.context).load(moviePos.posterUrl)
                // caso dê erro, predefine para o asset "Sem imagem"
                .error(Uri.parse("file:///android_asset/$ASSET_PLACEHOLDER_NO_IMAGE")).into(holder.binding.ivPoster)
        }


        // (Old - OkHttp)
        // val movieImg = CinecartazRepository.getInstance().getMoviePosterFromUrl(movie.posterUrl) { response ->
        //     if (response.isSuccess) {
        //         holder.binding.imgViewThumbnail.setImageBitmap(response.getOrDefault(null))
        //     }
        // }


        // Dados de momento preenchidos: [Title] [Year] [Genre] [Rating IMDB] [Director]
        holder.binding.tvMovieTitle.text = moviePos.title
        holder.binding.tvYear.text = "${moviePos.year}"
        holder.binding.tvGenre.text = moviePos.genre
        holder.binding.tvRatingImdb.text = "${moviePos.ratingImdb}"
        holder.binding.tvDirector.text = moviePos.director
    }

    override fun getItemCount(): Int = movies.size

    // Para atualizar diretamente a lista de Imagens
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<OMDBMovie>) {
        this.movies = items
        notifyDataSetChanged()
    }

}