package io.github.a2nr.submissionmodul1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.github.a2nr.submissionmodul1.databinding.FragmentMovieDetailBinding
import io.github.a2nr.submissionmodul1.viewmodel.ListMovieViewModel

class DetailMovieFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding =
            FragmentMovieDetailBinding.inflate(inflater,container,false)
        val arg = DetailMovieFragmentArgs.fromBundle(arguments!!)
        binding.mediaType.text = arg.dataDetail.media_type
        binding.overview.text = arg.dataDetail.overview
        binding.releaseDate.text = arg.dataDetail.release_date
        binding.titleTextView.text = arg.dataDetail.title
        binding.voteAverage.text = arg.dataDetail.vote_average.toString()
        Glide.with(this)
            .load(ListMovieViewModel.getLinkImage(arg.dataDetail.poster_path))
            .into(binding.posterImageView)
        return binding.root
    }
}