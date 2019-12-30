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
import io.github.a2nr.jetpakcourse.databinding.FragmentListMovieBinding
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.viewmodel.AppViewModelFactory
import io.github.a2nr.jetpakcourse.viewmodel.ListMovieViewModel
import java.text.SimpleDateFormat
import java.util.*
import io.github.a2nr.jetpakcourse.viewmodel.ListMovieViewModel as ListMovieViewModel1

class ListMovieFragment : Fragment(),
    SearchView.OnQueryTextListener {
    companion object {
        const val NOTIFICATION_FEEDBACK = "ListMovieFragment.NOTIFICATION"
    }

    private lateinit var viewModel: ListMovieViewModel
    private lateinit var binding: FragmentListMovieBinding
    private lateinit var typeMenu: MenuItem
    private lateinit var adapter: ItemMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel =
            ViewModelProviders.of(this, AppViewModelFactory(this.requireActivity().application))
                .get(ListMovieViewModel1::class.java)
                .apply {
                    typeTag = R.id.type_movie
                }

        activity?.intent?.let {
            if (it.action == NOTIFICATION_FEEDBACK) {
                viewModel.typeTag = R.id.type_release_now
            }
        }
        val itemClicked: (View, MovieData) -> Unit = { view, data ->
            Log.i("ListMovieFragment", "Item Clicked at ${data.id}")
            view.findNavController().navigate(
                ListMovieFragmentDirections.actionListMovieFragmentToDetailMovieFragment(
                    data
                )
            )
        }
        adapter = ItemMovieAdapter(requireContext(),itemClicked)

        view?.let{
            NavigationUI.setupActionBarWithNavController(
                requireActivity() as AppCompatActivity
                , it.findNavController()
            )
        }
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentListMovieBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ListMovieFragment
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            listMovie.apply {
                addItemDecoration(
                    ItemMovieDecoration(
                        requireContext(),
                        resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
                    )
                )
                layoutManager = when (this.resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT ->
                        LinearLayoutManager(this@ListMovieFragment.requireContext())
                    else ->
                        GridLayoutManager(this@ListMovieFragment.requireContext(), 2)
                }
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.listMovie.adapter = adapter
        viewModel.listMovieData.observe(this, Observer {
            adapter.submitList(it)
            binding.apply {
                listMovie.visibility = RecyclerView.VISIBLE
                progressBarDataReady.visibility = ProgressBar.INVISIBLE
            }
        })
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
        binding.listMovie.layoutManager = when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(requireContext(), 2)
            else -> LinearLayoutManager(requireContext())
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.i("onQueryTextSubmit", "triggered?")
        newText?.let {
            viewModel.doSearchMovie(
                when (viewModel.typeTag) {
                    R.id.type_tv_show -> MovieDataRepository.TV
                    else -> MovieDataRepository.MOVIE
                }, it, resources.getString(R.string.lang_code)
            )
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        typeMenu = menu.findItem(R.id.type_menu)
        viewModel.typeTag?.let {
            menu.performIdentifierAction(it, 0)
            it
        }
        (menu.findItem(R.id.search).actionView as SearchView).setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = binding.apply {
        when (item.itemId) {
            R.id.type_movie -> {
                viewModel.typeTag = R.id.type_movie
                viewModel.doGetMovies(
                    MovieDataRepository.MOVIE
                    , "day", resources.getString(R.string.lang_code)
                )
            }
            R.id.type_tv_show -> {
                viewModel.typeTag = R.id.type_tv_show
                viewModel.doGetMovies(
                    MovieDataRepository.TV
                    , "day", resources.getString(R.string.lang_code)
                )
            }
            R.id.type_my_favorite -> {
                viewModel.typeTag = R.id.type_my_favorite
                viewModel.doGetFavorite()
            }
            R.id.type_release_now -> {
                viewModel.doGetReleaseMovie(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Calendar.getInstance().time)
                )
            }
        }
    }.run{
        when(item.itemId){
            R.id.setting -> {
                view?.findNavController()?.navigate(
                    ListMovieFragmentDirections
                        .actionListMovieFragmentToSettingFragment()
                )
            }
            R.id.change_lang -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            else ->{
                typeMenu.title = item.title
                listMovie.visibility = RecyclerView.INVISIBLE
                progressBarDataReady.visibility = ProgressBar.VISIBLE
            }
        }
        true
    }

}