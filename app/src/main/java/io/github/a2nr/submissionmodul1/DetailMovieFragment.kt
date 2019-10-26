package io.github.a2nr.submissionmodul1

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        vM = ViewModelProviders.of(this, AppViewModelFactory(this.requireActivity().application))
            .get(ListMovieViewModel::class.java)
        val binding =
            FragmentMovieDetailBinding.inflate(inflater, container, false)
        (this.activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        movieData = DetailMovieFragmentArgs.fromBundle(arguments!!).dataDetail
        binding.mediaType.text = movieData.media_type
        binding.overview.text = movieData.overview
        binding.releaseDate.text = movieData.release_date
        binding.titleTextView.text = movieData.title
        binding.voteAverage.text = movieData.vote_average.toString()
        Glide.with(this)
            .load(ListMovieViewModel.getLinkImage(movieData.poster_path))
            .into(binding.posterImageView)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return let {
            when (item.itemId) {
                R.id.mark_favorite -> {
                    vM.markAsFavorite(movieData)
                    Snackbar
                        .make(
                            requireView(),
                            "${movieData.title} Added into favorite",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                }
            }
            true
        }
    }
}