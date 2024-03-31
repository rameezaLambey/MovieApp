package com.example.movieapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.movieapp.databinding.MovieDetailsLayoutBinding

class MovieDetailsFragment(private val movie: Movie) : Fragment() {

    private lateinit var binding: MovieDetailsLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MovieDetailsLayoutBinding.inflate(inflater, container, false)
        ImageTransformer.loadImageFromUrl(
            requireContext(),
            movie.imageUrl,
            binding.movieDetailsImageView
        )
        binding.movie = movie
        return binding.root
    }
}
