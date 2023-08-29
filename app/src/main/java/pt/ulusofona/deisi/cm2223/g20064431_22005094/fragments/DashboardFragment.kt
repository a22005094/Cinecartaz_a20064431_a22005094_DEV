package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {
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

    // Factory
    companion object {
        @JvmStatic
        fun newInstance() = DashboardFragment()
    }

}