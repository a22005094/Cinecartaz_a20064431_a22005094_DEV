package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.ActivityMainBinding

// required imports for location request
import android.Manifest
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send


// Credits pela implementação desta Activity: Ficha5 prática de CM (material das Aulas)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // manage location permission request
        // If permissions are not granted, the app will exit
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION).build().send { result ->
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
            // TODO: add more as necessary... as new features are added :)
        }
        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }

}