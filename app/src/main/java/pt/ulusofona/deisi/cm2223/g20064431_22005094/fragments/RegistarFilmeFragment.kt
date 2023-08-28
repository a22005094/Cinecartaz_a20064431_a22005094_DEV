package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.Manifest
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.allPermanentlyDenied
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentRegistarFilmeBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinema
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomDate
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.CinemasManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils.convertUriToBitmap
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils.getDataDeOntemEmMillis


/*  CREDITS

  - Date picker (função showDatePicker()):
    > ChatGPT c/ query "Please show me a DatePickerDialog demo on Android, using Kotlin and API 23"

  - Gallery picker no Android:
    > https://developer.android.com/training/basics/intents/result#kotlin
    > Sobre permissões:
       > https://developer.android.com/training/permissions/requesting
       > https://stackoverflow.com/questions/66551781/android-onrequestpermissionsresult-is-deprecated-are-there-any-alternatives

  - Gestão de imagens selecionadas em formato lista:
    > Ficha Prática de RecyclerView
    > ChatGPT query, semelhante com "Please show me a Android API 23 recycler view demo for displaying selected images"

*/

// TODO ver short notepad de pendentes
// TODO usar a pb_pesquisa_filme
// TODO fazer uso da library kpermissions, e adaptar acessos a permissões aqui
// TODO verificar se o User deu permanently deny à permissão -- o botão nao vai fazer nada... deveria talvez mostrar um Toast (DIFÍCIL)

// ------ em dúvida ------
// TODO (?) eliminar fotos anexadas OnLongPress em cima da ImageView? Requer uma forma de identificar univocamente cada imagem, para aceder à lista e remover apenas aquela em concreto...
// ------ later stages ------
// TODO (UI) para o design da pagina, ler isto: https://developer.android.com/codelabs/basic-android-kotlin-training-polished-user-experience#0
// TODO (UX) usar loading circle enquanto espera pelos resultados da API
// TODO (PENDENTE) opcionalmente, pede-se para usar a localização para pré-selecionar o Cinema +próximo (vale pontos na avaliação)


class RegistarFilmeFragment : Fragment() {

    // private val model: CinecartazOkHttp = CinecartazOkHttp()
    private val model = CinecartazRepository.getInstance()

    // TODO rever estes dois parametros...
    private var filmeSelecionado: Int = -1
    private var cinemaSelecionado: Int = -1

    // ---

    private lateinit var binding: FragmentRegistarFilmeBinding

    // (@ datePicker)
    private var dtSelecionada = CustomDate() // para o Date Picker de data de visualização do filme

    // (@ seleção de Imagens)
    // private lateinit var permissoesLauncher: ActivityResultLauncher<String>   // (OLD) p/ request permissoes se necessário
    private lateinit var carregarFotoLauncher: ActivityResultLauncher<String> // p/ botão "Selecionar Fotos"
    private var listaImagens: MutableList<Bitmap> = mutableListOf()
    private val imagensAdapter = ImagemAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Colocar o título do Fragmento na AppBar
        // [v1 - Não funcionou] -> (requireActivity() as AppCompatActivity).supportActionBar?.title = R.string.calculator.toString()
        activity?.setTitle(R.string.feature_register_movie)

        // Instanciar objeto Launcher p/ lançamento da Activity de seleção de Imagens (@ Galeria).
        // É também registada a Callback a executar c/ o resultado da Activity, i.e., os URIs das Imgs selecionadas.
        carregarFotoLauncher = registerForActivityResult(GetMultipleContents()) {
            processarImagensSelecionadas(it)
        }

        // *** ViewBinding ***
        // "Inflate the layout for this fragment"
        val view = inflater.inflate(R.layout.fragment_registar_filme, container, false)
        binding = FragmentRegistarFilmeBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // ------------------------------------------------------
        //  Definir lógica @ listeners, eventos, adapter... etc.
        // ------------------------------------------------------
        // > Number picker
        binding.numberPicker.minValue = 1
        binding.numberPicker.maxValue = 10
        binding.numberPicker.value = 5

        // > Date Picker - predefine p/ data de ontem (só se podem inserir avaliações em datas passadas)
        dtSelecionada.adicionarDias(-1)
        binding.tvWatchDate.text = dtSelecionada.toString()

        // > Adapter de imagens
        // v1 - Método semelhante às fichas, formato Lista
        // binding.rvImagens.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // v2 - Formato Grid (mais apelativo para uma "galeria")
        binding.rvImagens.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvImagens.adapter = imagensAdapter

        // Listeners das EditTexts - a pesquisa é feita no momento em que as EditTexts perdem o foco
        // (exemplo: quando o User seleciona fora da caixa após escrever texto, ou seleciona outra caixa)
        binding.etMovieName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Perdeu focus - efetuar pesquisa por Filme
                pesquisarFilme(binding.etMovieName.text.toString())
            }
        }

        binding.etCinemaName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Perdeu focus - efetuar pesquisa por Cinema
                pesquisarCinema(binding.etCinemaName.text.toString())
            }
        }


        binding.btnMinus.setOnClickListener { decrementarRating() }
        binding.btnPlus.setOnClickListener { incrementarRating() }
        setRatingPickerColor()
        binding.sliderRating.addOnChangeListener { _, _, _ -> setRatingPickerColor() }

        binding.tvWatchDate.setOnClickListener { showDatePicker() }
        binding.btnAddPhoto.setOnClickListener { lancarPickerImagens() }
        binding.btnInsertRecord.setOnClickListener { gravarFilme() }

        // atualizar a lista de filmes (cinemas.json)
        CinemasManager.atualizarListaCinemas(requireContext())

        // -------------------
        // Zona para testes...
        // -------------------


    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance() = RegistarFilmeFragment()
    }

    private fun decrementarRating() {
        binding.numberPicker.value -= 1
    }

    private fun incrementarRating() {
        binding.numberPicker.value += 1
    }

    private fun setRatingPickerColor() {
        val value = binding.sliderRating.value
        binding.sliderText.text = "${value.toInt()}"
        val stateList = arrayOf(intArrayOf(android.R.attr.state_active))

        if (value < 3) {
            binding.sliderRating.trackActiveTintList = ColorStateList(
                stateList, intArrayOf(ContextCompat.getColor(requireContext(), R.color.rating_step1))
            )
        } else if (value < 5) {
            binding.sliderRating.trackActiveTintList = ColorStateList(
                stateList, intArrayOf(ContextCompat.getColor(requireContext(), R.color.rating_step2))
            )
        } else if (value < 7) {
            binding.sliderRating.trackActiveTintList = ColorStateList(
                stateList, intArrayOf(ContextCompat.getColor(requireContext(), R.color.rating_step3))
            )
        } else if (value < 9) {
            binding.sliderRating.trackActiveTintList = ColorStateList(
                stateList, intArrayOf(ContextCompat.getColor(requireContext(), R.color.rating_step4))
            )
        } else {
            binding.sliderRating.trackActiveTintList = ColorStateList(
                stateList, intArrayOf(ContextCompat.getColor(requireContext(), R.color.rating_step5))
            )
        }
    }

    private fun showDatePicker() {
        // Listener para registar mudanças à data selecionada no Picker
        val pickListener = DatePickerDialog.OnDateSetListener { _, ano, mes, dia ->
            // [DEBUG] Toast.makeText(requireContext(), "Selecionou ${dia}/${mes}/${ano}...", Toast.LENGTH_SHORT).show()

            // atualizar data selecionada na variável
            dtSelecionada.alterarDataPara(ano, mes, dia)

            // atualizar textView para a nova data
            binding.tvWatchDate.text = dtSelecionada.toString()
        }

        val dtPickDialog = DatePickerDialog(
            requireContext(), pickListener, dtSelecionada.getAno(), dtSelecionada.getMes(), dtSelecionada.getDia()
        )

        // No máximo, seleciona a data de ontem
        dtPickDialog.datePicker.maxDate = getDataDeOntemEmMillis()
        dtPickDialog.show()
    }

    private fun lancarPickerImagens() {
        // Lançar multi-image picker usando a Galeria.

        // 1. Verificar se a permissão de acesso ao Storage já foi fornecida pelo Utilizador.
        // Se a permissão para aceder ao Storage ainda não foi fornecida... tentar solicitar de novo ao utilizador
        permissionsBuilder(Manifest.permission.READ_EXTERNAL_STORAGE).build().send { result ->
            // Mostrar msg Rationale caso rejeite a 1ª vez ou para sempre

            if (result.allGranted()) {
                // OK! Permissões já aceites pelo utilizador - lançar a activity p/ selecionar Imagens.
                // "Pass in the MIME type you want to let the user select, as the input" -> neste caso, apenas Imagens

                // permissoesLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                carregarFotoLauncher.launch("image/*")
            } else if (result.allPermanentlyDenied()) {
                // Permissão rejeitada permanentemente - mostrar Rationale
                Toast.makeText(
                    requireContext(), R.string.rationale_rejected_permanently_permission_storage, Toast.LENGTH_LONG
                ).show()
            } else {
                // Permissão rejeitada - mostrar Rationale
                Toast.makeText(requireContext(), R.string.rationale_rejected_once_permission_storage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun processarImagensSelecionadas(listaUriImagens: MutableList<Uri>) {
        // Por cada imagem selecionada pelo Utilizador:
        // - gerar objeto Bitmap
        // - todo: Resize the image
        // - todo: Compress the image
        // - guardar na lista local de imagens selecionadas
        // - notificar a RecycleView (... adapter!) sobre a alteração de itens

        for (uriImagem in listaUriImagens) {
            try {
                // v1 - Erro...
                //   val bitmapImg = BitmapFactory.decodeFile(uriImagem.path)
                // v2 - Deprecated... (https://stackoverflow.com/questions/68840221/kotlin-how-to-convert-image-uri-to-bitmap)
                //   val src = ImageDecoder.createSource(requireActivity().contentResolver, )
                //   val imgSrc = ImageDecoder.createSource(requireActivity().contentResolver, uriImagem)
                //   val bitmapImg = ImageDecoder.decodeBitmap(source)

                val bitmapImg = convertUriToBitmap(uriImagem, requireActivity().contentResolver)
                bitmapImg?.let {
                    // Adicionar a nova imagem à lista
                    listaImagens.add(bitmapImg)
                    // Atualiza itens @Adapter & notifica da alteracao
                    imagensAdapter.updateItems(listaImagens)
                }
            } catch (exc: Exception) {
                // Vai continuar a processar as imagens seguintes (se houver),
                // mas a imagem onde gerou exceção poderá não ter sido inserida corretamente.
                exc.printStackTrace()
                Toast.makeText(
                    requireContext(), getString(R.string.exception_add_image), Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun pesquisarFilme(nomeFilme: String) {
        if (nomeFilme.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                model.getMoviesByName(nomeFilme) { apiResult ->
                    if (apiResult.isSuccess) {
                        val listaFilmes = apiResult.getOrDefault(mutableListOf())

                        if (listaFilmes.isEmpty()) {
                            Toast.makeText(requireContext(), "Sem resultados de pesquisa.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Todos os filmes
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
                        }
                    } else {
                        // Erro na comunicação c/ API - apresentar msg erro (Toast)
                        Toast.makeText(requireContext(), apiResult.exceptionOrNull()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            // Erro - não foi inserido texto a pesquisar
            binding.etMovieName.error = getString(R.string.error_missing_text)
        }
    }

    private fun pesquisarCinema(txtPesquisa: String) {
        // TODO poderá precisar de retoques...

        // Pesquisa por um Cinema usando o texto da caixa.
        // Apenas seleciona um cinema se só um resultado foi devolvido, e assinala a caixa a verde.
        // Caso contrário devolve erro e assinala a caixa a vermelho (input inválido/0 matches/1+ matches)

        var cinemaEncontrado: Cinema? = null
        var pesquisaSucesso = false
        var resultMsg = ""

        if (txtPesquisa.isNotEmpty()) {
            var nrMatches = 0
            val textoAPesquisar = txtPesquisa.trim().lowercase()

            if (CinemasManager.listaCinemas.size > 0) {
                for (cinema in CinemasManager.listaCinemas) {
                    // A pesquisa é feita diretamente por cinemas que tenham o mesmo texto (igual e na mesma ordem).
                    // TODO: sugestão de melhoria: separar todas as palavras e fazer "ranking" por maior número de matches?
                    if (cinema.name.lowercase().contains(textoAPesquisar)) {
                        cinemaEncontrado = cinema
                        nrMatches++
                    }
                }

                // Possíveis resultados:
                // > 0  Matches = ERROR! Nenhum resultado encontrado
                // > 1  Match   = OK
                // > 2+ Matches = ERROR! Tem de ser mais específico...

                if (nrMatches == 0) {  // ERROR
                    resultMsg = getString(R.string.error_no_results_found)
                } else if (nrMatches > 1) {
                    resultMsg = getString(R.string.error_too_many_results)
                } else {
                    pesquisaSucesso = true  // OK!
                    resultMsg = ""  // Não vai ser apresentada msg Toast
                }
            } else {
                resultMsg = getString(R.string.error_internal_error)
            }
        } else {
            // Erro - não foi inserido texto a pesquisar
            resultMsg = getString(R.string.error_missing_text)
        }

        if (pesquisaSucesso) {
            cinemaSelecionado = cinemaEncontrado!!.id
            binding.etCinemaName.setText(cinemaEncontrado.name)
            binding.etCinemaName.setBackgroundResource(R.drawable.edit_text_input_success)
        } else {
            cinemaSelecionado = -1
            binding.etCinemaName.setBackgroundResource(R.drawable.edit_text_input_error)
            binding.etCinemaName.error = resultMsg
        }

        if (resultMsg.isNotEmpty()) {
            Toast.makeText(requireContext(), resultMsg, Toast.LENGTH_LONG).show()
        }
    }

    private fun gravarFilme() {
        // validar que todos os campos devidos estão preenchidos... mas não esquecer de tratar todos, inclusive os opcionais

    }

}
