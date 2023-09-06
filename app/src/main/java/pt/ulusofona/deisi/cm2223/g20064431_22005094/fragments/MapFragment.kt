package pt.ulusofona.deisi.cm2223.g20064431_22005094.fragments

import FusedLocation
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.deisi.cm2223.g20064431_22005094.*
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.databinding.FragmentMapBinding
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils
import java.util.*


class MapFragment : Fragment(), OnLocationChangedListener, GoogleMap.OnMarkerClickListener {

    private lateinit var binding: FragmentMapBinding

    // Map reference
    private var map: GoogleMap? = null


    // data model
    private val model = CinecartazRepository.getInstance()

    // list of movies watched
    var watchedMovies: List<WatchedMovie> = listOf()


    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val bounds = MAP_BOUNDS

        binding = FragmentMapBinding.bind(view)
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync { map ->
            this.map = map
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN

            map.setOnMarkerClickListener { onMarkerClick(it) }

            map!!.setOnMapLoadedCallback {
                map!!.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        10
                    )
                )
            }
            map.setOnMarkerClickListener(this)
        }



        FusedLocation.registerListener(this)         // Register location listener
        //geocoder = Geocoder(context, Locale.getDefault())   // Instantiate geocoder


        return binding.root
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            model.getWatchedMovies { result ->
                if (result.isSuccess) {

                    // populate list of watched movies
                    watchedMovies = result.getOrDefault(mutableListOf())

                    CoroutineScope(Dispatchers.Main).launch {
                        // place one marker for each cinema
                        placeMarkersOnMap(binding)
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        NavigationManager.popCurrentFragment(activity!!.supportFragmentManager)
                    }
                }
            }
        }

    }

    override fun onLocationChanged(latitude: Double, longitude: Double) {
        //placeCamera(latitude, longitude)  // only makes sense if in "navigation mode"
        //placeCityName(latitude, longitude)

        // update current location
    }

    private fun placeMarkersOnMap(binding: FragmentMapBinding) {
        // get list of theatres in watched movies data (dic with theatre as key, value = object)
        val theatres = watchedMovies.groupBy { it.theatre }

        // for each cinema, create an infoWindow with a list of movies watched (max 3)
        theatres.forEach { (theatre, moviesWatched) ->
            // Add a marker for each theatre, with a list of movies
            binding.map.getMapAsync { map ->
                this.map = map
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(theatre.latitude, theatre.longitude))
                        //.snippet(moviesWatched.map { it.movie.title }.joinToString (separator = "\n"))
                        .snippet(moviesWatched[0].uuid)
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(Utils.decodeSatisfactionColor(moviesWatched[0].review))
                        )
                        .title(theatre.name)
                )
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.i("RMata", "marker uuid; ${marker.snippet}")
        CoroutineScope(Dispatchers.Main).launch {
            activity?.supportFragmentManager?.let {
                NavigationManager.goToWatchedMovieDetailsFragment(
                    it,
                    marker.snippet!!
                )
            }
        }
        return true
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



/*
// Sample from classes. was useful for sampling and debug
private fun placeCityName(latitude: Double, longitude: Double) {
    val addresses = geocoder.getFromLocation(latitude, longitude, 5)
    val location = addresses.first {
        it.locality != null && it.locality.isNotEmpty()
    }
    //binding.tvCityName.text = location.locality
}



// Moves the camera to center user's location - would be useful in navigation mode...

    private fun initialPosition(){
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(MAP_BOUNDS, 10))
    }

    private fun placeCamera(latitude: Double, longitude: Double, toBound: Boolean = false) {
        var auxlatitude = latitude
        var auxlongitude = longitude
        if (toBound){
            auxlatitude = ( MAP_BOUNDS_NE.latitude + MAP_BOUNDS_SW.latitude ) / 2
            auxlongitude = ( MAP_BOUNDS_NE.longitude + MAP_BOUNDS_SW.longitude ) / 2
        }

        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(auxlatitude, auxlongitude), 10f));

    }

*/
