package com.example.ingressoscom.presentation.view.util.main

import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ingressoscom.R
import com.example.ingressoscom.databinding.ActivityMainBinding
import com.example.ingressoscom.presentation.model.Movie
import com.example.ingressoscom.presentation.view.MovieDetailActivity
import com.example.ingressoscom.presentation.view.adapter.MovieAdapter
import com.example.ingressoscom.presentation.viewmodel.MovieViewModel
import timber.log.Timber

class FunctionsAux(
    private val activity: AppCompatActivity,
    private val binding: ActivityMainBinding,
    private val viewModel: MovieViewModel,
    private val locationHelper: LocationHelper,
    private val network: Network
) {

    private var isSearchVisible: Boolean = false
    private var isPresaleFilterActive: Boolean = false
    private var movieAdapter: MovieAdapter? = null

    fun setupListeners() {
        binding.iconSearch.setOnClickListener {
            toggleSearchVisibility()
        }

        binding.searchEditText.addTextChangedListener(searchTextWatcher)

        binding.iconLocation.setOnClickListener {
            displayUserAddress()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (network.isNetworkAvailable()) {
                viewModel.fetchMovies()
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                showErrorViews("Sem conexão com a internet. Verifique sua rede e tente novamente.")
                Timber.e("Tentativa de atualizar filmes sem conexão com a internet.")
            }
        }
    }

    fun setAdapter(adapter: MovieAdapter) {
        this.movieAdapter = adapter
    }

    fun handleBottomNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.preorder -> {
                togglePresaleFilter(item)
                true
            }
            R.id.navigation_cine -> {
                openGoogleSearchForCinemas()
                true
            }
            R.id.grid -> {
                toggleLayoutManager()
                true
            }
            else -> false
        }
    }

    private fun toggleSearchVisibility() {
        isSearchVisible = !isSearchVisible
        if (isSearchVisible) {
            binding.searchEditText.visibility = View.VISIBLE
            binding.toolbarLogo.visibility = View.GONE
            binding.textViewAddress.visibility = View.GONE
            Timber.d("Campo de busca exibido.")
        } else {
            binding.searchEditText.text.clear()
            binding.searchEditText.visibility = View.GONE
            binding.toolbarLogo.visibility = View.VISIBLE
            if (binding.textViewAddress.text.isNotEmpty()) {
                binding.textViewAddress.visibility = View.VISIBLE
            }
            viewModel.resetMovies()
            Timber.d("Campo de busca ocultado e lista de filmes resetada.")
        }
    }

    private val searchTextWatcher = object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) {
            val query = s.toString()
            viewModel.searchMovies(query)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun togglePresaleFilter(item: MenuItem) {
        isPresaleFilterActive = !isPresaleFilterActive
        viewModel.filterMovies(isPresaleFilterActive)
        item.title = if (isPresaleFilterActive) "Todos os Filmes" else "Pré-venda"
        val message = if (isPresaleFilterActive) "Filtrando apenas filmes em pré-venda." else "Exibindo todos os filmes."
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        Timber.d(message)
    }

    private fun openGoogleSearchForCinemas() {
        Timber.d("Abrindo pesquisa no Google para cinemas próximos.")
        val uri = Uri.parse("https://www.google.com/search?q=cinemas+perto+de+mim")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
    }

    private fun toggleLayoutManager() {
        if (binding.recyclerViewMovies.layoutManager is GridLayoutManager) {
            binding.recyclerViewMovies.layoutManager = LinearLayoutManager(activity)
            Toast.makeText(activity, "Visualização em lista ativada.", Toast.LENGTH_SHORT).show()
            Timber.d("LayoutManager alterado para LinearLayoutManager.")
        } else {
            binding.recyclerViewMovies.layoutManager = GridLayoutManager(activity, 3)
            Toast.makeText(activity, "Visualização em grade ativada.", Toast.LENGTH_SHORT).show()
            Timber.d("LayoutManager alterado para GridLayoutManager.")
        }
    }

    private fun displayUserAddress() {
        if (!locationHelper.isLocationPermissionGranted()) {
            Timber.d("Permissão de localização não concedida. Solicitando permissão...")
            locationHelper.requestLocationPermission(activity)
        } else {
            fetchAndDisplayAddress()
        }
    }

    fun fetchAndDisplayAddress() {
        locationHelper.fetchUserLocation(
            onSuccess = { location ->
                val address = locationHelper.getAddressFromLocation(location)
                if (address != null) {
                    binding.textViewAddress.text = address
                    binding.textViewAddress.visibility = View.VISIBLE
                    Toast.makeText(activity, "Endereço obtido: $address", Toast.LENGTH_SHORT).show()
                    Timber.d("Endereço simplificado: $address")
                } else {
                    Toast.makeText(activity, "Endereço não encontrado.", Toast.LENGTH_SHORT).show()
                    Timber.d("Nenhum endereço encontrado.")
                }
            },
            onFailure = { exception ->
                Toast.makeText(activity, "Erro ao obter localização: ${exception.message}", Toast.LENGTH_LONG).show()
                Timber.e(exception, "Erro ao obter a localização.")
            }
        )
    }

    fun openMovieDetail(movie: Movie) {
        val intent = Intent(activity, MovieDetailActivity::class.java).apply {
            putExtra(MovieDetailActivity.EXTRA_MOVIE, movie)
        }
        activity.startActivity(intent)
    }

    fun updateMovies(movies: List<Movie>) {
        movieAdapter?.updateMovies(movies)
    }

    fun showErrorViews(message: String) {
        binding.imageViewError.visibility = View.VISIBLE
        binding.recyclerViewMovies.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun hideErrorViews() {
        binding.imageViewError.visibility = View.GONE
        binding.recyclerViewMovies.visibility = View.VISIBLE
    }
}
