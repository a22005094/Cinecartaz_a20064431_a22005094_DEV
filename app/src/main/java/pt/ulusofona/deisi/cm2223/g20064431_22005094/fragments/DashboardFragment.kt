package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentDashboardBinding


// TODO: ideias para o Dashboard
// - que filme teve a pior Rating? (e quanto)
// - que filme teve a melhor Rating? (e quanto)
// - evolução de quantos filmes foram vistos (WatchedMovies registados) - num timeframe, ex. Ano X, Ano Y, Ano Z, ...
// - armazenar localmente (BD Room) e mostrar: nº de acessos à App (?) - "Hi, this is your visit Nº 512"...
// - ...

class DashboardFragment : Fragment() {
    private val model = CinecartazRepository.getInstance()
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Colocar o título do Fragmento na AppBar
        // [Option1] - (Não funcionou)
        //  (requireActivity() as AppCompatActivity).supportActionBar?.title = R.string.calculator.toString()
        activity?.setTitle(R.string.feature_dashboard)

        // "Inflate the layout for this fragment"
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        binding = FragmentDashboardBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // TODO retestar possível crash relacionado com load dos cinemas
        // loadStatistics()
    }

    private fun loadStatistics() {
        // Stat #1: Worst-rated movie!
        CoroutineScope(Dispatchers.IO).launch {
            model.getWorstRatedWatchedMovie { result ->
                if (result.isSuccess) {
                    val watchedMovie = result.getOrNull()
                    watchedMovie?.let {
                        val statText = "${watchedMovie.movie.title}: ${watchedMovie.review}"
                        binding.tvStat1.text = statText
                    }
                }
            }
        }

        // Stat #2: Best-rated movie!
        // CoroutineScope(Dispatchers.IO).launch {
        //
        // }

        // Stat #3: Cinema mais visitado (( Não faz sentido, porque aqui usamos só 1 filme para 1 cinema... ))
        // ...
    }

    // Factory
    companion object {
        @JvmStatic
        fun newInstance() = DashboardFragment()
    }

}