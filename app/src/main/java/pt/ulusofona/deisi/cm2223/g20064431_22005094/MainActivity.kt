package pt.ulusofona.deisi.cm2223.g20064431_22005094

// kpermissions and Manifest imports were required for location request
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ActivityMainBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.CinemasManager
import kotlin.math.sqrt

// Credits pela implementação-base desta Activity: Ficha5 prática de CM (material das Aulas)

// Credits - Voice search: https://developer.android.com/training/wearables/user-input/voice
private val INTENT_VOICE_SEARCH = 0

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val model = CinecartazRepository.getInstance()
    private lateinit var binding: ActivityMainBinding

    // * [Época recurso] - ACELERÓMETRO - Créditos:
    // 1) Documentação oficial: https://developer.android.com/guide/topics/sensors/sensors_motion
    // 2) ChatGPT: com a query "Using Android API 23 and Kotlin, can you tell me a quick way to detect if, at any time,
    //   the phone was shaken? I need to process some application logic whenever that happens." e a
    //   Query "And now, how can I add a time threshold between detections to allow some time between detections?"
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var lastTimestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão Pesquisar Filme por Voz
        binding.btnVoiceSearch.setOnClickListener {
            launchVoiceDialog("")
        }

        // Manage location permission request
        // If permissions are not granted, the app will exit
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).build().send { result ->
            if (result.allGranted()) {
                // if screen rotates, won't navigate to main fragment
                // and remains in current fragment
                if (!screenRotated(savedInstanceState))
                    NavigationManager.goToDashboardFragment(supportFragmentManager)
            } else {
                finish()
            }
        }

        // [RECURSO] - ACELERÓMETRO: inicializar variáveis de apoio (acesso a leituras do Acelerómetro)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStart() {
        super.onStart()
        setSupportActionBar(binding.toolbar)
        setupDrawerMenu()

        CoroutineScope(Dispatchers.IO).launch {
            // * Carregar lista de Cinemas (cinemas.json)
            CinemasManager.updateListOfCinemas(applicationContext)

            // TODO: rever. Uma hipótese é fazer download apenas quando faz o registo do filme.
            // * Carregar lista de imagens dos Cinemas
            // CinemasManager.getCinemasImages()
        }
    }

    // -----------------------------------------------------------------------------
    // * RECURSO - utilização do Acelerómetro para detetar o "shake" do dispositivo
    // -----------------------------------------------------------------------------
    override fun onResume() {
        super.onResume()

        // * ACELERÓMETRO *
        // - Para situações em que se regressa à Activity, após por ex. um onRestart() ou onPause().
        // - Reativar "subscrição" do Listener a futuras alterações nos dados recolhidos por sensores (neste caso, o Acelerómetro).
        // - Verificou-se que os valores de SENSOR_DELAY já disponibilizados têm intervalos de tempo muito reduzidos (poucos microssegundos),
        //   portanto optou-se por um valor de sampling mais equilibrado relativamente aos acessos aos dados do Acelerómetro
        //   (não é absolutamente crítico verificar aqui "exatamente no instantâneo", mas também sem intervalos com delays muito excessivos).
        //   Assim, ajuda também q.b. na poupança de recursos ((e bateria)), ao reduzir processamento que será inerente aos dados lidos do Acelerómetro.
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()

        // * ACELERÓMETRO *
        // - Despoletado, por exemplo, quando esta Activity passa para Background
        //   (o que aqui será à partida até raro, pois tudo (Fragmentos de ecrãs, etc.) dependem desta MainActivity).
        // - Suspender a "subscrição" ao Listener de mudanças nos Sensores (neste caso, apenas do Acelerómetro).
        // - É uma possível abordagem para poupar bateria do dispositivo, ao tentar reduzir o tempo em que
        //    se está subscrito a Listeners, sem que, na realidade, vá ter tempo útil de uso.
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val timestampNow = System.currentTimeMillis()

            // Enquanto a variável [lastTimestamp] não for preenchida numa primeira deteção, vai sempre entrar
            //  neste bloco abaixo para leitura e deteção se houve o abanar do dispositivo.
            //  (assim, quando for feito o primeiro shake e o valor no acelerómetro seja lido, faz trigger imediatamente na primeira vez).
            // Mediante deteção de cada "shake" do dispositivo, é que então se estabelece um período de tempo ajustável (@ TIME_MILLIS_BETWEEN_SHAKE_DETECTIONS)
            //  entre a próxima leitura ao acelerómetro que deve ser utilizada -- e desta forma, só a partir desse tempo decorrido é que volta
            //  a tentar calcular a aceleração, e verificar se ocorreu novos "shakes".

            if (event.sensor == accelerometer) {
                // Só irá verificar o Shake do dispositivo (e possivelmente lançar o fragmento de Registar Filme)
                // caso não esteja já a ser atualmente apresentado
                if (!NavigationManager.isCurrentlyAtRegisterWatchedMovieFragment(supportFragmentManager)) {
                    if (timestampNow - lastTimestamp >= TIME_MILLIS_BETWEEN_SHAKE_DETECTIONS) {
                        // Após o timeframe decorrido, detectar novamente alterações no acelerómetro (verificar se foi "shaken")

                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        val accelerationValue = sqrt((x * x + y * y + z * z).toDouble())

                        // Ajustar threshhold de aceleração conforme necessário
                        if (accelerationValue > THRESHOLD_ACCELERATION_DETECT_SHAKE) {
                            // "Shake" detectado - verificar se o fragmento Registar Filme já está visível.
                            lastTimestamp = timestampNow

                            NavigationManager.goToRegisterWatchedMovieFragment(supportFragmentManager)
                            Toast.makeText(this, getString(R.string.lbl_register_now_shake), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não implementado para este cenário, pois não é necessário.
    }
    // -----------------------------------------------------------------------------

    private fun screenRotated(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState != null
    }

    private fun setupDrawerMenu() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawer, binding.toolbar, R.string.drawer_open, R.string.drawer_close
        )
        binding.navDrawer.setNavigationItemSelectedListener { onClickNavigationItem(it) }
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun onClickNavigationItem(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_feature_dashboard -> NavigationManager.goToDashboardFragment(supportFragmentManager)
            R.id.nav_feature_watched_movies -> NavigationManager.goToWatchedMovieListFragment(supportFragmentManager)
            R.id.nav_feature_register_movie -> NavigationManager.goToRegisterWatchedMovieFragment(supportFragmentManager)
            R.id.nav_feature_watched_movies_map -> NavigationManager.goToMapFragment(supportFragmentManager)
            // Add more as necessary... as new features are added :)
        }
        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun launchVoiceDialog(textInput: String?) {
        // Inicialmente, foi investigado o uso de um DialogFragment, totalmente personalizado a nível dos conteúdos a apresentar.
        // Créditos:
        // > https://stackoverflow.com/questions/7977392/android-dialogfragment-vs-dialog)
        // > https://developer.android.com/reference/android/app/DialogFragment
        //
        // Verificou-se então que a utilização de um simples AlertDialog poderia simplificar
        // a implementação e já trazer "out of the box" um conjunto de propriedades interessantes, que facilmente
        // permitiria lançar um Dialog para pesquisa de resultados por Voz.

        // Mostrar um novo AlertDialog, ainda sem texto de pesquisa (terá de ser introduzido pelo utilizador).
        // O Dialog será novamente invocado, conforme recebidos resultados da Activity de voice input.

        val dialog: AlertDialog =
            AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_microphone_black)
                .setTitle(R.string.lbl_title_voice_search)

                // Botão "Cancelar"
                // Na verdade, este botão nada tem a fazer... o alerta fecha-se automaticamente :)
                .setNeutralButton(R.string.lbl_cancel, null)

                // (Builder) Criar o objeto
                .create()

        // ****************************
        // Conteúdo (message) do Dialog:
        // ****************************
        if (!textInput.isNullOrEmpty()) {
            // * Já existe texto de pesquisa
            dialog.setMessage(textInput)

            // * Layout pretendido:
            // Colocar 2 botões: Repetir (voice input) + Pesquisar (submeter valor atual registado)

            // Botão "Repetir"
            // (configurado como NegativeButton coloca-o junto do botão Positivo de realizar pesquisa)
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.lbl_repeat)) { _, _ ->
                launchVoiceInputActivity()
            }

            // Botão "Pesquisar"
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.lbl_search)) { _, _ ->
                searchForMoviesWithName(textInput)
            }
        } else {
            // * Ainda sem texto de pesquisa
            dialog.setMessage("")

            // * Layout pretendido:
            // 1 botão (Falar...) -> voice input
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.lbl_speak)) { _, _ ->
                launchVoiceInputActivity()
            }
        }

        dialog.show()
    }

    private fun launchVoiceInputActivity() {
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
        startActivityForResult(intent, INTENT_VOICE_SEARCH)
    }

    // Utilizado principalmente para a funcionalidade de Pesquisa por Voz, para receber a(s) palavra(s)
    // ditas pelo utilizador, e poder atualizar os resultados.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == INTENT_VOICE_SEARCH && resultCode == Activity.RESULT_OK) {
            // "This callback is invoked when the Speech Recognizer returns."
            // "This is where you process the intent and extract the speech text from the intent."

            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results -> results[0] }

            // Se não foi recebido texto, vai solicitar apenas o input novamente.
            // Se foi recebido texto, vai relançar o AlertDialog com o texto preenchido
            launchVoiceDialog(spokenText)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun searchForMoviesWithName(nameQuery: String) {
        // Começar por indicar ao user que vai fazer a pesquisa
        val searchToast = Toast.makeText(this, getString(R.string.lbl_searching_for_results), Toast.LENGTH_SHORT)
        searchToast.show()

        // Acesso à BD para pesquisar Filmes pelo termos de pesquisa (assíncrono)
        CoroutineScope(Dispatchers.IO).launch {
            model.getWatchedMoviesImdbIdsWithTitleLike(nameQuery) { results ->

                if (results.isSuccess) {
                    val listOfMovieIDs: List<String> = results.getOrDefault(mutableListOf())

                    if (listOfMovieIDs.isEmpty()) {
                        // ERRO - sem resultados
                        CoroutineScope(Dispatchers.Main).launch {
                            searchToast.cancel()
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.lbl_no_results_found_with_value, nameQuery), Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else if (listOfMovieIDs.size == 1) {
                        // Só 1 resultado obtido: abrir diretamente o ecrã de detalhes
                        searchToast.cancel()
                        NavigationManager.goToWatchedMovieDetailsFragment(
                            supportFragmentManager,
                            listOfMovieIDs.first()
                        )

                    } else {
                        // Foram obtidos vários resultados - mostrar ecrã de Lista c/ filtro
                        CoroutineScope(Dispatchers.Main).launch {
                            searchToast.cancel()
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.lbl_showing_results_for_query, "${listOfMovieIDs.size}", nameQuery),
                                Toast.LENGTH_SHORT
                            ).show()

                            // TODO - encaminhar para a página de detalhes c/ filtro aplicado para este termo de pesquisa
                        }
                    }
                } else {
                    // Erro
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, getString(R.string.error_internal_error), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

}