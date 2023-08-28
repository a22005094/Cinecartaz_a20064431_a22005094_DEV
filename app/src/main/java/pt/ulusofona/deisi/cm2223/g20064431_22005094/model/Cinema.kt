package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

data class Cinema(
    val id: Int,
    val name: String,
    val provider: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val postcode: String,
    val county: String
)