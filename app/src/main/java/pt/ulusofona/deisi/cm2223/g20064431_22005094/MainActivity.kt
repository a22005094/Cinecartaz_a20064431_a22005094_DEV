package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ActivityMainBinding

// Credits pela implementação desta Activity: Ficha5 prática de CM (material das Aulas)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "Se o ecrã rodar, não vai para o Fragment principal,
        //  e permanece no Fragmento onde estava anteriormente."
        if (!screenRotated(savedInstanceState)) NavigationManager.goToDashboardFragment(supportFragmentManager)
    }

    override fun onStart() {
        super.onStart()
        setSupportActionBar(binding.toolbar)
        setupDrawerMenu()
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
            R.id.nav_feature_register_movie -> NavigationManager.goToRegistarFilmeFragment(supportFragmentManager)
            // TODO: add more as necessary... as new features are added :)
        }
        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }

}