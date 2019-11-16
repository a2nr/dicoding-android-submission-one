package io.github.a2nr.submissionmodul1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.github.a2nr.submissionmodul1.databinding.FragmentMovieDetailBinding
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.viewmodel.AppViewModelFactory
import io.github.a2nr.submissionmodul1.viewmodel.ListMovieViewModel


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
        return FragmentMovieDetailBinding.inflate(inflater, container, false).apply {
            binding = this
            detailMovieFragment = this@DetailMovieFragment
            movieData = DetailMovieFragmentArgs.fromBundle(arguments!!).dataDetail.also {
                mediaType.text = it.media_type
                overview.text = it.overview
                releaseDate.text = it.release_date
                titleTextView.text = it.title
                voteAverage.text = it.vote_average.toString()
                Glide.with(this@DetailMovieFragment)
                    .load(ListMovieViewModel.getLinkImage(it.poster_path))
                    .into(this.posterImageView)
            }
            vM = ViewModelProviders.of(
                this@DetailMovieFragment,
                AppViewModelFactory(this@DetailMovieFragment.requireActivity().application)
            )
                .get(ListMovieViewModel::class.java).apply {
                    isMovieExists.observe(this@DetailMovieFragment, Observer {
                        isFavorite = it
                        updateFabIcon()
                    })
                }
            floatingActionButton.run { hide();show() }
            vM.doCheckMovieExists(movieData.id)
        }.root
    }

    private fun updateFabIcon() {
        binding.floatingActionButton.run {
            setImageDrawable(
                this.resources.getDrawable(
                    if (isFavorite) R.drawable.ic_favorite_24px else R.drawable.ic_favorite_border_24px,
                    this@DetailMovieFragment.requireContext().theme
                )
            )
            hide(); show()
        }

    }

    fun fabOnClick() {

        vM.run {
            if (isFavorite) unmarkAsFavorite(movieData) else markAsFavorite(movieData)
        }
        Snackbar.make(
            requireView(),
            "${movieData.title} ${if (isFavorite) "Removed" else "Added"} into favorite"
            ,
            Snackbar.LENGTH_SHORT
        )
            .show()
        isFavorite = !isFavorite
        updateFabIcon()
        StackImageAppWidgetProvider.sendRefresh(this.requireContext())

    }
}