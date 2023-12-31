package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import android.util.Base64
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinecartaz
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.CinemasManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo
import java.io.IOException

class CinecartazRoom(
    private val watchedMoviesDao: WatchedMovieDao,
    private val omdbMoviesDao: OMDBMovieDao,
    private val imagesDao: CustomImageDao
) : Cinecartaz() {

    // Classe que aglomerar toda a gestão local de dados (BD), seja de Avaliações, OMDBMovies, etc.
    // É o objeto que responde à vertente local deste projeto (@ padrão Repository).

    override fun getOmdbMovieIdsByName(
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

                // imagens anexadas do dispositivo (0...N imagens)
                val watchedMovieImgs = getCustomImageByRefId(movieRoomObj.uuid)
                // poster do filme (ou 0 ou 1 imagem)
                val omdbMovieImgs = getCustomImageByRefId(omdbMovieRoomObj.imdbId)

                val omdbMovieObj = OMDBMovie(
                    omdbMovieRoomObj.title,
                    omdbMovieRoomObj.year,
                    omdbMovieRoomObj.imdbId,
                    omdbMovieRoomObj.genre,
                    omdbMovieRoomObj.ratingImdb,
                    omdbMovieRoomObj.director,
                    omdbMovieRoomObj.plotShort,
                    omdbMovieRoomObj.releaseDate,
                    omdbMovieRoomObj.imdbVotes,
                    omdbMovieRoomObj.posterUrl,
                    // Carrega a imagem se tiver
                    if (omdbMovieImgs.isNotEmpty()) omdbMovieImgs.first() else null
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
                        photos = watchedMovieImgs
                    )
                )
            }

        }

        onFinished(Result.success(listOfWatchedMovies))

        // TODO - E se estiver Online... atualizar dados do OMDBMovie?
    }

    override fun getWatchedMovie(UuiD: String, onFinished: (Result<WatchedMovie>) -> Unit) {
        val resultWatchedMovie = watchedMoviesDao.getByUuid(UuiD)
        if (resultWatchedMovie != null) {

            val resultMovie = getMovieByOMDBId(resultWatchedMovie.movieImdbId)
            if (resultMovie == null) {
                onFinished(Result.failure(IOException("No data found for movie id ${resultWatchedMovie.movieImdbId}!!")))
                return
            }

            val resultCinema = CinemasManager.getCinemaById(resultWatchedMovie.cinemaId)
            if (resultCinema == null) {
                onFinished(Result.failure(IOException("No data found for cinema id ${resultWatchedMovie.cinemaId}!!")))
                return
            }

            // GET PHOTOS
            // (removed) get photos taken when the theatre was visit TODO

            // imagens anexadas do dispositivo (0...N imagens)
            val watchedMovieImgs = getCustomImageByRefId(UuiD)
            // poster do filme (ou 0 ou 1 imagem)
            val omdbMovieImgs = getCustomImageByRefId(resultMovie.imdbId)

            // atribuir poster (da BD) no objeto OmdbMovie
            resultMovie.poster = if (omdbMovieImgs.isNotEmpty()) omdbMovieImgs.first() else null

            // create return object with retrieved information
            val watchedMovie = WatchedMovie(
                resultWatchedMovie.uuid,
                resultMovie,
                resultCinema,
                resultWatchedMovie.review,
                resultWatchedMovie.date,
                resultWatchedMovie.comments,
                // carregar as imagens anexadas no device (pelo user)
                watchedMovieImgs
            )
            onFinished(Result.success(watchedMovie))

        } else {
            onFinished(Result.failure(IOException("No data found for UuiD ${UuiD}!!")))
        }
    }

    override fun getWatchedMoviesImdbIdsWithTitleLike(name: String, onFinished: (Result<List<String>>) -> Unit) {
        // Em princípio devolverá sempre resposta de Sucesso, independentemente do valor da Contagem
        onFinished(Result.success(watchedMoviesDao.getAllUuidsWithOmdbMovieTitleLike(name)))
    }

    override fun getWorstRatedWatchedMovie(onFinished: (Result<WatchedMovie>) -> Unit) {
        val resultWatchedMovie = watchedMoviesDao.getWorstRated()
        if (resultWatchedMovie != null) {

            val resultMovie = getMovieByOMDBId(resultWatchedMovie.movieImdbId)
            if (resultMovie == null) {
                // TODO - ?
                //onFinished(Result.failure(IOException("No data found for movie id ${resultWatchedMovie.movieImdbId}!!")))
                return
            }

            val resultCinema = CinemasManager.getCinemaById(resultWatchedMovie.cinemaId)
            if (resultCinema == null) {
                // TODO - ?
                //onFinished(Result.failure(IOException("No data found for cinema id ${resultWatchedMovie.cinemaId}!!")))
                return
            }

            onFinished(
                Result.success(
                    WatchedMovie(
                        uuid = resultWatchedMovie.uuid,
                        movie = resultMovie,
                        theatre = resultCinema,
                        review = resultWatchedMovie.review,
                        date = resultWatchedMovie.date,
                        comments = resultWatchedMovie.comments,
                        null // photo does not matter here
                    )
                )
            )

        } else {
            // TODO - ?
        }
    }

    override fun getBestRatedWatchedMovie(onFinished: (Result<WatchedMovie>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getWatchedMoviesWithTitleLike(name: String, onFinished: (Result<List<WatchedMovie>>) -> Unit) {
        // Semelhante à função "getWatchedMovies()".

        // para os objetos Room
        val listOfWatchedMoviesRoom: List<WatchedMovieRoom> =
            watchedMoviesDao.getAllWithOmdbMovieTitleLike(name) ?: mutableListOf()
        // para os objetos completos a usar in-App (os resultados da pesquisa, per se)
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

                // imagens anexadas do dispositivo (0...N imagens)
                val watchedMovieImgs = getCustomImageByRefId(movieRoomObj.uuid)
                // poster do filme (ou 0 ou 1 imagem)
                val omdbMovieImgs = getCustomImageByRefId(omdbMovieRoomObj.imdbId)

                val omdbMovieObj = OMDBMovie(
                    omdbMovieRoomObj.title,
                    omdbMovieRoomObj.year,
                    omdbMovieRoomObj.imdbId,
                    omdbMovieRoomObj.genre,
                    omdbMovieRoomObj.ratingImdb,
                    omdbMovieRoomObj.director,
                    omdbMovieRoomObj.plotShort,
                    omdbMovieRoomObj.releaseDate,
                    omdbMovieRoomObj.imdbVotes,
                    omdbMovieRoomObj.posterUrl,
                    // Carrega a imagem se tiver
                    if (omdbMovieImgs.isNotEmpty()) omdbMovieImgs.first() else null
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
                        photos = watchedMovieImgs
                    )
                )
            }

        }

        onFinished(Result.success(listOfWatchedMovies))
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
                watchedMovie.movie.posterUrl,
                watchedMovie.movie.releaseDate,
                watchedMovie.movie.imdbVotes
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

    // Não aplicável.
    // A função insertWatchedMovie() já trata da inserção de todos os modelos individualmente:
    // - Filme visto (WatchedMovie);
    // - Filme do IMDB vindo da API (OmdbMovie)
    // - Imagens (imagens anexadas pelo utilizador, poster do filme e fotos do cinema selecionado)

    // override fun insertOMDBMovie(movie: OMDBMovie, onFinished: () -> Unit) {
    //     throw Exception("Illegal operation - done via insertWatchedMovie()")
    // }

    // override fun insertImage(image: CustomImage, onFinished: () -> Unit) {
    //     throw Exception("Illegal operation - done via insertWatchedMovie()")
    // }

    // override fun clearAllMovies(onFinished: () -> Unit) {
    //     throw Exception("Illegal operation - done via insertWatchedMovie()")
    // }

    override fun getAllCustomImagesByRefId(
        refId: String,
        onFinished: (Result<List<CustomImage>>) -> Unit
    ) {

        val resultCustomImages: List<CustomImage> = getCustomImageByRefId(refId)
        onFinished(Result.success(resultCustomImages))
    }

// ------------ Custom ROOM/DAO methods


    fun getCustomImageByRefId(refId: String): List<CustomImage> {
        val resultCustomImageDao = imagesDao.getAllByRefId(refId)

        if (!resultCustomImageDao.isNullOrEmpty()) {
            return resultCustomImageDao.map {
                CustomImage(
                    it.uuid,
                    it.refId,
                    it.imageName,
                    Base64.decode(it.imageData, Base64.DEFAULT)
                )
            }.toList()
        }
        return emptyList()
    }


    // get movie object
    fun getMovieByOMDBId(omdbMovieId: String): OMDBMovie? {
        val resultMovieDao = omdbMoviesDao.getByImdbId(omdbMovieId)

        if (resultMovieDao != null) {
            return OMDBMovie(
                resultMovieDao.title,
                resultMovieDao.year,
                resultMovieDao.imdbId,
                resultMovieDao.genre,
                resultMovieDao.ratingImdb,
                resultMovieDao.director,
                resultMovieDao.plotShort,
                resultMovieDao.releaseDate,
                resultMovieDao.imdbVotes,
                resultMovieDao.posterUrl,
                getCustomImageByRefId(resultMovieDao.imdbId).let {
                    if (it.isNotEmpty()) it.first() else null
                }
            )
        }
        return null
    }

    // get cinema object
}