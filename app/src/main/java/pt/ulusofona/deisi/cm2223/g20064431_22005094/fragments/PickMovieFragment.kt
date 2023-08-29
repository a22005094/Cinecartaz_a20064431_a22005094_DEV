package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.MovieSearchResultAdapter
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentPickMovieBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils

// private const val ARG_PARAM1 = "param1"

class PickMovieFragment : Fragment() {
    private val model = CinecartazRepository.getInstance()
    private lateinit var binding: FragmentPickMovieBinding
    private val resultsAdapter = MovieSearchResultAdapter()

    // -------------------------------
    // * Para usar na "gestão de páginas" dos resultados obtidos nas pesquisas.
    private var maxPageNumber = 1
    private var currentPageNumber = 1
    // -------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Colocar o título do Fragmento na AppBar
        activity?.setTitle(R.string.feature_choose_movie)

        // "Inflate the layout for this fragment"
        val view = inflater.inflate(R.layout.fragment_pick_movie, container, false)
        binding = FragmentPickMovieBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        resetPageCountLabel()
        // Esconder inicialmente a textview de nr. resultados
        binding.tvInfoResults.visibility = View.INVISIBLE
        // Esconder também os botões p/ mudar de página nos resultados, e a label da página atual
        binding.btnLessResults.visibility = View.INVISIBLE
        binding.btnMoreResults.visibility = View.INVISIBLE
        binding.tvPageCount.visibility = View.INVISIBLE

        // Adapter para a RecycleView de resultados
        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter = resultsAdapter

        binding.btnSearch.setOnClickListener {
            // Efetuar pesquisa por Filme
            resetPageCountLabel()
            resetPageSettings()
            searchForMovie(binding.etMovieSearch.text.toString(), currentPageNumber)
        }

        binding.btnLessResults.setOnClickListener {
            goToPreviousPage()
        }

        binding.btnMoreResults.setOnClickListener {
            goToNextPage()
        }

        // (ignore & change accordingly)
        // TODO - Utils.selectedCinema = CinemasManager.listaCinemas.first()
    }

    private fun searchForMovie(movieName: String, pageNumberToSearch: Int) {
        if (movieName.isNotEmpty()) {

            CoroutineScope(Dispatchers.IO).launch {
                model.getMoviesByName(movieName, pageNumberToSearch) { apiResult ->
                    if (apiResult.isSuccess) {
                        // Obter dados da API, ou se não conseguir, default para s/ resultados
                        val resultsInfo = apiResult.getOrDefault(MovieSearchResultInfo(0, mutableListOf()))

                        if (resultsInfo.nrResults >= 1) {
                            // EXISTEM RESULTADOS!

                            // -------------------------------
                            // Gestão de páginas de resultados - verificar o número de resultados e adaptar...
                            if (resultsInfo.nrResults % 10 != 0) {
                                // Páginas não-dezenas (10, 20, 30, ...)
                                maxPageNumber = (resultsInfo.nrResults / 10) + 1
                            } else {
                                maxPageNumber = (resultsInfo.nrResults / 10)
                            }
                            // -------------------------------

                            // Apresentar resultados na RecycleView
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.rvResults.visibility = View.VISIBLE

                                binding.tvInfoResults.visibility = View.VISIBLE
                                binding.tvInfoResults.text =
                                    getString(R.string.lbl_results_count, "${resultsInfo.nrResults}")

                                binding.tvPageCount.visibility = View.VISIBLE
                                binding.tvPageCount.text =
                                    getString(R.string.lbl_page_count, "$pageNumberToSearch", "$maxPageNumber")

                                if (maxPageNumber > 1) {
                                    binding.btnLessResults.visibility = View.VISIBLE
                                    binding.btnMoreResults.visibility = View.VISIBLE
                                } else {
                                    binding.btnLessResults.visibility = View.INVISIBLE
                                    binding.btnMoreResults.visibility = View.INVISIBLE
                                }

                                resultsAdapter.updateItems(resultsInfo.movieResults)
                            }

                            /*  (OLD)
                                val result: String = listaFilmes.joinToString { movie ->
                                    movie.title
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(
                                        requireContext(),
                                        "Foram encontrados ${listaFilmes.size}: [$result]",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            */

                        } else {
                            // Sem resultados!
                            // Toast.makeText(requireContext(), "Sem resultados de pesquisa.", Toast.LENGTH_SHORT).show()

                            resetPageSettings()
                            Utils.currentlySelectedMovie = null

                            CoroutineScope(Dispatchers.Main).launch {
                                binding.tvPageCount.visibility = View.INVISIBLE
                                binding.tvInfoResults.visibility = View.VISIBLE
                                binding.tvInfoResults.text = getString(R.string.lbl_no_results_found)
                                binding.rvResults.visibility = View.GONE
                            }
                        }

                    } else {
                        // Erro na comunicação c/ API - apresentar msg erro (Toast)
                        Toast.makeText(requireContext(), apiResult.exceptionOrNull()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            // Erro - não foi inserido texto a pesquisar
            binding.etMovieSearch.error = getString(R.string.error_missing_text)
        }
    }

    private fun resetPageCountLabel() {
        binding.tvPageCount.text = getString(R.string.lbl_page_count, "$currentPageNumber", "$maxPageNumber")
    }

    private fun resetPageSettings() {
        maxPageNumber = 1
        currentPageNumber = 1
    }

    private fun goToPreviousPage() {
        if (currentPageNumber > 1) {
            currentPageNumber--
            searchForMovie(binding.etMovieSearch.text.toString(), currentPageNumber)
        }
    }

    private fun goToNextPage() {
        if (currentPageNumber >= maxPageNumber) {
            Toast.makeText(requireContext(), getString(R.string.warn_no_more_results), Toast.LENGTH_LONG).show()
        } else {
            currentPageNumber++
            searchForMovie(binding.etMovieSearch.text.toString(), currentPageNumber)
        }
    }

    // Factory
    companion object {
        @JvmStatic
        fun newInstance() = PickMovieFragment()
    }

}