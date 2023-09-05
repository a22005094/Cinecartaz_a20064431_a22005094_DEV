package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import java.util.UUID

data class CustomImage(
    // Automático
    val uuid: String = UUID.randomUUID().toString(),

    // ID da entidade ao qual a imagem se refere (ID de WatchedMovie/OMDBMovie/Cinema).
    // Está como [var] para poder ter valor placeholder e ser mais tarde atualizado para o ID real da entidade.
    var refId: String,

    // nome da imagem (para desambiguar entre imagens da mesma [refId])
    val imageName: String,

    // TODO | Rever: ByteArray é o tipo ideal aqui?
    // Contém os dados em si da imagem.
    // NOTA: este atributo difere do modelo CustomImageRoom, pois nesta classe já está presente o ByteArray
    //       dos dados da imagem, por questão de facilidade no carregamento das imagens em ImageViews.
    //       Na respetiva classe Room os dados da imagem são recebidos (e armazenados) em formato encoded Base64 string.
    val imageData: ByteArray
)