package io.github.a2nr.submissionmodul1.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AppViewModelFactory (private val application: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListMovieViewModel::class.java))
        {
            return ListMovieViewModel(application) as T
        }
        throw IllegalArgumentException("Unsupported ViewModel")
    }
}