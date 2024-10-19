package com.example.ingressoscom.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ingressoscom.presentation.model.Movie
import com.example.ingressoscom.presentation.repository.MovieRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    val moviesLiveData = MutableLiveData<List<Movie>>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    private var allMovies: List<Movie> = emptyList()

    fun fetchMovies() {
        loadingLiveData.postValue(true)
        Timber.d("Iniciando a busca de filmes...")
        viewModelScope.launch {
            try {
                val response = repository.getComingSoonMovies()
                if (response != null && response.items.isNotEmpty()) {
                    Timber.d("Recebidos ${response.items.size} filmes da API.")
                    val sortedMovies = response.items.filter { it.premiereDate?.localDate != null }
                        .sortedBy { it.premiereDate?.localDate }
                    allMovies = sortedMovies
                    moviesLiveData.postValue(allMovies)
                    Timber.d("Lista de filmes atualizada com ${allMovies.size} filmes.")
                } else {
                    val errorMessage = "Nenhum filme disponível"
                    errorLiveData.postValue(errorMessage)
                    Timber.w("Nenhum filme disponível na resposta da API.")
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Erro desconhecido"
                errorLiveData.postValue(errorMessage)
                Timber.e(e, "Erro ao buscar filmes: $errorMessage")
            } finally {
                loadingLiveData.postValue(false)
                Timber.d("Finalizada a busca de filmes.")
            }
        }
    }

    fun filterMovies(showPresaleOnly: Boolean) {
        if (showPresaleOnly) {
            val presaleMovies = allMovies.filter { it.inPreSale }
            moviesLiveData.postValue(presaleMovies)
            Timber.d("Filtrando para pré-venda: ${presaleMovies.size} filmes encontrados.")
        } else {
            moviesLiveData.postValue(allMovies)
            Timber.d("Filtro de pré-venda removido. Exibindo todos os ${allMovies.size} filmes.")
        }
    }

    fun searchMovies(query: String) {
        Timber.d("Pesquisando filmes com a query: '$query'")
        val filteredMovies = allMovies.filter {
            it.title.contains(query, ignoreCase = true)
        }
        moviesLiveData.postValue(filteredMovies)
        Timber.d("${filteredMovies.size} filmes encontrados para a pesquisa.")
    }

    fun resetMovies() {
        moviesLiveData.postValue(allMovies)
        Timber.d("Lista de filmes resetada para o estado original com ${allMovies.size} filmes.")
    }
}
