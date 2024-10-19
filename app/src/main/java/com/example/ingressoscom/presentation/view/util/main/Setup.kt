package com.example.ingressoscom.presentation.view.util.main

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ingressoscom.databinding.ActivityMainBinding
import com.example.ingressoscom.presentation.view.adapter.MovieAdapter

class Setup(
    private val activity: AppCompatActivity,
    private val binding: ActivityMainBinding,
    private val functionsAux: FunctionsAux
) {

    fun initialize() {
        setupToolbar()
        setupRecyclerView()
        setupBottomNavigationView()
    }

    private fun setupToolbar() {
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMovies.layoutManager = GridLayoutManager(activity, 3)
        val movieAdapter = MovieAdapter(emptyList()) { selectedMovie ->
            functionsAux.openMovieDetail(selectedMovie)
        }
        binding.recyclerViewMovies.adapter = movieAdapter
        functionsAux.setAdapter(movieAdapter)
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            functionsAux.handleBottomNavigationItemSelected(item)
        }
    }
}
