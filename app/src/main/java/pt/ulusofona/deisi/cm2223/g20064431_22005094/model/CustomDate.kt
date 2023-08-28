package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// - A gestão de classes na API 23 é... limitada.
// - Não existe acesso ao tipo LocalDate, e ao dia de hoje já existem algumas recomendações sobre a não-utilização
//    de determinadas classes que neste ponto vigoravam.
// - O que se pretende nesta classe é, simplesmente, procurar reduzir o boilerplate que envolve a gestão
//    de Datas, utilizando um tipo "customizado" de dados.
// - Feito com ligeiro recurso ao ChatGPT, usando queries semelhantes a:
//    "How to manage Android dates on API level 23?"; "How to get the device's current date in Android API 23?", etc.

class CustomDate {
    // Para armazenar a Data
    private val calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Por default, seleciona a data atual no dispositivo.
    init {
        calendar.timeInMillis = System.currentTimeMillis()
    }

    fun getAno(): Int = calendar.get(Calendar.YEAR)
    fun getMes(): Int = calendar.get(Calendar.MONTH)  // NOTA: O mês é Zero-based! (0 = Janeiro)
    fun getDia(): Int = calendar.get(Calendar.DAY_OF_MONTH)
    fun getDataEmMillis(): Long = calendar.timeInMillis

    // Adiciona (se positivo) ou retira (se negativo) X dias à instância.
    fun adicionarDias(nrDias: Int) = calendar.add(Calendar.DAY_OF_MONTH, nrDias)

    fun alterarDataPara(ano: Int, mes: Int, dia: Int) {
        calendar.set(Calendar.YEAR, ano)
        calendar.set(Calendar.MONTH, mes)
        calendar.set(Calendar.DAY_OF_MONTH, dia)
    }

    // Escreve no formato dd/MM/yyyy, ex: "31/12/1990"
    override fun toString(): String = sdf.format(calendar.time)

}