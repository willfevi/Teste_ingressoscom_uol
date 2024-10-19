// MovieRepository.kt
package com.example.ingressoscom.presentation.repository

import com.example.ingressoscom.presentation.model.MovieResponse
import com.example.ingressoscom.presentation.remote.ApiService
import com.example.ingressoscom.presentation.remote.RetrofitInstance
import retrofit2.Response
import timber.log.Timber

class MovieRepository(private val apiService: ApiService = RetrofitInstance.apiService) {
    suspend fun getComingSoonMovies(): MovieResponse? {
        Timber.d("Iniciando chamada para obter filmes em breve...")
        val response: Response<MovieResponse> = apiService.getComingSoonMovies()
        return if (response.isSuccessful) {
            Timber.d("Chamada de API bem-sucedida. Status Code: ${response.code()}")
            response.body()
        } else {
            Timber.e("Falha na chamada de API. Status Code: ${response.code()}, Erro: ${response.errorBody()?.string()}")
            null
        }
    }
}
