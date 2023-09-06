package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentWatchedMovieDetailsBinding
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
                        }
                        binding.tvTitle.text = watchedMovie?.movie?.title
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