package io.github.a2nr.submissionmodul1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.a2nr.submissionmodul1.viewmodel.AppViewModelFactory
import io.github.a2nr.submissionmodul1.viewmodel.ListMovieViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AlarmReceiver.createRemainderRelease(this)
        AlarmReceiver.createRemainderDaily(this)
    }
}
