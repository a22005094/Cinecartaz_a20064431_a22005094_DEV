package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import pt.ulusofona.deisi.cm2223.g20064431_22005094.NavigationManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.SelectedImageAdapter
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentRegisterWatchedMovieBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinema
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomDate
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.CinemasManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.ImageUtils.convertUriToByteArray
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils.getYesterdayDateInMillis
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils.isToday

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

// ------ Pendentes importantes ------
// TODO ao gravar um novo filme com sucesso, deve limpar o objeto global de CurrentlySelectedMovie

// TODO pede-se para usar a localização para pré-selecionar o Cinema +próximo (vale pontos na avaliação)

// TODO - em relação ao armazenamento da Data (Date vs Long), converter para Long antes de gravar
//        ou mandar gravar como Date na mesma, mas recorrer aos TypeConverters que existem no Room?
//        (parece ser fácil btw) - https://developer.android.com/training/data-storage/room/referencing-data

// TODO - nomes muito compridos ficam em várias linhas na EditText clicável para seleção de Filme...

// ------ em dúvida ------
// TODO (?) eliminar fotos anexadas OnLongPress em cima da ImageView? Requer uma forma de identificar univocamente cada imagem, para aceder à lista e remover apenas aquela em concreto...
// TODO (?) usar a pb_pesquisa_filme?

// ------ later stages ------
// TODO (UI) para o design da pagina, ler isto: https://developer.android.com/codelabs/basic-android-kotlin-training-polished-user-experience#0
// TODO (UX) usar loading circle enquanto espera pelos resultados da API


class RegisterWatchedMovieFragment : Fragment() {
    private val model = CinecartazRepository.getInstance()
    private lateinit var binding: FragmentRegisterWatchedMovieBinding

    // *** Parametros do formulário ***

    // Cinema
    private var selectedCinema: Cinema? = null

    // DatePicker
    private var selectedDate = CustomDate() // para o Date Picker de data de visualização do filme

    // Imagens
    // private lateinit var permissoesLauncher: ActivityResultLauncher<String>   // (OLD) p/ request permissoes se necessário
    private lateinit var openGalleryLauncher: ActivityResultLauncher<String>     // p/ botão "Selecionar Fotos"
    private val listOfSelectedImages: MutableList<CustomImage> = mutableListOf()
    private val selectedImagesAdapter = SelectedImageAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Colocar o título do Fragmento na AppBar
        // [v1 - Não funcionou] -> (requireActivity() as AppCompatActivity).supportActionBar?.title = R.string.calculator.toString()
        activity?.setTitle(R.string.feature_register_movie)

        // Instanciar objeto Launcher p/ lançamento da Activity de seleção de Imagens (@ Galeria).
        // É também registada a Callback a executar c/ o resultado da Activity, i.e., os URIs das Imgs selecionadas.
        openGalleryLauncher = registerForActivityResult(GetMultipleContents()) {
            handleSelectedPictures(it)
        }

        // *** ViewBinding ***
        // "Inflate the layout for this fragment"
        val view = inflater.inflate(R.layout.fragment_register_watched_movie, container, false)
        binding = FragmentRegisterWatchedMovieBinding.bind(view)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ------------------------------------------------------
        //  Definir lógica @ listeners, eventos, adapters... etc
        // ------------------------------------------------------

        // * Filme: a pesquisa e seleção é feita num novo Fragment à parte (PickMovieFragment)
        binding.etMovieName.setOnClickListener {
            NavigationManager.goToPickMovieFragment(parentFragmentManager)
        }

        // * Cinemas: atualizar a lista JSON (@ cinemas.json)
        // CinemasManager.updateListOfCinemas(requireContext())

        // * Cinemas: configurar adapter para a lista de Cinemas
        val arrayAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, CinemasManager.listOfCinemas
        )
        binding.actvCinemaName.setAdapter(arrayAdapter)

        // * Cinemas: listener para registar alterações ao Cinema selecionado
        binding.actvCinemaName.setOnItemClickListener { adapterView, _, position: Int, _ ->
            selectedCinema = adapterView.getItemAtPosition(position) as Cinema
        }

        // TODO - ao mudar o texto deveria limpar imediatamente o cinema atualmente selecionado
        // (se mudam o texto, estão a "dar forfeit" do valor que estaria selecionado...)
        // binding.actvCinemaName.addTextChangedListener { ... }

        // * Rating
        setRatingPickerColor()
        binding.sliderRating.addOnChangeListener { _, _, _ -> setRatingPickerColor() }

        // * Date Picker: predefine p/ Data de Ontem
        // (Só se podem inserir avaliações em datas passadas)
        if (isToday(selectedDate)) {
            // Validação para só alterar 1x a data para ontem
            // (protege de situações de múltiplos acessos ao mesmo fragmento, e.g. múltiplas pesquisas por filmes)
            selectedDate.addDays(-1)
        }
        binding.tvWatchDate.text = selectedDate.toString()
        binding.tvWatchDate.setOnClickListener { showDatePicker() }

        // * Image Picker & Adapter de imagens
        binding.btnAddPhoto.setOnClickListener { openImageGallery() }
        // Nota: a RecycleView é apresentada em formato Grid (mais apelativo para "galeria de imagens")
        binding.rvImages.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvImages.adapter = selectedImagesAdapter

        // * Btn gravar form
        binding.btnSubmit.setOnClickListener { submitForm() }
    }


    override fun onStart() {
        super.onStart()

        // -----------------
        //  *** OnStart ***
        // -----------------

        // Se um Filme foi selecionado, identificá-lo na EditText.
        // Isto ocorre em situações em que se acedeu ao menu de Seleção do Filme
        //  e depois se recuou novamente para este Fragmento (volta a executar a função)
        if (Utils.currentlySelectedMovie != null) {
            binding.etMovieName.setText(Utils.currentlySelectedMovie.toString())
        } else {
            binding.etMovieName.setText("")
        }

    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance() = RegisterWatchedMovieFragment()
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
            selectedDate.setDateTo(ano, mes, dia)

            // atualizar textView para a nova data
            binding.tvWatchDate.text = selectedDate.toString()
        }

        val dtPickDialog = DatePickerDialog(
            requireContext(),
            pickListener,
            selectedDate.getYear(),
            selectedDate.getMonth(),
            selectedDate.getDayOfMonth()
        )

        // No máximo, seleciona a data de ontem
        dtPickDialog.datePicker.maxDate = getYesterdayDateInMillis()
        dtPickDialog.show()
    }

    private fun openImageGallery() {
        // Lançar multi-image picker usando a Galeria.

        // 1. Verificar se a permissão de acesso ao Storage já foi fornecida pelo Utilizador.
        // Se a permissão para aceder ao Storage ainda não foi fornecida... tentar solicitar de novo ao utilizador
        permissionsBuilder(Manifest.permission.READ_EXTERNAL_STORAGE).build().send { result ->
            // Mostrar msg Rationale caso rejeite a 1ª vez ou para sempre

            if (result.allGranted()) {
                // OK! Permissões já aceites pelo utilizador - lançar a activity p/ selecionar Imagens.
                // "Pass in the MIME type you want to let the user select, as the input" -> neste caso, apenas Imagens

                // permissoesLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                openGalleryLauncher.launch("image/*")
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

    private fun handleSelectedPictures(listOfImageUris: MutableList<Uri>) {
        // Por cada imagem selecionada pelo Utilizador:
        // - gerar objeto Bitmap (p/ apresentar na "Galeria" de imagens selecionadas)
        // - todo: Resize & Compress image?
        // - guardar na lista de imgs selecionadas
        // - notificar a RecycleView (... adapter!) sobre a alteração de itens

        if (listOfImageUris.isNotEmpty()) {
            for (imgUri in listOfImageUris) {
                try {
                    // v1 - (old) Versão Bitmap
                    /*
                     val bitmapImg = convertUriToBitmap(imgUri, requireActivity().contentResolver)
                     bitmapImg?.let {
                         // Adicionar a nova imagem à lista
                         listOfSelectedImages.add(bitmapImg)
                         // Atualiza itens @Adapter & notifica da alteracao
                         selectedImagesAdapter.updateItems(listOfSelectedImages)
                     }
                    */

                    // v2 - usa uma classe nossa, CustomImage, mais adequada às necessidades
                    val imgBytes: ByteArray? = convertUriToByteArray(imgUri, requireActivity().contentResolver)
                    imgBytes?.let {
                        // Adicionar a nova imagem à lista
                        // NOTA: Para já, fica com uma base de CustomImage apenas c/ ByteArray de dados.
                        //       O objeto CustomImage será finalizado ao gravar o Filme, associando a imagem a este registo.
                        listOfSelectedImages.add(
                            CustomImage(
                                refId = "",
                                imageName = imgUri.path.toString(),
                                imageData = imgBytes
                            )
                        )
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

            // Após processar todas as imagens selecionadas, atualizar os itens
            // na lista do Adapter da RecycleView, e notificar sobre as alterações feitas (atualizar items @RecycleView)
            selectedImagesAdapter.updateItems(listOfSelectedImages)
        }
    }

    private fun submitForm() {
        // Validar inputs e mostrar msgs Erro se faltarem dados

        // É obrigatório preencher o Filme e Cinema.
        val missingValuesList = mutableListOf<String>()

        if (Utils.currentlySelectedMovie == null) {
            // Falta indicar o Filme
            binding.etMovieName.error = "ERRO: Deve selecionar um Filme!"
            // Adicionar campo em falta para mensagem de erro (translation-aware)
            missingValuesList.add(getString(R.string.lbl_movie))
        }

        if (selectedCinema == null) {
            binding.actvCinemaName.error = "ERRO: Deve selecionar um Cinema!"
            // Adicionar campo em falta para mensagem de erro (translation-aware)
            missingValuesList.add(getString(R.string.lbl_cinema))
        }

        if (missingValuesList.isNotEmpty()) {
            // Há erros na página, impedir gravação e mostrar msg ao utilizador

            val sb = StringBuilder()
            sb.append(getString(R.string.error_missing_required_data)).append("\n")
            missingValuesList.forEach { sb.append("- $it").append("\n") }

            Toast.makeText(requireContext(), sb.toString(), Toast.LENGTH_LONG).show()
        } else {
            // formulário OK - inserir WatchedMovie na BD

            // Informar user da operação
            Toast.makeText(requireContext(), "Saving...", Toast.LENGTH_SHORT).show()

            // Preparar modelo
            val newWatchedMovie = WatchedMovie(
                movie = Utils.currentlySelectedMovie!!,
                theatre = selectedCinema!!,
                review = binding.sliderRating.value.toInt(),
                date = selectedDate.toMillis(),
                comments = binding.etObservations.text.toString()
            )

            // Fotos: preencher fotos anexadas com o RefId do novo WatchedMovie (p/ associar as imagens a este registo)
            // e atribuir a lista de fotos
            listOfSelectedImages.forEach { customImage ->
                customImage.refId = newWatchedMovie.uuid
            }
            newWatchedMovie.photos = listOfSelectedImages

            CoroutineScope(Dispatchers.IO).launch {
                model.insertWatchedMovie(newWatchedMovie) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Data inserted!", Toast.LENGTH_SHORT).show()

                        // TODO - o que fazer depois, ao inserir com sucesso?
                        binding.btnSubmit.isEnabled = false
                        binding.btnSubmit.text = getString(R.string.lbl_movie_saved)
                    }
                }
            }


        }
    }

}
