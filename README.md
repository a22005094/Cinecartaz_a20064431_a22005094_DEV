# Cinecartaz

Projeto de Computação Móvel - ULHT 2022-23\
Época Especial (06/09/2023)

## Constituição do Grupo ##
Rui Mata - a20064431\
Rui Joaquim - a22005094

## Link do APK final (OneDrive) ##
https://grupolusofona-my.sharepoint.com/:u:/g/personal/a22005094_alunos_ulht_pt/EZjQHKIx0FBDkm6vf0ptk4ABavplCGjP0n6KoIjgtpp9-w?e=EsHyoc

## Link do Vídeo (Youtube)
https://youtu.be/Mh3M5EwAVt4

## [0] - Nota importante: Erro de instalação do .APK em Android API 23 ##
O ambiente de desenvolvimento recorreu ao uso do Android Studio com 2 imagens, uma Android API 23 e outra com a API 31, sendo a segunda para permitir o Debugging e App Inspection das bases de dados Room em utilização no projeto.
Durante este processo, ambas imagens não ofereceram qualquer problema, e permitiram sempre a compilação e execução direta da aplicação no IDE, tanto em modo "Run" como em "Debug", e sempre seguindo as instruções de compilação do enunciado do projeto (minSdk = 23, compileSdk = 31, targetSdk = 31).

No entanto, após entrega do APK via Moodle (que foi copiada diretamente das pastas "debug" do Projeto), por casualidade decidimos retestar o funcionamento de raíz da aplicação em emulador com API 23, ao qual nos foi apresentada a mensagem de erro: "Error code: 'UNKNOWN', message='Unknown failure: 'Error: java.lang.NumberFormatException: Invalid int: "current" '' ". Já no emulador API 31 foi apresentado um erro diferente: "Error code: 'INSTALL_PARSE_FAILED_NO_CERTIFICATES', message='INSTALL_PARSE_FAILED_NO_CERTIFICATES: Scanning Failed.: No signature found in package of version 2 or newer for package pt.ulusofona.deisi.cm2223.g20064431_22005094'".

Tal situação obrigou o grupo a verificar o porquê deste erro de instalação do .APK, e que sugeriu a recriação do ficheiro, desta vez através do menu "Menu -> Build -> Bundle/APK -> Build APK". O erro manteve-se no emulador API 23, mas felizmente, no emulador API 31 a aplicação é instalada e pode ser utilizada com êxito.

Por este motivo, foi submetido um segundo APK via Moodle (que termina com a nomenclatura "_build", para distinção), e recomenda-se a utilização de uma imagem mais recente do Android para minimizar eventuais erros de instalação do ficheiro, lamentando o transtorno causado.

## [1] - Screenshots dos ecrãs da aplicação ##

## [2] - Classes de lógica de negócio (excluindo classes Room, de Utilitários, etc.)
NOTA: Existem várias classes que foram excluídas desta listagem, por não se tratarem de classes "puras" do Modelo de dados, tais como classes para funções de "utilitário", classes associadas a Base de Dados (classes "Room", "Dao" entre outras), Adapters, Navegação, funções de Repository/Offline, entre outras; sugere-se uma análise do código para melhor verificação do que foi implementado.

### Classe WatchedMovie (Data class para Filmes registados na App) ###
- Atributos:
    * uuid - String (auto-gerida)
    * movie - OMDBMovie
    * theatre - Cinema
    * review - Int
    * date - Long
    * comments - String
    * photos - List<CustomImage>? (nullable)
    * calcDistance - Double (default: 0.0)
- Métodos:
    * Não tem - é uma Data class simples

### Classe OMDBMovie (Data class de detalhes da API sobre um Filme) ###
- Atributos:
    * val title - String       // ex.  "Avatar Spirits"
    * year - Int?              // ex.  "2010", "N/A", etc. Nullable pois a API nem sempre disponibiliza o ano como inteiro. ("N/A")
    * imdbId - String          // ex.  "tt1900832"
    * genre - String           // ex.  "Documentary, Biography, Sport"
    * ratingImdb - Double?   // ex.  "8.2", "N/A", etc. Nullable pois a API nem sempre disponibiliza este rating como inteiro. ( filmes sem rating = "N/A" )
    * director - String      // ex.  "Christopher Nolan"
    * plotShort - String     // ex.  "The quick brown fox jumps over the lazy dog"
    * releaseDate - Long,    // ex.  Data de lançamento em formato milissegundos
    * imdbVotes - Int?,      // ex.  "42", "N/A", etc. Nullable pois a API nem sempre disponibiliza esta contagem como inteiro. ( = "N/A" )
    * posterUrl - String,     // URL do Poster (se não tem, devolve "N/A", ex.: "https://m.media-amazon.com/images/M/MV5BMzQ4MDMxNjExNl5BMl5BanBnXkFtZTgwOTYzODI5NTE@._V1_SX300.jpg")
    * poster - CustomImage?   // variável que referencia a entidade que a detém, e respetivos dados (ByteArray) da imagem. Preenchido ao selecionar um filme visto.
- Métodos:
    * toString() - String    // devolve uma representação sucinta do filme: Nome e Ano (usado p.ex. na página Registar Filme)

### Classe Cinema (Data class para Cinemas carregados para a App via JSON) ###
- Atributos:
    * id - Int
    * name - String
    * provider - String
    * latitude - Double
    * longitude - Double
    * address - String
    * postcode - String
    * county - String
    * imageUrls - List<String>    // lista de URLs para transferir as imagens do Cinema
    * photos - List<CustomImage>? // (Nullable) Lista de fotos do Cinema carregados em memória (NOTA: foi desenvolvido código para esta funcionalidade, mas depois suspenso)
- Métodos:
    * toString() - String         // ToString() para simplificação da representação do objeto; indica apenas o seu Nome.

### Classe CustomImage (Para armazenar imagens em memória, e associadas tipicamente a instâncias) ###
- Atributos:
    * uuid - String          // (auto-gerida)
    * refId - String         // o ID da entidade (Avaliação de filme / Filme OMDB / Cinema)
    * imageName - String     // File name (para desambiguação entre várias imagens do mesmo ID de Referência) 
    * imageData - ByteArray  // Contém um objeto com os dados em si da imagem (este tipo de dados facilita o carregamento de Vistas de imagens na aplicação)
- Métodos:
    * Não tem - é uma Data class simples

### Classe CustomDate ###
Classe com maior foco utilitário, para ajudar na conversão rápida de datas entre formatos, de registos em milissegundos para determinados padrões de datas (ex. 2007/12/31), e vice-versa. Procurando ultrapassar a limitação em não conseguir utilizar o tipo LocalDate, recorre ao tipo Calendar para transformações intermédias e SimpleDateFormatters para o parse de transformações de datas em Strings formatadas.

- Atributos:
    * calendar - Calendar      // (Para gestão interna das Datas)
    * sdf - SimpleDateFormat   // Para formatações
    * imageName - String       // File name (para desambiguação entre várias imagens do mesmo ID de Referência) 
    * imageData - ByteArray    // Contém um objeto com os dados em si da imagem (este tipo de dados facilita o carregamento de Vistas de imagens na aplicação)
- Métodos:
    * getYear() - Int
    * getMonth() - Int
    * getDayOfMonth() - Int
    * addDays(nrOfDays: Int) - Unit
    * setDateTo(ano: Int, mes: Int, dia: Int) - Unit
    * toMillis() - Long
    * toString() - String   // imprime a data já no formato estabelecido pelo SimpleDateFormatter
    * @JvmStatic fromHumanReadableDate(dtString: String) - CustomDate   // Factory. Devolve um objeto preenchido com base numa Data em formato String.
      

### Classe OMDBMovie (detalhes do filme visualizado, carregado via API) ###

| Nome | Tipo |
| :---: | :---: |
|  |  |

### Classe Cinema (carregados via ficheiro .JSON) ###

| Nome | Tipo |
| :---: | :---: |
|  |  |

## X - Funcionalidades desenvolvidas ##

| Funcionalidade | Desenvolvido? | Feedback |
| :---: | :---: | :---:|
| Registo de Filmes | [x] Sim | Totalmente funcional, desde o acesso à API para obter detalhes sobre filmes (no menu EXTRA), à validação dos mesmos e seleção de um Cinema, carregado a partir do ficheiro JSON. Neste menu é feito o registo do Filme (WatchedMovie), Filme do IMDB (OmdbMovie) e respetivas imagens (anexadas pelo utilizador + poster do filme) em Base de dados, para que sejam imediatamente acessíveis em offline na página de Listagem. Não foi, no entanto, implementada a funcionalidade adicional de "Utilização de geo-localização" neste ecrã. |
| Lista de filmes - Portrait | [x] Sim | Totalmente funcional e com ligação à ORM Room. Automaticamente atualizado (e para aqui encaminhado) assim que se regista um novo filme na aplicação. |
| Lista de filmes - Landscape | [+/-] Suporte limitado | |
| Lista de filmes - Mapa | [x] Sim | Totalmente funcional - os Markers do mapa estão a actualizar com base nos registos de filmes que vão sendo feitos na aplicação, indicando a localização dos respetivos Cinemas. O clique num dos Markers leva a aplicação para a página de detalhes |
| Detalhes do filme | [x] Sim | Totalmente funcional. Carrega os campos esperados no ecrã, fazendo uso dos campos armazenados em Base de Dados e das respetivas imagens (anexadas e Poster), dispostas num género de galeria. |
| Pesquisa por Voz | [x] Sim | Totalmente funcional. Acessível através de um botão na ActionBar da aplicação. Conforme o idioma do sistema, a aplicação para input de voz adapta-se ao idioma que é esperado (ex. com o sistema em EN-US, a aplicação espera que falemos em Inglês, etc.). É feita a pesquisa por resultados que contenham as palavras ditas pelo utilizador, sendo que: 1) é apresentada mensagem de erro se não existem resultados; 2) a aplicação abre a página de detalhes do filme caso seja o único resultado, e 3) caso existam múltiplos resultados, o utilizador é encaminhado para a página da Lista de filmes, de forma a que possa confirmar o que tem inserido. Como ambição do grupo, a ideia era aplicar imediatamente um filtro por nome no último cenário, para que a página de Listar filmes já indicasse apenas os resultados da pesquisa, mas, infelizmente, esta funcionalidade adicional não foi conseguida atempadamente. A sua funcionalidade recorre a um AlertDialog, com um conjunto de botões para iniciar input por voz, submeter os resultados obtidos, e uma caixa para consulta dos resultados que vão sendo obtidos por voz. |
| Suporte multi-idioma | [+/-] Parcialmente | A aplicação foi parcialmente traduzida. Os ficheiros foram criados (original em Português, e os 2 novos idiomas pretendidos, para o qual foram escolhidos o Inglês e Espanhol), e a aplicação está a adaptar as traduções (que existem) para o idioma do sistema automaticamente. Carece, portanto, apenas de finalizar as restantes entradas no ficheiro, ainda que o suporte já esteja assegurado |
| Modo Offline / Repository | [+/-] Parcialmente | Os dados dos filmes visualizados (e avaliação dos mesmos) estão a ser armazenados em Base de Dados, numa tabela para tal dedicada. Também os detalhes do filme em si selecionado pelo utilizador (proveniente da API OMDB) estão a ser armazenados, estes noutra tabela distinta para esse fim, disponibilizando assim o acesso offline na aplicação para futura consulta no respetivo ecrã de detalhes. As imagens, tanto as anexadas do telemóvel como o Poster do filme, são armazenadas por uma tabela comum de imagens, utilizando uma chave "ID externo de referência" para associar as imagens com a respetiva entidade. Como sugestão de melhoria, poderiam ser feitos novos acessos à API ao consultar os detalhes de um Filme visualizado, para que as suas informações possam ser atualizadas com o decorrer do tempo.|
| Dashboard | [ ] Não | Funcionalidade não desenvolvida. Existam planos para a sua implementação, até que existem alguns esboços de objetos no Fragmento da aplicação e para os quais foram implementadas Queries funcionais para obter algumas estatísticas simples (por exemplo: "qual o filme ao qual foi dado o pior rating?", "e qual o melhor?", entre outras). Contudo, os desenvolvimentos foram suspensos para dar lugar a outras funcionalidades mais urgentes no momento. |
| [RECURSO] RegistaJá | [x] Sim | Totalmente funcional. Suspende a funcionalidade durante os próximos 3 segundos, até permitir voltar a acionar esta feature. Faz uso do Acelerómetro do dispositivo para deteção do gesto no dispositivo. |
| [RECURSO] Filtro na Lista: nome do filme | [x] Sim | Totalmente funcional. Atualiza os resultados conforme esperado, de acordo com os termos de pesquisa inseridos. Suporta a aplicação deste filtro em conjunto com o outro, e também a ordenação de resultados. |
| [RECURSO] Filtro na Lista: proximidade | [x] Sim | Totalmente funcional, no entanto, é importante notar que o intervalo de Polling para atualização da posição (nova amostra a cada 20 segundos) do dispositivo requer algum tempo de espera para aplicar alterações à localização. Faz uso de técnicas de Observable, Listeners e outros já aplicados na funcionalidade do Mapa, para deteção da localização do dispositivo e cálculos de distâncias para os Cinemas dos filmes da lista. Suporta a aplicação deste filtro em conjunto com o outro, e também a ordenação de resultados. |
| [RECURSO] Ordenação de resultados | [x] Sim | Funcional. Suporta a ordenação em conjunto com filtragens. |

## X.2 - Markers do Mapa ##
Relativamente aos markers da funcionalidade de Listagem por GoogleMap, é feita a seguinte distinção de cores dos Markers:
* Rating de 1 a 2 (Muito Fraco): vermelho
* Rating de 3 a 4 (Fraco): cor-de-laranja
* Rating de 5 a 6 (Médio): amarelo
* Rating de 7 a 8 (Bom): azul
* Rating de 9 a 10 (Excelente): verde
* Cor alternativa (fallback): ciano

## X.3 - Outras funcionalidades pretendidas ##
| Nome | Descrição da funcionalidade |
| :---: | :---: |
| Splash Screen | Pretendíamos ter desenvolvido também um Splash Screen, para minimizar eventuais problemas com a execução de Corotinas no arranque da aplicação, dando tempo para assegurar que tudo está devidamente carregado e pronto a utilizar. A ideia seria ter um género de "SplashActivity" de onde seriam feitas as invocações/inicializações necessárias, passando o controlo para a MainActivity ao finalizar estas operações. |
| Imagens dos cinemas | Era também desejada a utilização das imagens dos Cinemas na aplicação, sendo estas apresentadas no ecrã de detalhes de um Filme visualizado, junto com os detalhes do Cinema (e como se pode verificar no código, pois as respetivas funções foram construídas e preparadas para uso). Contudo, sendo um tópico mais secundário e que levou um determinado grau de perda de performance na aplicação (inúmeros pedidos lançados em simultâneo para obter as imagens), esta funcionalidade foi então arquivada e não mais investigada. |


## X.4 - Funcionalidade EXTRA: página de seleção de um filme ##
Optámos pela criação de um novo ecrã, como a nossa funcionalidade extra para que exista uma zona reservada para a seleção de um Filme.
Considerámos que a simples pesquisa e apresentação de mensagens de erro ao utilizador poderia trazer uma experiência mais limitada na utilização da aplicação, portanto, como forma de melhorar este ponto e tirar maior proveito da API da OMDB disponibilizada, neste menu é possível introduzir um ou vários termos de pesquisa, e verificar que resultados existem. Mediante os resultados obtidos, caso existam múltiplos resultados (>10 resultados), a pesquisa é dividida em várias páginas para consulta (já por imposição da API), e é permitido ao utilizador navegar entre as próximas e anteriores páginas de resultados. Nos resultados de pesquisa são apresentados pontos-chave na seleção do filme pretendido, com o Poster, o título do filme, o Rating IMDB, o ano, diretor do filme e o género. Mediante o click num dos resultados de pesquisa, é preenchida uma variável que indica o filme selecionado, sendo esta utilizada como validação e como ponto de referência para o filme que o utilizador selecionou, e permitindo assim fazer o seu registo.
De notar que esta funcionalidade, e como parte do âmbito do projeto, só está disponível se a aplicação estiver em modo Online. Caso o dispositivo não tenha acesso à Internet, é apresentada uma mensagem Toast respetiva, e não é efetuada qualquer pesquisa; derivado disto, também não vai ser possível selecionar um filme e, portanto, o utilizador não poderá registar novos filmes.

## X.5 - Reflexão e autoavaliação ##

## X.6 - Fontes de informação ##
Todos os excertos de código que suscitaram maior dúvida e provocaram maior investigação/debate estão diretamente colocados junto com os blocos de código.
Existe uma vasta lista de pequenas referências diretas para determinados problemas específicos que foram surgindo, ao qual convidamos a identificar a documentação e comentários que por todo o código foram sempre sendo colocados.
Como fontes, destacamos o StackOverflow em primeiro lugar (dúvidas pontuais), em conjunto com a documentação oficial do Kotlin (dúvidas de código, sintaxe Kotlin, corotinas e processamento assíncrono), da Google (para temas com o Room e o Google Maps), o ChatGPT (questões mais específicas onde não foi possível obter uma melhor resposta; divulgação de demos de código), e por último, alguns blogs populares, tais como o GeeksForGeeks, também para questões pontuais.



