package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemMovieSearchResultBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie

// A utilizar na página "Movie Picker" quando se regista um Filme.

class MovieSearchResultAdapter(private var movies: List<OMDBMovie> = listOf()) :
    RecyclerView.Adapter<MovieSearchResultAdapter.MovieResultViewHolder>() {

    // Esta classe interna está "(...) apenas a definir a estrutura do que será carregado em cada elemento da RecycleView" (Ficha6)
    class MovieResultViewHolder(val binding: ItemMovieSearchResultBinding) : RecyclerView.ViewHolder(binding.root)

    // Implementação
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieResultViewHolder {
        return MovieResultViewHolder(
            ItemMovieSearchResultBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieResultViewHolder, position: Int) {
        val movie: OMDBMovie = movies[position]

        val movieInfo = "${movie.title} (${movie.year})"

        // DADOS A USAR:
        //  > Title
        //  > Ano
        //  > Genre
        //  > Rating imdb
        //  > Director

        // TODO - se não tem imagem, preencher com a "NoImage.png"
        // val movieImg = CinecartazRepository.getInstance().getMoviePosterFromUrl(movie.posterUrl) { response ->
        //     if (response.isSuccess) {
        //         holder.binding.imgViewThumbnail.setImageBitmap(response.getOrDefault(null))
        //     }
        // }


        holder.binding.tvMovieTitle.text = movieInfo
    }

    override fun getItemCount(): Int = movies.size

    // Para atualizar diretamente a lista de Imagens
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<OMDBMovie>) {
        this.movies = items
        notifyDataSetChanged()
    }

}