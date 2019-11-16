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

        ViewModelProviders.of(this, AppViewModelFactory(this.application))
            .get(ListMovieViewModel::class.java).apply {
                releaseToday.observe(this@MainActivity,
                    Observer {
                        AlarmReceiver.createRemainderRelease(this@MainActivity, it)
                    })
                doGetReleaseMovie(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Calendar.getInstance().time)
                )

            }
        AlarmReceiver.createRemainderDaily(this)
    }
}
