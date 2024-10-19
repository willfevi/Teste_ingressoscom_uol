package com.example.ingressoscom.presentation.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ingressoscom.databinding.ActivityMovieDetailBinding
import com.example.ingressoscom.presentation.model.Movie
import com.example.ingressoscom.presentation.view.util.detail.MovieDetailFunctions
import timber.log.Timber

class MovieDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
    }

    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var movieDetailFunctions: MovieDetailFunctions
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalhes do Filme"

        movie = intent.getParcelableExtra(EXTRA_MOVIE)
        Timber.d("Recebido filme na MovieDetailActivity: ${movie?.title}")

        movie?.let {
            movieDetailFunctions = MovieDetailFunctions(this, binding, it)
            movieDetailFunctions.displayMovieDetails()
        } ?: run {
            Timber.e("Nenhum filme foi passado para MovieDetailActivity.")
            Toast.makeText(this, "Erro ao carregar detalhes do filme.", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.buttonWatchTrailer.setOnClickListener {
            movieDetailFunctions.openTrailer()
        }

        binding.imageButtonShare.setOnClickListener {
            movieDetailFunctions.shareMovie()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
