package pt.ulusofona.deisi.cm2223.g20064431_22005094

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.DashboardFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.PickMovieFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.RegisterWatchedMovieFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.WatchedMovieDetailsFragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments.WatchedMoviesFragment


// Credits: Aula5 @ Fichas práticas de CM
object NavigationManager {

    private fun placeFragment(fm: FragmentManager, fragment: Fragment, tag: String) {
        val transition = fm.beginTransaction()
        transition.replace(R.id.frame, fragment, tag)
        transition.addToBackStack(null)
        transition.commit()
    }


    // ** Adicionar aqui as funções de navegação para novos Fragmentos **

    fun popCurrentFragment(fm: FragmentManager) {
        fm.popBackStack()
    }

    fun goToDashboardFragment(fm: FragmentManager) {
        placeFragment(fm, DashboardFragment.newInstance(), TAG_DASHBOARD_FRAGMENT)
    }

    fun goToRegisterWatchedMovieFragment(fm: FragmentManager) {
        placeFragment(fm, RegisterWatchedMovieFragment.newInstance(), TAG_REGISTER_WATCHED_MOVIE_FRAGMENT)
    }

    fun goToPickMovieFragment(fm: FragmentManager) {
        placeFragment(fm, PickMovieFragment.newInstance(), TAG_PICK_MOVIE_FRAGMENT)
    }

    fun goToWatchedMovieListFragment(fm: FragmentManager) {
        placeFragment(fm, WatchedMoviesFragment.newInstance(), TAG_WATCHED_MOVIE_LIST_FRAGMENT)
    }

    fun goToMapFragment(fm: FragmentManager) {
        placeFragment(fm, MapFragment.newInstance(), TAG_MAP_FRAGMENT)
    }

    fun goToWatchedMovieDetailsFragment(fm: FragmentManager, watechMovieUuiD: String) {
        placeFragment(fm, WatchedMovieDetailsFragment.newInstance(watechMovieUuiD), TAG_WATCHED_MOVIE_DETAILS_FRAGMENT)
    }

    // Usado em conjunto com o acelerómetro, para não aceder várias vezes ao mesmo fragmento.
    fun isCurrentlyAtRegisterWatchedMovieFragment(fm: FragmentManager): Boolean {
        val fragmentRegisterWatchedMovie = fm.findFragmentByTag(TAG_REGISTER_WATCHED_MOVIE_FRAGMENT)
        // Devolve TRUE se o fragmento estiver atualmente visível no ecrã
        val isVisible = fragmentRegisterWatchedMovie != null && fragmentRegisterWatchedMovie.isVisible
        return isVisible
    }

}