package io.github.a2nr.myfavoritemovie

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.a2nr.myfavoritemovie.adapter.ItemMovieAdapter
import io.github.a2nr.myfavoritemovie.databinding.FragmentListMovieBinding
import io.github.a2nr.myfavoritemovie.repository.MovieData
import io.github.a2nr.myfavoritemovie.viewmodel.AppViewModelFactory
import io.github.a2nr.myfavoritemovie.viewmodel.ListMovieViewModel

class ListMovieFragment : Fragment() {
    private var onClickItemView: ((v: View, p: Int) -> Unit)? = null
    private lateinit var lMd: List<MovieData>
    private lateinit var vM: ListMovieViewModel
    private lateinit var binding: FragmentListMovieBinding
//    private lateinit var typeMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        vM = ViewModelProviders.of(this, AppViewModelFactory(this.requireActivity().application))
            .get(ListMovieViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)


        this.view?.findNavController()?.let {
            NavigationUI.setupActionBarWithNavController(
                this@ListMovieFragment.requireActivity() as AppCompatActivity
                , it
            )
        }

        binding = FragmentListMovieBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ListMovieFragment
            (this@ListMovieFragment.activity as AppCompatActivity)
                .setSupportActionBar(toolbar)
            listMovie.apply {
                addItemDecoration(ItemDecoration(resources.getDimension(R.dimen.activity_horizontal_margin).toInt()))
                layoutManager = run {
                    when (this.resources.configuration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT ->
                            LinearLayoutManager(this@ListMovieFragment.requireContext())
                        //Configuration.ORIENTATION_LANDSCAPE
                        else ->
                            GridLayoutManager(this@ListMovieFragment.requireContext(), 2)
                    }
                }
            }
        }
        val obs = Observer<List<MovieData>> {
            binding.apply {
                listMovie.visibility = RecyclerView.VISIBLE
                progressBar.visibility = ProgressBar.INVISIBLE
                onClickItemView = { view, pos ->
                    Log.i("ListMovieFragment", "Item Clicked at $pos")
                    view.findNavController().navigate(
                        ListMovieFragmentDirections.actionListMovieFragmentToDetailMovieFragment(
                            lMd[pos]
                        )
                    )
                }
                listMovie.adapter =
                    ItemMovieAdapter(this@ListMovieFragment.requireContext())
                        .apply {
                            setCallBack(onClickItemView)
                            submitData(it)
                        }
            }
            lMd = it
        }
        vM.listMovieData.observe(this, obs)
        setHasOptionsMenu(true)
        vM.doGetFavorite()
        return binding.root
    }

    override fun onResume() {
        if (!vM.listMovieData.value.isNullOrEmpty()) {
            binding.apply {
                listMovie.visibility = RecyclerView.VISIBLE
                progressBar.visibility = ProgressBar.INVISIBLE
            }
        }
        super.onResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.listMovie.layoutManager = run {
            when (newConfig.orientation) {
                Configuration.ORIENTATION_LANDSCAPE ->
                    GridLayoutManager(this@ListMovieFragment.requireContext(), 2)
                // Configuration.ORIENTATION_PORTRAIT
                else ->
                    LinearLayoutManager(this@ListMovieFragment.requireContext())
            }
        }
    }

    override fun onPause() {
        binding.apply {
            progressBar.visibility = ProgressBar.VISIBLE
            listMovie.visibility = RecyclerView.INVISIBLE
        }
        super.onPause()
    }

    inner class ItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                val pos = parent.getChildAdapterPosition(view) + 1
                if (this@ListMovieFragment.resources.configuration.orientation
                    == Configuration.ORIENTATION_PORTRAIT
                ) {
                    if (pos == 1) top = margin
                } else {
                    if ((pos == 1) || (pos == 2)) top = margin
                }
                left = margin
                right = margin
                bottom = margin

            }
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.main_menu, menu)
//        typeMenu = menu.findItem(R.id.type_menu)
//        menu.performIdentifierAction(R.id.type_my_favorite, 0)
//        super.onCreateOptionsMenu(menu, inflater)
//    }

}