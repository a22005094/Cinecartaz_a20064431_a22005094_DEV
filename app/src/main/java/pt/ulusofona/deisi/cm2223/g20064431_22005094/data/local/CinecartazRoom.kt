package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import android.util.Base64
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinecartaz
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.CinemasManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo

class CinecartazRoom(
    private val watchedMoviesDao: WatchedMovieDao,
    private val omdbMoviesDao: OMDBMovieDao,
    private val imagesDao: CustomImageDao
) : Cinecartaz() {

    // Classe que aglomerar toda a gestão local de dados (BD), seja de Avaliações, OMDBMovies, etc.
    // É o objeto que responde à vertente local deste projeto (@ padrão Repository).

    override fun getMoviesByName(
        movieName: String, pageNumber: Int, onFinished: (Result<MovieSearchResultInfo>) -> Unit
    ) {
        throw Exception("Illegal operation")
    }

    override fun getMovieDetailsByImdbId(imdbId: String, onFinished: (Result<OMDBMovie>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getWatchedMovies(onFinished: (Result<List<WatchedMovie>>) -> Unit) {
        // para os objetos Room
        val listOfWatchedMoviesRoom = watchedMoviesDao.getAll()
        // para os objetos completos a usar in-App
        val listOfWatchedMovies = mutableListOf<WatchedMovie>()

        for (movieRoomObj in listOfWatchedMoviesRoom) {

            // No Room, os filmes vistos (WatchedMovie) referencia o respetivo OMDBMovie e Cinema
            // através dos seus IDs (na BD não existe uma "gravação direta" de objetos).
            // Portanto, para carregar para memória o objeto WatchedMovie "puro", completo e já com referência
            // aos objetos associados OMDBMovie e Cinema, é necessário buscá-los a partir dos IDs.
            val omdbMovieRoomObj = omdbMoviesDao.getByImdbId(movieRoomObj.movieImdbId)
            val cinemaObj = CinemasManager.getCinemaById(movieRoomObj.cinemaId)!!

            if (omdbMovieRoomObj != null) {
                // A variável "cinemaObj" não é null-checked, pois neste projeto os dados dos Cinemas vão sempre iguais
                // (a origem dos dados dos Cinemas, "cinemas.json", é um ficheiro estático).

                val omdbMovieObj = OMDBMovie(
                    omdbMovieRoomObj.title,
                    omdbMovieRoomObj.year,
                    omdbMovieRoomObj.imdbId,
                    omdbMovieRoomObj.genre,
                    omdbMovieRoomObj.ratingImdb,
                    omdbMovieRoomObj.director,
                    omdbMovieRoomObj.plotShort,
                    omdbMovieRoomObj.posterUrl,
                    null              // TODO falta carregar aqui o ByteArray do poster
                )

                // Para estas questões intermédias de conversões [Long] <-> [Date] (bidirecionalmente),
                //  foi analisada a possibilidade de utilizar uma classe de TypeConverters no Room
                //  (ver: https://developer.android.com/training/data-storage/room/referencing-data).
                // No entanto foi descartada - na classe do Modelo efetuou-se a alteração para
                //  já usar diretamente o tipo de dados Long.

                listOfWatchedMovies.add(
                    WatchedMovie(
                        uuid = movieRoomObj.uuid,
                        movie = omdbMovieObj,
                        theatre = cinemaObj,
                        review = movieRoomObj.review,
                        date = movieRoomObj.date,
                        comments = movieRoomObj.comments,
                        null           // TODO falta carregar aqui o ByteArray das imagens anexadas
                    )
                )
            }

        }

        onFinished(Result.success(listOfWatchedMovies))

        //
        // TODO - Obter as fotos do WatchedMovie, poster do OMDBMovie e imagens do Cinema.
        // TODO - E se estiver Online... atualizar dados do OMDBMovie?
        //

    }

    override fun insertWatchedMovie(watchedMovie: WatchedMovie, onFinished: () -> Unit) {
        // Inserir um WatchedMovie implica um conjunto de operações:

        // 1- Inserir a instância do WatchedMovie na sua Tabela
        watchedMoviesDao.insert(
            WatchedMovieRoom(
                watchedMovie.uuid,
                watchedMovie.movie.imdbId,
                watchedMovie.theatre.id,
                watchedMovie.review,
                watchedMovie.date,
                watchedMovie.comments
            )
        )

        // 2- Inserir a instância do OMDBMovie na sua Tabela
        // (Os dados do filme estão obrigatoriamente atualizados, pois para
        //  ter sido atribuído ao WatchedMovie, os seus detalhes tiveram de ser recentemente requisitados via API!)

        // TODO assegurar que faz "Upsert", e não Insert duplicado. Se houver dados antigos, têm de ser substituídos!
        omdbMoviesDao.insert(
            OMDBMovieRoom(
                watchedMovie.movie.imdbId,
                watchedMovie.movie.title,
                watchedMovie.movie.year,
                watchedMovie.movie.genre,
                watchedMovie.movie.ratingImdb,
                watchedMovie.movie.director,
                watchedMovie.movie.plotShort,
                watchedMovie.movie.posterUrl
            )
        )

        // -------------------
        // ----- IMAGENS -----
        // -------------------

        // Lista para aglomerar todas as imagens a inserir (WatchedMovie + OMDBMovie + Cinema)
        // Todas as imagens a inserir vão ser recebidas como CustomImages, onde existe um ByteArray c/ os dados de cada Imagem.
        // Para inserir na BD Room, cada imagem (ByteArray) será encoded na respetiva String em Base64 (o formato esperado no DAO das Imagens).
        val listOfImages = mutableListOf<CustomImage>()

        // Buscar as CustomImages do WatchedMovie, se existirem
        watchedMovie.photos?.let {
            listOfImages.addAll(watchedMovie.photos!!)
        }

        // TODO testar e validar que isto é feito
        // Buscar o Poster do OMDBMovie.
        // Neste momento, a imagem do Poster também já está presente, pois foi armazenada em memória
        // no momento em que o Filme foi selecionado no menu Pesquisar Filme (PickMovieFragment).
        watchedMovie.movie.poster?.let {
            listOfImages.add(watchedMovie.movie.poster!!)
        }

        // TODO testar e validar que isto é feito
        // Buscar as CustomImages do Cinema (se tiver Fotos)
        watchedMovie.theatre.photos?.let {
            listOfImages.addAll(watchedMovie.theatre.photos!!)
        }

        // Todos os dados já estão no formato esperado, exceto o ByteArray que será convertido para Base64 String.
        // Créditos - documentação de apoio para conversão de ByteArray para string encoded Base64:
        // - https://www.bezkoder.com/kotlin-base64/
        // - https://stackoverflow.com/questions/2418485/how-do-i-convert-a-byte-array-to-base64-in-java
        val listOfImagesRoom = listOfImages.map { customImage ->
            CustomImageRoom(
                uuid = customImage.uuid, refId = customImage.refId, imageName = customImage.imageName,
                imageData = Base64.encodeToString(customImage.imageData, Base64.DEFAULT)
            )
        }
        imagesDao.insertAll(listOfImagesRoom)

        onFinished()
    }

    override fun insertOMDBMovie(movie: OMDBMovie, onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun insertImage(image: CustomImage, onFinished: () -> Unit) {
        TODO("Not yet implemented")
        // Avaliar se é mesmo necessário
    }

    override fun clearAllMovies(onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

}