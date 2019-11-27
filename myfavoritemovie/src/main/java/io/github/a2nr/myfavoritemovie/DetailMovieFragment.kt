package io.github.a2nr.myfavoritemovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.github.a2nr.myfavoritemovie.databinding.FragmentMovieDetailBinding
import io.github.a2nr.myfavoritemovie.repository.MovieData
import io.github.a2nr.myfavoritemovie.viewmodel.AppViewModelFactory
import io.github.a2nr.myfavoritemovie.viewmodel.ListMovieViewModel


class DetailMovieFragment : Fragment() {
    private lateinit var vM: ListMovieViewModel
    private lateinit var movieData: MovieData
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
                .get(ListMovieViewModel::class.java)
            floatingActionButton.run {
                setImageDrawable(
                    this.resources.getDrawable(
                        R.drawable.ic_favorite_24px,
                        this@DetailMovieFragment.requireContext().theme
                    )
                )
                hide(); show()
            }
        }.root
    }

    fun fabOnClick() {

        vM.unmarkAsFavorite(movieData.id)
        Snackbar.make(
            requireView(),
            "${movieData.title} is Removed"
            ,
            Snackbar.LENGTH_SHORT
        )
            .show()
        findNavController().popBackStack()
    }
}