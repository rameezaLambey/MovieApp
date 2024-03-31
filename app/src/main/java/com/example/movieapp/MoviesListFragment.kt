package com.example.movieapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.databinding.MoviesListLayoutBinding

class MoviesListFragment: Fragment() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var binding: MoviesListLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MoviesListLayoutBinding.inflate(LayoutInflater.from(context))
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adapter = MovieAdapter(requireActivity())
        movieViewModel.moviesLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.moviesList = it as ArrayList<Movie>
                binding.moviesRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.moviesRecyclerView.adapter = adapter

                binding.swipeRefreshView.isRefreshing = false
            }
        }
        movieViewModel.fetchMovieData()

        binding.swipeRefreshView.setOnRefreshListener {
            adapter.moviesList = ArrayList()
            movieViewModel.fetchMovieData()
        }
        return binding.root
    }
}
