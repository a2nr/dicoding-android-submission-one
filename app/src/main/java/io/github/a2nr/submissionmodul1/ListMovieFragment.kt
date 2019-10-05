package io.github.a2nr.submissionmodul1

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.github.a2nr.submissionmodul1.adapter.ItemMovieAdapter
import io.github.a2nr.submissionmodul1.databinding.FragmentListMovieBinding
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.viewModel.AppViewModelFactory
import io.github.a2nr.submissionmodul1.viewModel.ListMovieViewModel

class ListMovieFragment : Fragment() {
    private var onClickItemView : ((v:View, p: Int)->Unit)?=null
    private lateinit var lMd : List<MovieData>
    private lateinit var vM : ListMovieViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentListMovieBinding.inflate(inflater, container, false)

        val tab = binding.tabs
        val listenerTab = this.TabListener()
        tab.addOnTabSelectedListener(listenerTab)

        (this.activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        val navCon = this.view?.findNavController()
        navCon?.let {
            NavigationUI.setupActionBarWithNavController(this.requireActivity() as AppCompatActivity
                , it
            )
        }


        val vmf = AppViewModelFactory(this.requireActivity().application)
        vM = ViewModelProviders.of(this, vmf).get(ListMovieViewModel::class.java)
        binding.lifecycleOwner = this
        val adapter = ItemMovieAdapter(this.requireContext())
        onClickItemView={ view, pos ->
            Log.i("ListMovieFragment","Item Clicked at $pos")
            view.findNavController()
                .navigate(ListMovieFragmentDirections
                    .actionListMovieFragmentToDetailMovieFragment(lMd[pos]))
        }
        val itemDecoration = ItemDecoration(resources.getDimension(R.dimen.activity_horizontal_margin).toInt())
        binding.listMovie.addItemDecoration(itemDecoration)
        binding.listMovie.layoutManager = LinearLayoutManager(this.requireContext())
        vM.listMovieData.observe(this, Observer {
            binding.listMovie.adapter = adapter.apply {
                this.setCallBack(onClickItemView)
                // object data dipastikan ada dari database
                // kosong atau gak nya  bakal dicek ketika adapter dieksekusi
                // kusus nya pada method .getItemCount()
                this.submitData(it)
            }
            lMd = it
        })
        listenerTab.selectType(resources.getString(R.string.movie))
        setHasOptionsMenu(true)
        return binding.root
    }

    class ItemDecoration(private val margin :Int) : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = margin
                }
                left =  margin
                right = margin
                bottom = margin
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.change_lang->{
                val inten = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(inten)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    inner class TabListener : TabLayout.OnTabSelectedListener{
        fun selectType(s:String) {
            when(s){
                resources.getString(R.string.movie)->{
                    vM.fetchMovieData(ListMovieViewModel.MOVIE
                        , "day",resources.getString(R.string.lang_code))
                }
                resources.getString(R.string.tv_show)-> {
                    vM.fetchMovieData(ListMovieViewModel.TV
                        , "day",resources.getString(R.string.lang_code))
                }
            }
        }
        override fun onTabReselected(p0: TabLayout.Tab?) {
            Log.i("TabListener","onTabReselected...")
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
            Log.i("TabListener","onTabUnselected...")
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            Log.i("TabListener","onTabSelected...")
            selectType(p0?.text.toString())
        }


    }

}