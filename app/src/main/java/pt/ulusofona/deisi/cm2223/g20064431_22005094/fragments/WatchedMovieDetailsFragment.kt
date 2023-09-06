package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.ARG_WATCHED_MOVIE_UUID
import pt.ulusofona.deisi.cm2223.g20064431_22005094.MAX_RATING_VALUE
import pt.ulusofona.deisi.cm2223.g20064431_22005094.NavigationManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentWatchedMovieDetailsBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomDate
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie

class WatchedMovieDetailsFragment : Fragment() {

    private lateinit var binding: FragmentWatchedMovieDetailsBinding

    private var watchedMovieUuiD: String? = null
    private var watchedMovie: WatchedMovie? = null
    private val model = CinecartazRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            watchedMovieUuiD = it.getString(ARG_WATCHED_MOVIE_UUID)
        }
        Log.i("RMata", "watcheduuID = ${watchedMovieUuiD}")

        populateData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_watched_movie_details, container, false)
        binding = FragmentWatchedMovieDetailsBinding.bind(view)

        return binding.root
    }

    private fun populateData(){
        // get watchedMovie details from DB
        if ( watchedMovieUuiD == null ) {
            Toast.makeText(context, getString(R.string.error_no_data_found), Toast.LENGTH_LONG).show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            model.getWatchedMovie(watchedMovieUuiD!!) {
                if (it.isSuccess) {
                    watchedMovie = it.getOrNull()
                    // watchedMovie = null // just to test "no data found"

                    Log.i("RMata", "title = ${watchedMovie?.movie?.title}")

                    // map data to view
                    CoroutineScope(Dispatchers.Main).launch{
                        if (watchedMovie == null){
                            NavigationManager.popCurrentFragment(activity!!.supportFragmentManager)
                            Toast.makeText(context, getString(R.string.error_no_data_found), Toast.LENGTH_LONG).show()
                        } else {
                            binding.tvSeenDate.text = CustomDate(watchedMovie!!.date).toString()
                            binding.tvTitle.text = watchedMovie!!.movie!!.title
                            // binding.lblLaunchDate.text = watchedMovie.movie.  TODO missing
                            binding.tvGenre.text = watchedMovie!!.movie.genre
                            binding.tvStoryline.text = watchedMovie!!.movie.plotShort
                            binding.tvImdbRating.text = "${watchedMovie!!.movie.ratingImdb}/${MAX_RATING_VALUE}"
                            binding.tvSeenDate2.text = CustomDate(watchedMovie!!.date).toString()
                            binding.tvTheatre.text = watchedMovie!!.theatre.name
                            binding.tvExperienceRating.text ="${watchedMovie!!.review}/${MAX_RATING_VALUE}"
                            binding.tvObs.text = watchedMovie!!.comments



                            Glide.with(binding.ivPoster.context).asBitmap().
                            load(watchedMovie?.movie?.poster?.imageData)
                                .into(binding.ivPoster)// .error(R.drawable.ic_watched_movies);
                        }
                    }
                }
            }
        }
    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance(watchedMovieUuiDArg: String) = WatchedMovieDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_WATCHED_MOVIE_UUID, watchedMovieUuiDArg)
            }
        }

    }
}