package io.github.a2nr.submissionmodul1

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.a2nr.submissionmodul1.adapter.ItemMovieAdapter
import io.github.a2nr.submissionmodul1.databinding.FragmentListMovieBinding
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.viewmodel.AppViewModelFactory
import io.github.a2nr.submissionmodul1.viewmodel.ListMovieViewModel

class ListMovieFragment : Fragment() {
    private var onClickItemView: ((v: View, p: Int) -> Unit)? = null
    private lateinit var lMd: List<MovieData>
    private lateinit var vM: ListMovieViewModel
    private lateinit var binding: FragmentListMovieBinding
    private lateinit var typeMenu: MenuItem
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)


        this.view?.findNavController()?.let{
            NavigationUI.setupActionBarWithNavController(
                this@ListMovieFragment.requireActivity() as AppCompatActivity
                ,it
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
        vM = ViewModelProviders.of(this, AppViewModelFactory(this.requireActivity().application))
            .get(ListMovieViewModel::class.java).apply {
                listMovieData.observe(this@ListMovieFragment,
                    Observer {
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
                    })
            }
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        typeMenu = menu.findItem(R.id.type_menu)
        when (vM.typeTag) {
            null -> vM.typeTag = R.id.type_movie
        }
        vM.typeTag?.let {
            menu.performIdentifierAction(it, 0)
            it
        }
        (menu.findItem(R.id.search).actionView as SearchView)
            .setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i("onQueryTextSubmit", "triggered?")
                    query?.apply {
                        vM.doSearchMovie(
                            run {
                                when (vM.typeTag) {
                                    R.id.type_tv_show -> ListMovieViewModel.TV
                                    else -> ListMovieViewModel.MOVIE
                                }
                            },
                            query, resources.getString(R.string.lang_code)
                        )
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.i("onQueryTextChange", "triggered?")
                    return true
                }
            })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return let {
            when (item.itemId) {
                R.id.setting -> {
                    this.view?.findNavController()?.navigate(
                        ListMovieFragmentDirections
                            .actionListMovieFragmentToSettingFragment()
                    )
                }
                R.id.change_lang -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                }
                R.id.type_movie -> {
                    typeMenu.title = item.title
                    vM.doGetMovies(
                        ListMovieViewModel.MOVIE
                        , "day", resources.getString(R.string.lang_code)
                    )
                }
                R.id.type_tv_show -> {
                    typeMenu.title = item.title
                    vM.doGetMovies(
                        ListMovieViewModel.TV
                        , "day", resources.getString(R.string.lang_code)
                    )
                }
                R.id.type_my_favorite -> {
                    typeMenu.title = item.title
                    vM.doGetFavorite()
                }
            }
            true
        }
    }

}