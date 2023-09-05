package pt.ulusofona.deisi.cm2223.g20064431_22005094

interface OnLocationChangedListener {

    // latitude e longitude para que a interface não dependa da framework Android
    fun onLocationChanged(latitude: Double, longitude: Double)

}