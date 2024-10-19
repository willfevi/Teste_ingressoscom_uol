package com.example.ingressoscom.presentation.view.util.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.ingressoscom.R
import com.example.ingressoscom.databinding.ActivityMovieDetailBinding
import com.example.ingressoscom.presentation.model.Movie
import com.example.ingressoscom.presentation.view.MovieDetailActivity
import com.example.ingressoscom.presentation.view.adapter.formatReleaseDate
import timber.log.Timber

class MovieDetailFunctions(
    private val activity: MovieDetailActivity,
    private val binding: ActivityMovieDetailBinding,
    private val movie: Movie
) {

    fun displayMovieDetails() {
        binding.textViewTitleDetail.text = movie.title
        val formattedDate = formatReleaseDate(movie.premiereDate?.localDate)
        binding.textViewReleaseDateDetail.text = "Data de Lançamento:$formattedDate"
        binding.textViewSynopsis.text = movie.synopsis

        val posterUrl = movie.images?.firstOrNull { it.type.contains("Poster", ignoreCase = true) }?.url
        Timber.d("Carregando poster para ${movie.title}: $posterUrl")

        if (!posterUrl.isNullOrEmpty()) {
            Glide.with(activity)
                .load(posterUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.imageViewPosterDetail)
        } else {
            binding.imageViewPosterDetail.setImageResource(R.drawable.ic_placeholder)
        }
    }

    fun openTrailer() {
        val trailerUrl = movie.trailers?.firstOrNull()?.url
        if (!trailerUrl.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
            activity.startActivity(intent)
        } else {
            Timber.d("Trailer não disponível para o filme: ${movie.title}")
            Toast.makeText(activity, "Trailer não disponível.", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareMovie() {
        val trailerUrl = movie.trailers?.firstOrNull()?.url ?: ""
        val shareText = buildString {
            append("Confira o filme '${movie.title}'! ")
            append("Adquira seu ingresso agora mesmo! \uD83C\uDF7F $trailerUrl")
        }
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        activity.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
    }
}
