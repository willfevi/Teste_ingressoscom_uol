package com.example.ingressoscom.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ingressoscom.databinding.ActivityMainBinding
import com.example.ingressoscom.presentation.repository.MovieRepository
import com.example.ingressoscom.presentation.view.util.main.FunctionsAux
import com.example.ingressoscom.presentation.view.util.main.LocationHelper
import com.example.ingressoscom.presentation.view.util.main.Network
import com.example.ingressoscom.presentation.view.util.main.PermissionHandler
import com.example.ingressoscom.presentation.view.util.main.Setup
import com.example.ingressoscom.presentation.viewmodel.MovieViewModel
import com.example.ingressoscom.presentation.viewmodel.MovieViewModelFactory
import com.google.android.gms.location.LocationServices
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationHelper: LocationHelper
    private lateinit var functionsAux: FunctionsAux
    private lateinit var setup: Setup
    private lateinit var network: Network
    private lateinit var permissionHandler: PermissionHandler

    private val repository = MovieRepository()  // Initialize your repository here
    private val viewModel: MovieViewModel by viewModels { MovieViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationHelper = LocationHelper(this, fusedLocationClient)

        network = Network(
            context = this,
            onNetworkAvailable = {
                Toast.makeText(
                    this,
                    "Conexão restaurada. Carregando filmes...",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.fetchMovies()
            },
            onNetworkLost = {
                Toast.makeText(
                    this,
                    "Conexão com a internet perdida.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        network.registerNetworkCallback()

        functionsAux = FunctionsAux(
            activity = this,
            binding = binding,
            viewModel = viewModel,
            locationHelper = locationHelper,
            network = network
        )

        setup = Setup(
            activity = this,
            binding = binding,
            functionsAux = functionsAux
        )
        setup.initialize()

        permissionHandler = PermissionHandler(this, functionsAux)
        functionsAux.setupListeners()

        observeViewModel()

        if (network.isNetworkAvailable()) {
            viewModel.fetchMovies()
        } else {
            functionsAux.showErrorViews("Sem conexão com a internet. Verifique sua rede.")
            Timber.e("Inicialização sem conexão com a internet.")
        }
    }

    private fun observeViewModel() {
        viewModel.loadingLiveData.observe(this) { isLoading ->
            if (isLoading) {
                functionsAux.hideErrorViews()
                if (!binding.swipeRefreshLayout.isRefreshing) {
                    binding.progressBar.visibility = View.VISIBLE
                }
            } else {
                binding.progressBar.visibility = View.GONE
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this, "Filmes atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.moviesLiveData.observe(this) { movies ->
            if (!movies.isNullOrEmpty()) {
                functionsAux.hideErrorViews()
                binding.recyclerViewMovies.visibility = View.VISIBLE
                functionsAux.updateMovies(movies)
            } else {
                binding.recyclerViewMovies.visibility = View.GONE
                functionsAux.showErrorViews("Nenhum filme encontrado.")
            }
        }

        viewModel.errorLiveData.observe(this) { error ->
            error?.let {
                Timber.e("Erro ao obter os filmes: $it")
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                binding.progressBar.visibility = View.GONE
                functionsAux.showErrorViews("Erro ao carregar filmes: Verifique sua conexão com a internet.")
                Toast.makeText(
                    this,
                    "Erro ao carregar filmes: Verifique sua conexão com a internet.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        network.unregisterNetworkCallback()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.handlePermissionsResult(requestCode, permissions, grantResults)
    }
}
