package pt.ulusofona.deisi.cm2223.g20064431_22005094

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentMapBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils
import java.util.*

class MapFragment : Fragment(), OnLocationChangedListener {

    private lateinit var binding: FragmentMapBinding
    // Map reference
    private var map: GoogleMap? = null

    // Location name/Geocoder
    private lateinit var geocoder: Geocoder


    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        binding = FragmentMapBinding.bind(view)
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync { map ->
            this.map = map
//            // Coloca um ponto azul no mapa com a localização do utilizador
//            map.isMyLocationEnabled = true
//            // Adiciona um marker na universidade com o nome ULHT ao clicar
//            map.addMarker(
//                MarkerOptions()
//                .position(LatLng(38.75814, -9.15179))
//                .title("ULHT")
//            )
            //map.setLatLngBoundsForCameraTarget(MAP_BOUNDS)
            map?.moveCamera(CameraUpdateFactory.newLatLngBounds(MAP_BOUNDS, 10))
        }

        FusedLocation.registerListener(this)         // Register location listener
        geocoder = Geocoder(context, Locale.getDefault())   // Instantiate geocoder

        placeMarkersOnMap(binding)

        return binding.root
    }

    // Este método será invocado sempre que a posição alterar
    override fun onLocationChanged(latitude: Double, longitude: Double) {
        //placeCamera(latitude, longitude)  // only makes sense if in "navigation mode"
        placeCityName(latitude, longitude)
    }

    // Atualiza o mapa e faz zoom no mapa de acordo com a localização
    private fun placeCamera(latitude: Double, longitude: Double) {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(latitude, longitude))
            .zoom(7f)  // aprox zoom level for MAP_BOUNDS on Nexus 5 (
            .build()

        map?.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // Obtém a localidade do utilizador através da sua posição e coloca-a
    // numa TextView
    private fun placeCityName(latitude: Double, longitude: Double) {
        val addresses = geocoder.getFromLocation(latitude, longitude, 5)
        val location = addresses.first {
            it.locality != null && it.locality.isNotEmpty()
        }
        binding.tvCityName.text = location.locality
    }

    private fun placeMarkersOnMap(binding: FragmentMapBinding){
        // get list of theatres in watched movies data (dic with theatre as key, value = object)
        val theatres = Utils.watchedMovies.groupBy { it.theatre }

        // for each cinema, create an infoWindow with a list of movies watched (max 3)
        theatres.forEach{ (theatre, moviesWatched) ->
            // Add a marker for each theatre, with a list of movies
            binding.map.getMapAsync{ map ->
                this.map = map
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(theatre.latitude, theatre.longitude))
                        .snippet(moviesWatched.map { it.movie.title }.joinToString (separator = "\n"))
                        .icon(BitmapDescriptorFactory
                            .defaultMarker(Utils.decodeSatisfactionColor(10)))
                        .title(theatre.name)
                )
            }
        }
    }

    // Se o fragmento do mapa for destruído queremos parar de receber a
    // localização, se não podemos ter uma NullPointerException
    override fun onDestroy() {
        super.onDestroy()
        FusedLocation.unregisterListener()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    companion object {
        // Factory
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}