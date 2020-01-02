package io.github.a2nr.jetpakcourse

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import io.github.a2nr.jetpakcourse.databinding.FragmentMovieDetailBinding
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.utils.EspressoIdlingResource
import io.github.a2nr.jetpakcourse.viewmodel.AppViewModelFactory
import io.github.a2nr.jetpakcourse.viewmodel.DetailMovieViewModel
import io.github.a2nr.jetpakcourse.widgetapp.StackImageAppWidgetProvider


class DetailMovieFragment : Fragment() {
    private lateinit var viewModel: DetailMovieViewModel
    private var movieData: MovieData? = null
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
            movieData = arguments?.let { arg ->
                DetailMovieFragmentArgs.fromBundle(arg).dataDetail.also {
                    mediaType.text = it.mediaType
                    overview.text = it.overview
                    releaseDate.text = it.releaseDate
                    titleTextView.text = it.title
                    voteAverage.text = it.voteAverage.toString()
                    EspressoIdlingResource.increment()
                    Glide.with(this@DetailMovieFragment)
                        .load(
                            MovieDataRepository.getLinkImage(
                                key = it.posterPath
                            )
                        )
                        .listener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean = false

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                EspressoIdlingResource.decrement()
                                return false
                            }

                        })
                        .fitCenter()
                        .into(this.posterImageView)
                }
            }
            viewModel = ViewModelProviders.of(
                this@DetailMovieFragment,
                AppViewModelFactory(this@DetailMovieFragment.requireActivity().application)
            )
                .get(DetailMovieViewModel::class.java).apply {
                    isMovieExists.observe(this@DetailMovieFragment, Observer {
                        isFavorite = it
                        updateFabIcon()
                    })
                }
            floatingActionButton.run { hide();show() }
            movieData?.let {
                viewModel.doCheckMovieExists(it.id)
            }

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

        viewModel.run {
            movieData?.let {
                if (isFavorite) unMarkAsFavorite(it) else markAsFavorite(it)
            }
        }
        Snackbar.make(
            requireView(),
            "${movieData?.title} ${if (isFavorite) "Removed" else "Added"} into favorite"
            ,
            Snackbar.LENGTH_SHORT
        )
            .show()
        isFavorite = !isFavorite
        updateFabIcon()
        StackImageAppWidgetProvider.sendRefresh(this.requireContext())

    }
}