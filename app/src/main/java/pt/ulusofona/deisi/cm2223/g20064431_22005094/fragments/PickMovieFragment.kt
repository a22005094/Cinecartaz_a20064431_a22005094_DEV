package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.MovieSearchResultAdapter
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentPickMovieBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils.closeKeyboard
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// private const val ARG_PARAM1 = "param1"

// TODO - confirmar se ainda está a crashar quando está sem Net...

class PickMovieFragment : Fragment() {
    private val model = CinecartazRepository.getInstance()
    private lateinit var binding: FragmentPickMovieBinding
    private val resultsAdapter = MovieSearchResultAdapter(::onMovieClick)

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
        // Esconder inicialmente as textview sobre resultados
        binding.tvInfoResults.visibility = View.INVISIBLE
        binding.tvNoResults.visibility = View.GONE
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

            // Pesquisar
            searchForMovie(binding.etMovieSearch.text.toString(), currentPageNumber)

            // Fechar o teclado Android
            closeKeyboard(it)
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

                            // PARTE 1 - Adaptar interface para resultados da API (mas ainda sem informação sobre os Filmes)

                            // Apresentar resultados na RecycleView
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.rvResults.visibility = View.VISIBLE
                                binding.tvNoResults.visibility = View.GONE
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
                            }

                            // PARTE 2 - resultados convertidos para OMDBMovies

                            // Tendo recebido a lista de IMDB_ID (string) dos Filmes encontrados,
                            // converter cada resultado no seu objeto OMDBMovie com detalhes

                            // ** NOTA ** - TODO referenciar isto no Readme
                            //  A utilização de múltiplas chamadas à API a partir de uma lista inicial de valores
                            //  foi um tema que ofereceu alguma dificuldade, por existir um FOR loop (síncrono)
                            //  em conjunto com chamadas à API (assíncronas), que exigiu um grau maior de cuidado
                            //  em garantir que todas as chamadas estão concluídas no Loop antes de executar mais instruções
                            //  pós-carregamentos via API (para, por ex., atualizar o adapter com a lista final de resultados).
                            //  Surgia o problema de que o FOR, logo na 1ª iteração, lançava o pedido à API e logo a seguir
                            //  saltava imediatamente para as instruções seguintes ao Loop, provocando resultados indesejados
                            //  como o update ao Adapter de filmes sem ter ainda quaisquer items inseridos (nem sequer
                            //  esperava por completar a 1ª chamada à API... mas depois continuava a emitir os pedidos em background)
                            //
                            //  Inicialmente, fez-se uma leitura de capítulos sobre processamento assíncrono em Kotlin
                            //  na documentação oficial, tentando, por exemplo, recorrer ao uso de um Flow e recolher dados com
                            //  collect {} (https://kotlinlang.org/docs/flow.html), mas não devolveu os resultados esperados.
                            //  Eventualmente foi resolvido, mas com queries ao ChatGPT, interrogando sobre o tema de execução de
                            //  chamadas à API e a necessidade de aguardar pela finalização de todas as chamadas async esperadas,
                            //  antes de prosseguir com operações seguintes. O código aqui resultante surge então com
                            //  apoio de uma Demo fornecida pela ferramenta.
                            //

                            CoroutineScope(Dispatchers.IO).launch {
                                var listOfOmdbMovies = mutableListOf<OMDBMovie>()
                                val deferredResults = mutableListOf<Deferred<Unit>>()

                                for (imdbMovieId in resultsInfo.movieResults) {
                                    val deferred = async {
                                        suspendCoroutine<Unit> { continuation ->
                                            model.getMovieDetailsByImdbId(imdbMovieId) { result ->
                                                if (result.isSuccess) {
                                                    val omdbMovie: OMDBMovie? = result.getOrNull()

                                                    omdbMovie?.let {
                                                        listOfOmdbMovies.add(omdbMovie)
                                                    }
                                                }
                                                continuation.resume(Unit)
                                            }
                                        }
                                    }
                                    deferredResults.add(deferred)
                                }

                                // Await all deferred results
                                deferredResults.awaitAll()

                                // NEW! Ordenar os resultados por ordem decrescente do Ano.
                                listOfOmdbMovies =
                                    listOfOmdbMovies.sortedByDescending { movie -> movie.year }.toMutableList()

                                CoroutineScope(Dispatchers.Main).launch {
                                    resultsAdapter.updateItems(listOfOmdbMovies)
                                }
                            }
                            // --------------


                        } else {
                            // Sem resultados!
                            // Toast.makeText(requireContext(), "Sem resultados de pesquisa.", Toast.LENGTH_SHORT).show()

                            resetPageSettings()
                            Utils.currentlySelectedMovie = null

                            CoroutineScope(Dispatchers.Main).launch {
                                binding.tvPageCount.visibility = View.INVISIBLE
                                binding.tvInfoResults.visibility = View.INVISIBLE
                                binding.tvNoResults.visibility = View.VISIBLE
                                binding.rvResults.visibility = View.GONE
                                binding.btnLessResults.visibility = View.INVISIBLE
                                binding.btnMoreResults.visibility = View.INVISIBLE
                            }
                        }

                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Erro na comunicação c/ API - apresentar msg erro (Toast)
                            Toast.makeText(requireContext(), apiResult.exceptionOrNull()?.message, Toast.LENGTH_LONG)
                                .show()
                        }
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


    private fun onMovieClick(movie: OMDBMovie) {
        //  A utilizar no Adapter.
        //  Serve para registar seleção do filme que foi selecionado neste Fragmento
        //  e recuar para o menu anterior, já com esta seleção feita.

        // * Selecionar o filme
        Utils.currentlySelectedMovie = movie

        // * Recuar para a página anterior
        requireActivity().supportFragmentManager.popBackStack()
    }

}