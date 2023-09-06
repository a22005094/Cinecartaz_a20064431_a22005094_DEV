package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.NavigationManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.WatchedMoviesAdapter
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentWatchedMoviesBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie


class WatchedMoviesFragment(
) : Fragment() {

    private lateinit var binding: FragmentWatchedMoviesBinding

    //val adapter = WatchedMoviesAdapter(Utils.watchedMovies)
    private var adapter = WatchedMoviesAdapter(mutableListOf(), ::onMovieClick)
    private val model = CinecartazRepository.getInstance()

    // Armazena a lista de resultados atuais.
    // Usado para ordenar os resultados da página.
    private var listOfCurrentResults = mutableListOf<WatchedMovie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_watched_movies, container, false)

        // attempt (!) to change app's title
        activity?.title = "Watched movies"

        binding = FragmentWatchedMoviesBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        //binding.radioBtnNone.isChecked = true
        //binding.radioBtn500.isChecked = false
        //binding.radioBtn1000.isChecked = false
        //binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

        binding.btnFilter.setOnClickListener {
            searchForResults()
        }


        CoroutineScope(Dispatchers.IO).launch {
            model.getWatchedMovies { result ->
                if (result.isSuccess) {

                    val listOfWatchedMovies: List<WatchedMovie> = result.getOrDefault(mutableListOf())
                    adapter = WatchedMoviesAdapter(listOfWatchedMovies, ::onMovieClick)

                    // update UI thread with the list of watched movies
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.rvItemWatchedMovies.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvItemWatchedMovies.adapter = adapter
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        NavigationManager.popCurrentFragment(activity!!.supportFragmentManager)
                    }
                }
            }
        }
    }


    //TODO - test invocation of movie details, from click in movie list (activity<-fragment<-adapter)
    private fun onMovieClick(watchedMovieUUID: String) {
        Log.i("RMata", watchedMovieUUID)

        // invoque
        activity?.supportFragmentManager?.let {
            NavigationManager.goToWatchedMovieDetailsFragment(
                it,
                watchedMovieUUID
            )
        }
        // invoque movie detail fragment
        activity?.supportFragmentManager?.let { NavigationManager.goToWatchedMovieDetailsFragment(it, watchedMovieUUID) }

    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance() = WatchedMoviesFragment()
    }

    private fun searchForResults() {

        CoroutineScope(Dispatchers.IO).launch {
            val searchTerm: String? = binding.etMovieName.text?.toString()
            var listOfWatchedMovies: List<WatchedMovie>

            // #1 - obter os filmes que correspondem ao nome de pesquisa
            if (!searchTerm.isNullOrEmpty()) {
                //
                // Vamos querer filtrar os resultados pelo nome
                //
                model.getWatchedMovies { result ->
                    if (result.isSuccess) {
                        listOfWatchedMovies = result.getOrDefault(mutableListOf())

                    }
                }


            } else {
                //
                // Vamos querer usar todos os filmes inseridos
                //
                model.getWatchedMoviesWithTitleLike(searchTerm!!) { result ->
                    if (result.isSuccess) {
                        listOfWatchedMovies = result.getOrDefault(mutableListOf())

                    }
                }


            }

            // #2 - dentro dos filmes obtidos, filtrar por distância
        }

    }


}