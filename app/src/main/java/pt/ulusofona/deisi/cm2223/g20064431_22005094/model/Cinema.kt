package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

// Credits:
// > estender toString() numa "Data class": https://stackoverflow.com/questions/35970957/how-to-extend-a-data-class-with-tostring

// TODO | Rever: transformar em singleton com todos os métodos associados, removendo a classe Util.CinemasManager?

data class Cinema(
    val id: Int,
    val name: String,
    val provider: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val postcode: String,
    val county: String,
    val imageUrls: List<String>,

    // A lista de fotos associadas ao cinema, caso existam.
    // Está como [var] para permitir reatribuições mais tarde, conforme necessário.
    var photos: List<CustomImage>? = null
) {
    override fun toString(): String {
        // Apenas indicar o Nome
        return name
    }
}