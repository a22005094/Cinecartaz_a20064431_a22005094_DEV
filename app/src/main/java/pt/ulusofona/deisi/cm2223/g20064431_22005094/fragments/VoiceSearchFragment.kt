package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.INTENT_VOICE_SEARCH
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentVoiceSearchBinding

// Créditos pelo código-base (e investigação sobre o tema):
// > https://developer.android.com/reference/android/app/DialogFragment
// > https://stackoverflow.com/questions/7977392/android-dialogfragment-vs-dialog

class VoiceSearchFragment : DialogFragment() {
    private val model = CinecartazRepository.getInstance()
    private lateinit var binding: FragmentVoiceSearchBinding

    // TODO use me!
    // private lateinit var voiceInputLauncher: ActivityResultLauncher<String>     // p/ botão "Selecionar Fotos"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Instanciar objeto Launcher p/ lançamento da Activity de Pesquisa por Voz.
        // É também registada a Callback a executar c/ o resultado da Activity, i.e., os URIs das Imgs selecionadas.
        //
        //  voiceInputLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        //   handleSelectedPictures(it)
        //  }

        // *** ViewBinding ***
        // "Inflate the layout for this fragment"
        val view = inflater.inflate(R.layout.fragment_voice_search, container, false)
        binding = FragmentVoiceSearchBinding.bind(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO doesn't work :(
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        binding.btnVoiceInput.setOnClickListener {
            performVoiceSearch()
        }

        binding.btnSubmit.setOnClickListener {

        }
    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance() = VoiceSearchFragment()
    }

    private fun performVoiceSearch() {
        // --------------------------------------------------------------------------
        // VOICE SEARCH - PASSO #1 - INICIAR ACTIVITY C/ O INTENT PARA REGISTO DE VOZ
        // --------------------------------------------------------------------------

        // Os passos aqui aplicados foram obtidos de documentação oficial @:
        // https://developer.android.com/training/wearables/user-input/voice

        // "Create an intent that can start the Speech Recognizer activity"
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            }

        // This starts the activity and populates the intent with the speech text.
        activity?.startActivityForResult(intent, INTENT_VOICE_SEARCH)
    }

}


/*

 ******************************
 Versão 1 - AlertDialog direto.
 ******************************

*** A Desvantagem aqui é que perdemos quaisquer acessos a estados... countDown de tempo... acesso a ViewBinding, e etc ***

    val title: String = "Pesquisar por voz um filme visualizado"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title: String = "Pesquisar por voz um filme visualizado"

        return AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_speak)
            .setTitle(title)
            .setPositiveButton(R.string.lbl_search) { dialog, whichButton ->
                // TODO
                Toast.makeText(context, "Teste positive button", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.lbl_cancel) { dialog, whichButton ->
                // TODO
                Toast.makeText(context, "Teste negative button", Toast.LENGTH_SHORT).show()
                //(getActivity() as FragmentAlertDialog).doNegativeClick()
            }
            .create()
    }

*/