package io.github.a2nr.jetpakcourse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.a2nr.jetpakcourse.receiver.AlarmReceiver

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AlarmReceiver.createRemainderRelease(this)
        AlarmReceiver.createRemainderDaily(this)
    }
}
