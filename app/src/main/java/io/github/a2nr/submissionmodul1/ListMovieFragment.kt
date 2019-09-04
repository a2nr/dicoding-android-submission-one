package io.github.a2nr.submissionmodul1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import io.github.a2nr.submissionmodul1.adapter.ItemMovieAdapter
import io.github.a2nr.submissionmodul1.databinding.FragmentListMovieBinding
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.viewModel.AppViewModelFactory
import io.github.a2nr.submissionmodul1.viewModel.ListMovieViewModel

class ListMovieFragment : Fragment() {
    private var onClickItemView : ((v:View, p: Int)->Unit)?=null
    private lateinit var lMd : List<MovieData>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentListMovieBinding.inflate(inflater, container, false)

        val vmf = AppViewModelFactory(this.requireActivity().application)
        val vM = ViewModelProviders.of(this, vmf).get(ListMovieViewModel::class.java)
        binding.lifecycleOwner = this
        val adapter = ItemMovieAdapter(this.requireContext())
        onClickItemView={ view, pos ->
            Log.i("ListMovieFragment","Item Clicked at $pos")
            view.findNavController()
                .navigate(ListMovieFragmentDirections
                    .actionListMovieFragmentToDetailMovieFragment(lMd[pos]))
        }
        vM.listMovieData.observe(this, Observer {
            adapter.setCallBack(onClickItemView)
            binding.listMovie.adapter = adapter.submitData(it)
            lMd = it
        })
        vM.fetchMovieData("movie", "day")
        return binding.root
    }
}