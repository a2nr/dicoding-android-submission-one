package io.github.a2nr.submissionmodul1

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.github.a2nr.submissionmodul1.databinding.FragmentMovieDetailBinding
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.viewmodel.AppViewModelFactory
import io.github.a2nr.submissionmodul1.viewmodel.ListMovieViewModel
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent



class DetailMovieFragment : Fragment() {
    private lateinit var vM: ListMovieViewModel
    private lateinit var movieData: MovieData
    private var isFavorite: Boolean = false
    private lateinit var binding: FragmentMovieDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        vM = ViewModelProviders.of(this, AppViewModelFactory(this.requireActivity().application))
            .get(ListMovieViewModel::class.java)
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        movieData = DetailMovieFragmentArgs.fromBundle(arguments!!).dataDetail
        binding.mediaType.text = movieData.media_type
        binding.overview.text = movieData.overview
        binding.releaseDate.text = movieData.release_date
        binding.titleTextView.text = movieData.title
        binding.voteAverage.text = movieData.vote_average.toString()
        Glide.with(this)
            .load(ListMovieViewModel.getLinkImage(movieData.poster_path))
            .into(binding.posterImageView)
        vM.isMovieExists.observe(this, Observer {
            isFavorite = it
            updateFabIcon()
        })
        binding.floatingActionButton.hide()
        binding.floatingActionButton.show()
        vM.doCheckMovieExists(movieData.id)
        binding.detailMovieFragment = this
        return binding.root
    }

    private fun updateFabIcon() {
        if (isFavorite) {
            binding.floatingActionButton.setImageDrawable(
                this.resources.getDrawable(
                    R.drawable.ic_favorite_24px,
                    this.requireContext().theme
                )
            )
        } else {
            binding.floatingActionButton.setImageDrawable(
                this.resources.getDrawable(
                    R.drawable.ic_favorite_border_24px,
                    this.requireContext().theme
                )
            )
        }
        binding.floatingActionButton.hide()
        binding.floatingActionButton.show()

    }

    fun fabOnClick() {
        var s = "Added"

        if (isFavorite) {
            vM.unmarkAsFavorite(movieData)
            s = "Removed"
        } else {
            vM.markAsFavorite(movieData)
        }
        isFavorite = !isFavorite
        updateFabIcon()

        StackImageAppWidgetProvider.sendRefresh(this.requireContext())

        Snackbar.make(
            requireView(), "${movieData.title} $s into favorite",
            Snackbar.LENGTH_SHORT
        )
            .show()
    }
}