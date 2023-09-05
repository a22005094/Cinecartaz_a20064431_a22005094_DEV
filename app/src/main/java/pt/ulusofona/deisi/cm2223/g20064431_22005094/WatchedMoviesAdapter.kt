package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        "${watchedMovies[position].review}/${MAX_RATING_VALUE}".also { holder.binding.tvEvaluation.text = it }
        holder.binding.tvTheatre.text = watchedMovies[position].theatre.name
        //TODO: Get image from IMDB:  holder.binding.ivImageview.setImageBitmap(watchedMovies[position].movie.posterUrl)

        //holder.binding.ivImageview.setImageURI(Uri.parse(watchedMovies[position].movie.posterUrl))
        Glide.with(holder.binding.ivImageview.context).
            load(Uri.parse(watchedMovies[position].movie.posterUrl))
            .into(holder.binding.ivImageview)// .error(R.drawable.ic_watched_movies);


        // TODO: Remove!!
        // Log.i("RMata", "@@@ ${watchedMovies[position].movie.title} / ${watchedMovies[position].review}")
        holder.binding.tvYear.text = "2023"
    }
}