package com.example.ingressoscom.presentation.remote

import com.example.ingressoscom.presentation.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("events/coming-soon/partnership/desafio")
    suspend fun getComingSoonMovies(): Response<MovieResponse>
}
