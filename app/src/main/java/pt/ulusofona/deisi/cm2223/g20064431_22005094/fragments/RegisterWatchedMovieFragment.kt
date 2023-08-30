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
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentRegistarFilmeBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinema
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomDate
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.CinemasManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils.convertUriToBitmap
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils.currentlySelectedMovie
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


class RegisterWatchedMovieFragment : Fragment() {

    private val model = CinecartazRepository.getInstance()
    private lateinit var binding: FragmentRegistarFilmeBinding

    // TODO rever estes dois parametros...
    private var selectedMovie: Int = -1
    private var selectedCinema: Cinema? = null

    // (@ datePicker)
    private var selectedDate = CustomDate() // para o Date Picker de data de visualização do filme

    // (@ seleção de Imagens)
    // private lateinit var permissoesLauncher: ActivityResultLauncher<String>   // (OLD) p/ request permissoes se necessário
    private lateinit var openGalleryLauncher: ActivityResultLauncher<String>     // p/ botão "Selecionar Fotos"
    private var listOfSelectedImages: MutableList<Bitmap> = mutableListOf()
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
        val view = inflater.inflate(R.layout.fragment_registar_filme, container, false)
        binding = FragmentRegistarFilmeBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // ------------------------------------------------------
        //  Definir lógica @ listeners, eventos, adapter... etc.
        // ------------------------------------------------------

        // * Filme
        // Configurar listener - a pesquisa é feita no momento em que a EditText perde o foco
        // (exemplo: quando o User seleciona fora da caixa após escrever texto, ou seleciona outra caixa)
        // TODO - binding.etMovieName. ...
        binding.etMovieName.setOnClickListener {
            NavigationManager.goToPickMovieFragment(parentFragmentManager)
        }

        // * Cinema
        //  > Atualizar a lista JSON (@ cinemas.json)
        CinemasManager.updateListOfCinemas(requireContext())

        //  > Configurar adapter a usar c/ lista de Cinemas
        val arrayAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, CinemasManager.listOfCinemas
        )
        binding.actvCinemaName.setAdapter(arrayAdapter)

        //  > Listener para armazenar o Cinema selecionado
        binding.actvCinemaName.setOnItemClickListener { adapterView, _, position: Int, _ ->
            selectedCinema = adapterView.getItemAtPosition(position) as Cinema

            Toast.makeText(
                requireContext(), "Selected: ${adapterView.getItemAtPosition(position)}", Toast.LENGTH_SHORT
            ).show()
        }

        // * Rating
        setRatingPickerColor()
        binding.sliderRating.addOnChangeListener { _, _, _ -> setRatingPickerColor() }

        // * Date Picker - predefine p/ data de ontem
        // (só se podem inserir avaliações em datas passadas)
        if (isToday(selectedDate)) {
            // Validação para só alterar 1x a data para ontem
            // (protege de situações de múltiplos acessos ao mesmo fragmento, e.g. múltiplas pesquisas por filmes)
            selectedDate.addDays(-1)
        }

        binding.tvWatchDate.text = selectedDate.toString()
        binding.tvWatchDate.setOnClickListener { showDatePicker() }

        // * Image Picker & Adapter de imagens
        binding.btnAddPhoto.setOnClickListener { openImageGallery() }

        // A RecycleView é apresentada em formato Grid (mais apelativo para "galeria de imagens")
        binding.rvImages.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvImages.adapter = selectedImagesAdapter

        // * Btn gravar
        binding.btnInsertRecord.setOnClickListener { saveWatchedMovie() }

        // ---------------------
        //  Zona para testes...
        // ---------------------

        if (currentlySelectedMovie != null) {
            Toast.makeText(
                requireContext(),
                "Selected movie: ${currentlySelectedMovie!!.title}",
                Toast.LENGTH_SHORT
            ).show()
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
        // - gerar objeto Bitmap
        // - todo: Resize the image
        // - todo: Compress the image
        // - guardar na lista local de imagens selecionadas
        // - notificar a RecycleView (... adapter!) sobre a alteração de itens

        for (imgUri in listOfImageUris) {
            try {
                // v1 - Erro...
                //   val bitmapImg = BitmapFactory.decodeFile(imgUri.path)
                // v2 - Deprecated... (https://stackoverflow.com/questions/68840221/kotlin-how-to-convert-image-uri-to-bitmap)
                //   val src = ImageDecoder.createSource(requireActivity().contentResolver, )
                //   val imgSrc = ImageDecoder.createSource(requireActivity().contentResolver, imgUri)
                //   val bitmapImg = ImageDecoder.decodeBitmap(source)

                val bitmapImg = convertUriToBitmap(imgUri, requireActivity().contentResolver)
                bitmapImg?.let {
                    // Adicionar a nova imagem à lista
                    listOfSelectedImages.add(bitmapImg)
                    // Atualiza itens @Adapter & notifica da alteracao
                    selectedImagesAdapter.updateItems(listOfSelectedImages)
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

    private fun isFormValid(): Boolean {
        return false
    }

    private fun saveWatchedMovie() {
        // validar que todos os campos devidos estão preenchidos... mas não esquecer de tratar todos, inclusive os opcionais

        if (isFormValid()) {
        } else {
        }

    }

}
