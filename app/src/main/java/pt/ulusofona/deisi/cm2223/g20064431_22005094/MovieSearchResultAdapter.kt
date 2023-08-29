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
        //val resultNumber = position + 1 // "human-friendly"
        //holder.binding.tvResultNumber.text = "$resultNumber."

        val movie = movies[position]
        val movieInfo = "${movie.title} (${movie.year})"
        holder.binding.tvMovieName.text = movieInfo
    }

    override fun getItemCount(): Int = movies.size

    // Para atualizar diretamente a lista de Imagens
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<OMDBMovie>) {
        this.movies = items
        notifyDataSetChanged()
    }

}