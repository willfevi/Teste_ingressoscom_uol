package com.example.ingressoscom.repository

import com.example.ingressoscom.presentation.model.*
import com.example.ingressoscom.presentation.remote.ApiService
import com.example.ingressoscom.presentation.repository.MovieRepository
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Response

class MovieRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        apiService = mock(ApiService::class.java)
        movieRepository = MovieRepository(apiService)
    }

    @Test
    fun `getComingSoonMovies returns MovieResponse when API call is successful`(): Unit = runBlocking {
        val mockMovie = Movie(
            id = "1",
            title = "Test Movie",
            type = "Action",
            synopsis = "Test Synopsis",
            cast = "Actor A, Actor B",
            director = "Director X",
            inPreSale = true,
            isReexhibition = false,
            isPlaying = true,
            premiereDate = PremiereDate(
                localDate = "2024-12-25",
                isToday = false,
                dayOfWeek = "Wednesday",
                dayAndMonth = "December 25",
                hour = "18:00",
                year = "2024"
            ),
            images = listOf(Image("Poster", "https://example.com/poster.jpg")),
            trailers = listOf(Trailer("Trailer", "https://example.com/trailer.mp4", "https://example.com/embedded.mp4")),
            genres = listOf("Action", "Adventure"),
            contentRating = "PG-13",
            duration = "120 min",
            rating = 8.5
        )

        val mockResponse = MovieResponse(listOf(mockMovie))
        `when`(apiService.getComingSoonMovies()).thenReturn(Response.success(mockResponse))

        val result = movieRepository.getComingSoonMovies()

        assertNotNull(result)
        assertEquals(mockResponse, result)
        verify(apiService, times(1)).getComingSoonMovies()
    }

    @Test
    fun `getComingSoonMovies returns null when API call fails`(): Unit = runBlocking {
        val errorResponse = Response.error<MovieResponse>(
            404,
            "Not Found".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(apiService.getComingSoonMovies()).thenReturn(errorResponse)

        val result = movieRepository.getComingSoonMovies()

        assertNull(result)
        verify(apiService, times(1)).getComingSoonMovies()
    }

    @Test
    fun `getComingSoonMovies returns null when API throws an exception`(): Unit = runBlocking {
        `when`(apiService.getComingSoonMovies()).thenThrow(RuntimeException("Network Failure"))

        val result = movieRepository.getComingSoonMovies()

        assertNull(result)
        verify(apiService, times(1)).getComingSoonMovies()
    }

    @Test
    fun `getComingSoonMovies returns null when response body is null`(): Unit = runBlocking {
        `when`(apiService.getComingSoonMovies()).thenReturn(Response.success(null))

        val result = movieRepository.getComingSoonMovies()

        assertNull(result)
        verify(apiService, times(1)).getComingSoonMovies()
    }

    @Test
    fun `getComingSoonMovies handles empty movie list correctly`(): Unit = runBlocking {
        val mockResponse = MovieResponse(emptyList())
        `when`(apiService.getComingSoonMovies()).thenReturn(Response.success(mockResponse))

        val result = movieRepository.getComingSoonMovies()

        assertNotNull(result)
        assertTrue(result!!.items.isEmpty())
        verify(apiService, times(1)).getComingSoonMovies()
    }
}
