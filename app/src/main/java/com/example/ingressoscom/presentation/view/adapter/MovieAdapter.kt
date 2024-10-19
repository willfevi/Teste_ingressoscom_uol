package com.example.ingressoscom.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ingressoscom.R
import com.example.ingressoscom.databinding.ItemMovieBinding
import com.example.ingressoscom.presentation.model.Movie
import timber.log.Timber

class MovieAdapter(
    private var movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.textViewTitle.text = movie.title

            val formattedDate = formatReleaseDate(movie.premiereDate?.localDate)
            binding.textViewReleaseDate.text = formattedDate

            val images = movie.images
            if (images.isNullOrEmpty()) {
                Timber.d("Filme ${movie.title} não possui imagens.")
            } else {
                Timber.d("Filme ${movie.title} possui ${images.size} imagens.")
                images.forEachIndexed { index, image ->
                    Timber.d("Imagem $index: url=${image.url}, type=${image.type}")
                }
            }

            val posterUrl = movie.images?.firstOrNull {
                it.type.contains("Poster", ignoreCase = true)
            }?.url
            Timber.d("Carregando poster para ${movie.title}: $posterUrl")

            if (!posterUrl.isNullOrEmpty()) {
                Glide.with(binding.imageViewPoster.context)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.imageViewPoster)
            } else {
                binding.imageViewPoster.setImageResource(R.drawable.ic_placeholder)
            }

            binding.root.setOnClickListener {
                onItemClick(movie)
                Timber.d("Item clicado: ${movie.title}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    fun updateMovies(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
        Timber.d("Lista de filmes atualizada. Total de filmes: ${movies.size}")
    }
}
