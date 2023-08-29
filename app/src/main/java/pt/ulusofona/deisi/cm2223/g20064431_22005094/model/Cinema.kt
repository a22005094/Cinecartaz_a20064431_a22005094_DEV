package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

// Credits:
// > estender toString() numa "Data class": https://stackoverflow.com/questions/35970957/how-to-extend-a-data-class-with-tostring

data class Cinema(
    val id: Int,
    val name: String,
    val provider: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val postcode: String,
    val county: String
) {
    override fun toString(): String {
        // Apenas indicar o Nome no método toString()
        return name
    }
}