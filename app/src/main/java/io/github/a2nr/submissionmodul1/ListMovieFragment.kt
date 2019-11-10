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
        binding = FragmentListMovieBinding.inflate(inflater, container, false)

        (this.activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        val navCon = this.view?.findNavController()
        navCon?.let {
            NavigationUI.setupActionBarWithNavController(
                this.requireActivity() as AppCompatActivity
                , it
            )
        }

        vM = ViewModelProviders.of(this, AppViewModelFactory(this.requireActivity().application))
            .get(ListMovieViewModel::class.java)
        binding.lifecycleOwner = this
        val adapter = ItemMovieAdapter(this.requireContext())
        onClickItemView = { view, pos ->
            Log.i("ListMovieFragment", "Item Clicked at $pos")
            view.findNavController()
                .navigate(
                    ListMovieFragmentDirections
                        .actionListMovieFragmentToDetailMovieFragment(lMd[pos])
                )
        }

        val itemDecoration =
            ItemDecoration(resources.getDimension(R.dimen.activity_horizontal_margin).toInt())
        binding.listMovie.addItemDecoration(itemDecoration)
        when (this.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT ->
                binding.listMovie.layoutManager = LinearLayoutManager(this.requireContext())
            Configuration.ORIENTATION_LANDSCAPE ->
                binding.listMovie.layoutManager = GridLayoutManager(this.requireContext(), 2)
        }
        vM.listMovieData.observe(this, Observer {
            binding.listMovie.visibility = RecyclerView.VISIBLE
            binding.progressBar.visibility = ProgressBar.INVISIBLE
            binding.listMovie.adapter = adapter.apply {
                this.setCallBack(onClickItemView)
                this.submitData(it)
            }
            lMd = it
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() {
        if (!vM.listMovieData.value.isNullOrEmpty()) {
            binding.listMovie.visibility = RecyclerView.VISIBLE
            binding.progressBar.visibility = ProgressBar.INVISIBLE
        }
        super.onResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                binding.listMovie.layoutManager = GridLayoutManager(this.requireContext(), 2)
            Configuration.ORIENTATION_PORTRAIT ->
                binding.listMovie.layoutManager = LinearLayoutManager(this.requireContext())
        }
    }

    override fun onPause() {
        binding.progressBar.visibility = ProgressBar.VISIBLE
        binding.listMovie.visibility = RecyclerView.INVISIBLE
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
                if (this@ListMovieFragment
                        .resources
                        .configuration
                        .orientation == Configuration.ORIENTATION_PORTRAIT
                ) {
                    if (pos == 1)
                        top = margin
                } else {
                    if ((pos == 1) ||
                        (pos == 2)
                    )
                        top = margin
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
                    var media_type = ListMovieViewModel.MOVIE

                    when(vM.typeTag){
                        R.id.type_tv_show -> {media_type =ListMovieViewModel.TV}
                    }

                    vM.doSearchMovie(
                        media_type,
                        query,
                        resources.getString(R.string.lang_code)
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