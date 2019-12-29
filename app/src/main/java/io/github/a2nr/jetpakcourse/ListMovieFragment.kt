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
import io.github.a2nr.jetpakcourse.adapter.ItemMovieDecoration
import io.github.a2nr.jetpakcourse.adapter.ItemMovieListAdapter
import io.github.a2nr.jetpakcourse.databinding.FragmentListMovieBinding
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.viewmodel.AppViewModelFactory
import io.github.a2nr.jetpakcourse.viewmodel.ListMovieViewModel
import java.text.SimpleDateFormat
import java.util.*
import io.github.a2nr.jetpakcourse.viewmodel.ListMovieViewModel as ListMovieViewModel1

class ListMovieFragment :
    SearchView.OnQueryTextListener, Fragment() {
    companion object {
        const val NOTIFICATION_FEEDBACK = "ListMovieFragment.NOTIFICATION"
    }

    private lateinit var viewModel: ListMovieViewModel
    private lateinit var binding: FragmentListMovieBinding
    private lateinit var typeMenu: MenuItem

    private lateinit var itemMovieListAdapter: ItemMovieListAdapter
    private lateinit var itemMovieAdapter: ItemMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders
            .of(this, AppViewModelFactory(this.requireActivity().application))
            .get(ListMovieViewModel1::class.java)
        if (viewModel.typeTag.value == null)
            viewModel.setTypeTag(R.id.type_movie)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentListMovieBinding.inflate(inflater, container, false)
        view?.let {
            NavigationUI.setupActionBarWithNavController(
                requireActivity() as AppCompatActivity,
                it.findNavController()
            )
        }
        binding.listMovie.addItemDecoration(
            ItemMovieDecoration(
                requireContext(),
                resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
            )
        )
        binding.listMovie.layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> LinearLayoutManager(requireContext())
            else -> GridLayoutManager(requireContext(), 2)
        }
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        this.activity?.intent?.let {
            if (it.action == NOTIFICATION_FEEDBACK) {
                viewModel.setTypeTag(R.id.type_release_now)
            }
        }
        val itemClickCallBack: ((v: View, data: MovieData) -> Unit) = { view, data ->
            Log.i("ListMovieFragment", "Item Clicked at ${data.id}")
            view.findNavController().navigate(
                ListMovieFragmentDirections.actionListMovieFragmentToDetailMovieFragment(data)
            )
        }
        itemMovieListAdapter = ItemMovieListAdapter(requireContext(), itemClickCallBack)
        viewModel.typeTag.observe(this, Observer {
            if (it != R.id.type_my_favorite) {
                if (!viewModel.pageListMovie.hasObservers()) {
                    binding.listMovie.adapter = itemMovieListAdapter
                    viewModel.pageListMovie.observe(this, Observer {
                        itemMovieListAdapter.submitList(it)
                        activity?.runOnUiThread {
                            binding.apply {
                                listMovie.visibility = RecyclerView.VISIBLE
                                progressBarDataReady.visibility = ProgressBar.INVISIBLE
                            }
                        }
                    })
                }
            } else {
                if (!viewModel.listMovieData.hasObservers())
                    viewModel.listMovieData.observe(this, Observer {
                        itemMovieAdapter = ItemMovieAdapter(requireContext(), it, itemClickCallBack)
                        binding.listMovie.adapter = itemMovieAdapter
                        activity?.runOnUiThread {
                            binding.apply {
                                listMovie.visibility = RecyclerView.VISIBLE
                                progressBarDataReady.visibility = ProgressBar.INVISIBLE
                            }
                        }
                    })
            }
        })

        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        if (!viewModel.pageListMovie.value.isNullOrEmpty()) {
            binding.apply {
                listMovie.visibility = RecyclerView.VISIBLE
                progressBarDataReady.visibility = ProgressBar.INVISIBLE
            }
        }
        super.onResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.listMovie.layoutManager = when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(requireContext(), 2)
            else -> LinearLayoutManager(requireContext())
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean = query?.apply {
        Log.i("onQueryTextSubmit", "triggered?")
        if (!query.isNullOrEmpty())
            viewModel.doSearchMovie(
                when (viewModel.typeTag.value) {
                    R.id.type_tv_show -> MovieDataRepository.TV
                    else -> MovieDataRepository.MOVIE
                },
                query, resources.getString(R.string.lang_code)
            )
    }.run { true }

    override fun onQueryTextChange(newText: String?): Boolean {
        return onQueryTextSubmit(newText)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        typeMenu = menu.findItem(R.id.type_menu)
        viewModel.typeTag.value?.let { menu.performIdentifierAction(it, 0) }
        (menu.findItem(R.id.search).actionView as SearchView).setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = binding.apply {
        when (item.itemId) {
            R.id.setting -> {
                root.findNavController().navigate(
                    ListMovieFragmentDirections
                        .actionListMovieFragmentToSettingFragment()
                )
            }
            R.id.change_lang -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.type_movie -> {
                typeMenu.title = item.title
                viewModel.setTypeTag(R.id.type_movie)
                viewModel.doGetMovies(
                    MovieDataRepository.MOVIE
                    , "day", resources.getString(R.string.lang_code)
                )
                listMovie.visibility = RecyclerView.INVISIBLE
                progressBarDataReady.visibility = ProgressBar.VISIBLE
            }
            R.id.type_tv_show -> {
                typeMenu.title = item.title
                viewModel.setTypeTag(R.id.type_tv_show)
                viewModel.doGetMovies(
                    MovieDataRepository.TV
                    , "day", resources.getString(R.string.lang_code)
                )
                listMovie.visibility = RecyclerView.INVISIBLE
                progressBarDataReady.visibility = ProgressBar.VISIBLE
            }
            R.id.type_my_favorite -> {
                typeMenu.title = item.title
                viewModel.setTypeTag(R.id.type_my_favorite)
                viewModel.doGetFavorite()
                listMovie.visibility = RecyclerView.INVISIBLE
                progressBarDataReady.visibility = ProgressBar.VISIBLE
            }
            R.id.type_release_now -> {
                typeMenu.title = item.title
                viewModel.setTypeTag(R.id.type_release_now)
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
    }.run { super.onOptionsItemSelected(item) }

}
