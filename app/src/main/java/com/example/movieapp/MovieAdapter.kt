package com.example.movieapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.MovieItemLayoutBinding

class MovieAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    var moviesList = ArrayList<Movie>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MovieItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moviesList[position])
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    inner class ViewHolder(private val binding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            ImageTransformer.loadImageFromUrl(
                itemView.context,
                movie.imageUrl,
                binding.movieCardImageView
            )
            binding.movie = movie
            binding.movieCardClickListener = View.OnClickListener {
                val movieDetailsFragment = MovieDetailsFragment(movie)
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, movieDetailsFragment)
                    .addToBackStack("MovieDetailsFragmentNavigation")
                    .commit()
            }
            binding.executePendingBindings()
        }
    }
}
