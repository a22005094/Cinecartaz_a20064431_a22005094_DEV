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
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils


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

        binding.radioDistanceNa.isChecked = true
        binding.radioDistance500.isChecked = false
        binding.radioDistance1000.isChecked = false

        binding.sortAscend.isChecked = false
        binding.sortDescend.isChecked = true

        CoroutineScope(Dispatchers.IO).launch {
            model.getWatchedMovies { result ->
                if (result.isSuccess) {

                    var listOfWatchedMovies: MutableList<WatchedMovie> =
                        result.getOrDefault(mutableListOf()).toMutableList()

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
        activity?.supportFragmentManager?.let {
            NavigationManager.goToWatchedMovieDetailsFragment(
                it,
                watchedMovieUUID
            )
        }

    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance() = WatchedMoviesFragment()
    }

    private fun searchForResults() {
        // Reset nos resultados atuais
        listOfCurrentResults = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {
            val searchTerm: String? = binding.etMovieName.text?.toString()
            var listOfWatchedMovies: MutableList<WatchedMovie>


            // Verificar se foi aplicado algum tipo de filtro de distancia
            var distancia = Int.MAX_VALUE
            if (binding.radioDistance500.isChecked) {
                distancia = 500
            } else if (binding.radioDistance1000.isChecked) {
                distancia = 1000
            }

            // Ordenação: asc ou desc?
            var sortAscending = (binding.sortAscend.isChecked)


            // #1 - obter os filmes que correspondem ao nome de pesquisa
            if (searchTerm.isNullOrEmpty()) {
                //
                // Vamos querer usar todos os filmes inseridos
                //
                model.getWatchedMovies { result ->
                    if (result.isSuccess) {
                        listOfWatchedMovies = result.getOrDefault(mutableListOf()).toMutableList().toMutableList()

                        // calculate distances
                        listOfWatchedMovies =
                            Utils.calcDistancesOnMoviesList(listOfWatchedMovies, Utils.lastKnownLocation)

                        // filter
                        var listAux = mutableListOf<WatchedMovie>()
                        for (watched in listOfWatchedMovies) {
                            if (watched.calcDistance < distancia) {
                                listAux.add(watched)
                            }
                        }

                        // No final, passar os resultados finais para o objeto global neste fragmento, "listOfCurrentResults"
                        listOfCurrentResults = listAux

                        if (sortAscending) {
                            listOfCurrentResults.sortBy { it.movie.ratingImdb }

                        } else {
                            listOfCurrentResults.sortByDescending { it.movie.ratingImdb }
                        }

                        CoroutineScope(Dispatchers.Main).launch { adapter.updateItems(listOfCurrentResults) }
                    }
                }


            } else {
                //
                // Vamos querer filtrar os resultados pelo nome
                //
                model.getWatchedMoviesWithTitleLike(searchTerm) { result ->
                    if (result.isSuccess) {
                        listOfWatchedMovies = result.getOrDefault(mutableListOf()).toMutableList()

                        // calculate distances
                        listOfWatchedMovies =
                            Utils.calcDistancesOnMoviesList(listOfWatchedMovies, Utils.lastKnownLocation)

                        // filter
                        var listAux = mutableListOf<WatchedMovie>()
                        for (watched in listOfWatchedMovies) {
                            if (watched.calcDistance < distancia) {
                                listAux.add(watched)
                            }
                        }

                        // No final, passar os resultados finais para o objeto global neste fragmento, "listOfCurrentResults"
                        listOfCurrentResults = listAux

                        if (sortAscending) {
                            listOfCurrentResults.sortBy { it.movie.ratingImdb }

                        } else {
                            listOfCurrentResults.sortByDescending { it.movie.ratingImdb }
                        }

                        CoroutineScope(Dispatchers.Main).launch { adapter.updateItems(listOfCurrentResults) }
                    }
                }

            }

        }

    }


}