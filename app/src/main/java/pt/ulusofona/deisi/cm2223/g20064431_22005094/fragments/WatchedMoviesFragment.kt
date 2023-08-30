package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.WatchedMoviesAdapter
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentWatchedMoviesBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils

class WatchedMoviesFragment : Fragment() {

    private lateinit var binding: FragmentWatchedMoviesBinding
    val adapter = WatchedMoviesAdapter(Utils.watchedMovies)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_watched_movies, container, false)

        // attempt (!) to change app's title
        activity?.title = "Watched movies"

        binding = FragmentWatchedMoviesBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.rvItemWatchedMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItemWatchedMovies.adapter = adapter

    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance() = WatchedMoviesFragment()
    }
}