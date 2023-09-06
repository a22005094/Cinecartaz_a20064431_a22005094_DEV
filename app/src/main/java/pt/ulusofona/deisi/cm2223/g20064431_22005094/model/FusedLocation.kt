import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import pt.ulusofona.deisi.cm2223.g20064431_22005094.OnLocationChangedListener
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.Utils

@SuppressLint("MissingPermission")
class FusedLocation private constructor(context: Context) : LocationCallback() {

    private val TAG = FusedLocation::class.java.simpleName

    // Intervalos de tempo em que a localização é verificada, 20 segundos
    private val TIME_BETWEEN_UPDATES = 20 * 1000L

    // Este atributo será utilizado para acedermos à API da Fused Location
    private var client = FusedLocationProviderClient(context)

    // Configurar a precisão e os tempos entre atualizações da localização
    private var locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = TIME_BETWEEN_UPDATES
    }

    var lastKnownLocation: LatLng = LatLng(0.0, 0.0)
        private set

    init {

        // Instanciar o objeto que permite definir as configurações
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        // Aplicar as configurações ao serviço de localização
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(locationSettingsRequest)

        client.requestLocationUpdates(locationRequest,
            this, Looper.getMainLooper()
        )
    }

    // Este método é invocado sempre que a posição se alterar
    override fun onLocationResult(locationResult: LocationResult) {
        val newLocation = LatLng( locationResult.lastLocation.latitude,locationResult.lastLocation.longitude)
        val distance = Utils.calculateDistance(lastKnownLocation, newLocation)
        Log.i(TAG, locationResult.lastLocation.toString())
        notifyListeners(locationResult)

        Log.i(TAG, "previous location $lastKnownLocation")
        lastKnownLocation = newLocation
        Log.i(TAG, "---new location $lastKnownLocation, which distances $distance")
    }

    companion object {
        // Se quisermos ter vários listeners isto tem de ser uma lista
        private var listener: OnLocationChangedListener? = null
        private var instance: FusedLocation? = null

        fun registerListener(listener: OnLocationChangedListener) {
            this.listener = listener
        }

        fun unregisterListener() {
            listener = null
        }

        // Se tivermos vários listeners, temos de os notificar com um forEach
        fun notifyListeners(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            Utils.currentLocation = LatLng(
                locationResult.lastLocation.latitude,
                locationResult.lastLocation.longitude)
            listener?.onLocationChanged(location.latitude, location.longitude)
        }

        // Só teremos uma instância em execução
        fun start(context: Context) {
            instance =
                if(instance == null) FusedLocation(context)
                else instance
        }
    }

}