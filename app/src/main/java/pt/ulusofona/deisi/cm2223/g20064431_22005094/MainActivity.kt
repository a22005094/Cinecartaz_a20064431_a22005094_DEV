package pt.ulusofona.deisi.cm2223.g20064431_22005094

// kpermissions and Manifest imports were required for location request
import android.Manifest
import android.app.Activity
import android.content.Intent
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
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ActivityMainBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.CinemasManager

// Credits pela implementação desta Activity: Ficha5 prática de CM (material das Aulas)

// Credits - Voice search: https://developer.android.com/training/wearables/user-input/voice
// TODO rever
const val INTENT_VOICE_SEARCH = 0

class MainActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO rever
        binding.fabVoiceSearch.setOnClickListener {
            launchVoiceDialogFragment()
            // performVoiceSearch()
        }

        // manage location permission request
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

    }

    override fun onStart() {
        super.onStart()
        setSupportActionBar(binding.toolbar)
        setupDrawerMenu()

        // Carregar lista de Cinemas (cinemas.json)
        CoroutineScope(Dispatchers.IO).launch {
            CinemasManager.updateListOfCinemas(applicationContext)
        }
    }

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
            // TODO: add more as necessary... as new features are added :)
        }
        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }

    // ----------

    // TODO rever
    private fun launchVoiceDialogFragment() {
        // v1 - old.      //  val dialog_v1 = DialogFragment().show(supportFragmentManager, "--teste--")
        // v2 - not good. //  VoiceSearchDialog.newInstance().show(supportFragmentManager, "Teste#1")

        NavigationManager.goToVoiceSearchFragment(supportFragmentManager)
    }

    // TODO rever
    // Utilizado principalmente para a funcionalidade de Pesquisa por Voz, para receber a(s) palavra(s)
    // ditas pelo utilizador, e poder atualizar os resultados no VoiceSearchFragment.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == INTENT_VOICE_SEARCH && resultCode == Activity.RESULT_OK) {
            // "This callback is invoked when the Speech Recognizer returns."
            // "This is where you process the intent and extract the speech text from the intent."

            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results -> results[0] }

            // Se foi recebido algum texto falado pelo Utilizador, preencher variável com resultados atuais de pesquisa
            if (spokenText.isNullOrEmpty()) {
                Toast.makeText(this, "You didn't search anything...", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "You have searched for: ['$spokenText']", Toast.LENGTH_LONG).show()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}