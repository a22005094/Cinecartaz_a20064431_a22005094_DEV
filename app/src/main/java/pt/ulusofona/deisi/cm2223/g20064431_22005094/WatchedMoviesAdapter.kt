package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.provider.SyncStateContract.Constants
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ItemWatchedMoviesBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie

class WatchedMoviesAdapter (private var watchedMovies: List<WatchedMovie> = listOf()):
    RecyclerView.Adapter<WatchedMoviesAdapter.WatchedMoviesHolder>() {

    // Item holder class (in this case a single movie
    class WatchedMoviesHolder(val binding: ItemWatchedMoviesBinding) : RecyclerView.ViewHolder(binding.root)

    // MyMoviesAdapter implementation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchedMoviesHolder {
        // TODO - TEST!!
        return WatchedMoviesHolder(
            ItemWatchedMoviesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = watchedMovies.size

    override fun onBindViewHolder(holder: WatchedMoviesHolder, position: Int) {
        holder.binding.tvMovie.text = watchedMovies[position].movie.title
        holder.binding.tvEvaluation.text = "${watchedMovies[position].review}/${MAX_RATING_VALUE}"
        //TODO: Get image from IMDB:  holder.binding.ivImageview.setImageBitmap(watchedMovies[position].movie.posterUrl)

        // TODO: Remove!!
        Log.i("RMata", "@@@ ${watchedMovies[position].movie.title} / ${watchedMovies[position].review}")
    }
}