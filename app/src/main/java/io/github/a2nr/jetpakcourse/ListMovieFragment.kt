package io.github.a2nr.jetpakcourse

import android.content.Intent
import android.content.res.Configuration
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
import io.github.a2nr.jetpakcourse.adapter.ItemMovieAdapter
import io.github.a2nr.jetpakcourse.databinding.FragmentListMovieBinding
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.viewmodel.AppViewModelFactory
import io.github.a2nr.jetpakcourse.viewmodel.ListMovieViewModel
import java.text.SimpleDateFormat
import java.util.*
import io.github.a2nr.jetpakcourse.viewmodel.ListMovieViewModel as ListMovieViewModel1

class ListMovieFragment : Fragment() {
    companion object {
        const val NOTIFICATION_FEEDBACK = "ListMovieFragment.NOTIFICATION"
    }

    private var onClickItemView: ((v: View, p: Int) -> Unit)? = null
    private lateinit var lMd: List<MovieData>
    private lateinit var viewModel: ListMovieViewModel
    private lateinit var binding: FragmentListMovieBinding
    private lateinit var typeMenu: MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel =
            ViewModelProviders.of(this, AppViewModelFactory(this.requireActivity().application))
                .get(ListMovieViewModel1::class.java)

        this.activity?.intent?.let {
            if (it.action == NOTIFICATION_FEEDBACK) {
                viewModel.typeTag = R.id.type_release_now
            }
        }
        this.view?.findNavController()?.let {
            NavigationUI.setupActionBarWithNavController(
                this@ListMovieFragment.requireActivity() as AppCompatActivity
                , it
            )
        }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel.listMovieData.observe(this, Observer {
            binding.apply {
                val adapter = ItemMovieAdapter(this@ListMovieFragment.requireContext())
                listMovie.visibility = RecyclerView.VISIBLE
                progressBarDataReady.visibility = ProgressBar.INVISIBLE
                onClickItemView = { view, pos ->
                    Log.i("ListMovieFragment", "Item Clicked at $pos")
                    view.findNavController().navigate(
                        ListMovieFragmentDirections.actionListMovieFragmentToDetailMovieFragment(
                            lMd[pos]
                        )
                    )
                }
                listMovie.adapter = adapter.apply {
                    setCallBack(onClickItemView)
                    submitData(it)
                }
            }
            lMd = it
        })
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentListMovieBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ListMovieFragment
            (this@ListMovieFragment.activity as AppCompatActivity)
                .setSupportActionBar(toolbar)
            listMovie.apply {
                val adapter = ItemMovieAdapter(this@ListMovieFragment.requireContext())
                addItemDecoration(adapter.ItemDecoration(resources.getDimension(R.dimen.activity_horizontal_margin).toInt()))
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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() {
        if (!viewModel.listMovieData.value.isNullOrEmpty()) {
            binding.apply {
                listMovie.visibility = RecyclerView.VISIBLE
                progressBarDataReady.visibility = ProgressBar.INVISIBLE
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
                else ->
                    LinearLayoutManager(this@ListMovieFragment.requireContext())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        typeMenu = menu.findItem(R.id.type_menu)
        when (viewModel.typeTag) {
            null -> viewModel.typeTag = R.id.type_movie
        }
        viewModel.typeTag?.let {
            menu.performIdentifierAction(it, 0)
            it
        }
        (menu.findItem(R.id.search).actionView as SearchView)
            .setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i("onQueryTextSubmit", "triggered?")
                    query?.apply {
                        viewModel.doSearchMovie(
                            run {
                                when (viewModel.typeTag) {
                                    R.id.type_tv_show -> MovieDataRepository.TV
                                    else -> MovieDataRepository.MOVIE
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
            binding.apply {
                when (item.itemId) {
                    R.id.setting -> {
                        this@ListMovieFragment.view?.findNavController()?.navigate(
                            ListMovieFragmentDirections
                                .actionListMovieFragmentToSettingFragment()
                        )
                    }
                    R.id.change_lang -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    }
                    R.id.type_movie -> {
                        typeMenu.title = item.title
                        viewModel.typeTag = R.id.type_movie
                        viewModel.doGetMovies(
                            MovieDataRepository.MOVIE
                            , "day", resources.getString(R.string.lang_code)
                        )
                        listMovie.visibility = RecyclerView.INVISIBLE
                        progressBarDataReady.visibility = ProgressBar.VISIBLE
                    }
                    R.id.type_tv_show -> {
                        typeMenu.title = item.title
                        viewModel.typeTag = R.id.type_tv_show
                        viewModel.doGetMovies(
                            MovieDataRepository.TV
                            , "day", resources.getString(R.string.lang_code)
                        )
                        listMovie.visibility = RecyclerView.INVISIBLE
                        progressBarDataReady.visibility = ProgressBar.VISIBLE
                    }
                    R.id.type_my_favorite -> {
                        typeMenu.title = item.title
                        viewModel.typeTag = R.id.type_my_favorite
                        viewModel.doGetFavorite()
                        listMovie.visibility = RecyclerView.INVISIBLE
                        progressBarDataReady.visibility = ProgressBar.VISIBLE
                    }
                    R.id.type_release_now -> {
                        typeMenu.title = item.title
                        viewModel.doGetReleaseMovie(
                            SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(Calendar.getInstance().time)
                        )
                        listMovie.visibility = RecyclerView.INVISIBLE
                        progressBarDataReady.visibility = ProgressBar.VISIBLE
                    }
                }
            }
            true
        }
    }

}