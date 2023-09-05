package pt.ulusofona.deisi.cm2223.g20064431_22005094

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.DashboardFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.PickMovieFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.RegisterWatchedMovieFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.VoiceSearchFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.WatchedMoviesFragment

// Credits: Aula5 @ Fichas práticas de CM
object NavigationManager {

    private fun placeFragment(fm: FragmentManager, fragment: Fragment) {
        val transition = fm.beginTransaction()
        transition.replace(R.id.frame, fragment)
        transition.addToBackStack(null)
        transition.commit()
    }


    // ** Adicionar aqui as funções de navegação para novos Fragmentos **

    fun goToDashboardFragment(fm: FragmentManager) {
        placeFragment(fm, DashboardFragment.newInstance())
    }

    fun goToRegisterWatchedMovieFragment(fm: FragmentManager) {
        placeFragment(fm, RegisterWatchedMovieFragment.newInstance())
    }

    fun goToPickMovieFragment(fm: FragmentManager) {
        placeFragment(fm, PickMovieFragment.newInstance())
    }

    fun goToWatchedMovieListFragment(fm: FragmentManager) {
        placeFragment(fm, WatchedMoviesFragment.newInstance())
    }

    // TODO rever (eliminar)
    fun goToVoiceSearchFragment(fm: FragmentManager) {
        placeFragment(fm, VoiceSearchFragment.newInstance())
    }

}