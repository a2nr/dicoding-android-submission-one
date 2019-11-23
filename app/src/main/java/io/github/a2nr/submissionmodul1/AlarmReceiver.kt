package io.github.a2nr.submissionmodul1

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.repository.MovieDataRepository
import io.github.a2nr.submissionmodul1.repository.MovieDatabase
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val PENDING_REMAINDER_DAILY_CODE = 1
        const val PENDING_REMAINDER_RELEASE_CODE = 2
        const val EXTRA_TITLE = "TITLE_MOVIE"
        const val TYPE_REMAINDER = "TYPE_REMAINDER"
        const val TYPE_REMAINDER_RELEASE = "TYPE_RELEASE"
        const val MINUTE_RELEASE = 0
        const val HOUR_RELEASE = 7
        const val TYPE_REMAINDER_DAILY = "TYPE_DAILY"
        const val MINUTE_DAILY = 0
        const val HOUR_DAILY = 8
        fun createRemainderRelease(context: Context?, releaseMovie: List<MovieData>) {
            context?.let { _context ->
                PreferenceManager.getDefaultSharedPreferences(_context).let { it ->
                    if (it.getBoolean(
                            _context.resources.getString(R.string.key_setting_remaind_release),
                            false
                        )
                    ) {
                        if ((!isAlarmSet(_context, TYPE_REMAINDER_RELEASE))) {
                            val alarmManager =
                                (_context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)

                            val cal = Calendar.getInstance()
                            if (it.getBoolean("tombol_1", false)) {
                                Log.i(
                                    "[DEBUG 1]",
                                    "Akan muncul notif setelah 1 Menit dari sekarang"
                                )
                                cal.apply {
                                    val second = get(Calendar.SECOND)
                                    set(Calendar.SECOND, second + 5)
                                }
                            } else {
                                cal.apply {
                                    set(Calendar.HOUR_OF_DAY, HOUR_RELEASE)
                                    set(Calendar.MINUTE, MINUTE_RELEASE)
                                    set(Calendar.SECOND, 0)
                                }
                            }

                            alarmManager.setInexactRepeating(
                                AlarmManager.RTC_WAKEUP,
                                cal.timeInMillis, AlarmManager.INTERVAL_DAY,
                                PendingIntent.getBroadcast(
                                    _context, PENDING_REMAINDER_RELEASE_CODE,
                                    Intent(_context, AlarmReceiver::class.java).apply {
                                        putExtra(TYPE_REMAINDER, TYPE_REMAINDER_RELEASE)
                                        val s = Array(5) {
                                            releaseMovie[it].title
                                        }
                                        putExtra(EXTRA_TITLE, s)
                                    }, 0
                                )
                            )

                            Log.i("createRemainderRelease", "created")
                        } else {
                            Log.i("createRemainderRelease", "Already Created")
                        }
                    } else {
                        if (isAlarmSet(_context, TYPE_REMAINDER_RELEASE)) {
                            cancelAlarm(_context, TYPE_REMAINDER_RELEASE)
                        }
                        Log.i("createRemainderRelease", "disabled")
                    }
                }

            }
        }

        fun createRemainderDaily(context: Context?) {
            context?.let { _context ->
                PreferenceManager.getDefaultSharedPreferences(_context).let {
                    if (it.getBoolean(
                            _context
                                .resources.getString(R.string.key_setting_remaind_daily),
                            false
                        )
                    ) {
                        if (!isAlarmSet(_context, TYPE_REMAINDER_DAILY)) {
                            val alarmManager =
                                (_context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                            val intent = Intent(_context, AlarmReceiver::class.java)
                                .apply {
                                    putExtra(TYPE_REMAINDER, TYPE_REMAINDER_DAILY)
                                }
                            val pending = PendingIntent.getBroadcast(
                                _context,
                                PENDING_REMAINDER_DAILY_CODE,
                                intent,
                                0
                            )
                            val cal = Calendar.getInstance()

                            if (it.getBoolean("tombol_1", false)) {
                                Log.i(
                                    "[DEBUG 1]",
                                    "Akan muncul notif setelah 1 Menit dari sekarang"
                                )
                                cal.apply {
                                    val second = get(Calendar.SECOND)
                                    set(Calendar.SECOND, second + 5)
                                }
                            } else {
                                cal.apply {
                                    set(Calendar.HOUR_OF_DAY, HOUR_DAILY)
                                    set(Calendar.MINUTE, MINUTE_DAILY)
                                    set(Calendar.SECOND, 0)
                                }

                            }
                            alarmManager.setInexactRepeating(
                                AlarmManager.RTC_WAKEUP,
                                cal.timeInMillis
                                , AlarmManager.INTERVAL_DAY,
                                pending
                            )
                            Log.i("createRemainderDaily", "created")
                        } else {
                            Log.i("createRemainderDaily", "Already Created")
                        }
                    } else {
                        if (isAlarmSet(_context, TYPE_REMAINDER_DAILY)) {
                            cancelAlarm(_context, TYPE_REMAINDER_DAILY)
                        }
                        Log.i("createRemainderDaily", "disabled")
                    }
                }

            }
        }

        // Gunakan metode ini untuk menampilkan notifikasi
        private fun showAlarmNotification(
            context: Context,
            title: String,
            message: Array<String>,
            notifId: Int,
            intent: Intent?
        ) {

            val CHANNEL_ID = "Channel_1"
            val CHANNEL_NAME = "AlarmManager channel"

            val notificationManagerCompat =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_favorite_24px)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)
            intent?.let {
                builder.setContentIntent(
                    PendingIntent
                        .getActivity(
                            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                        )
                )
            }


            /*
            Untuk android Oreo ke atas perlu menambahkan notification channel
            Materi ini akan dibahas lebih lanjut di modul extended
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                /* Create or update. */
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

                builder.setChannelId(CHANNEL_ID)

                notificationManagerCompat.createNotificationChannel(channel)
            }
            val s = message.let { _message ->
                var ss = ""
                _message.forEach {
                    ss += "-$it\r\n"
                }
                ss
            }

            notificationManagerCompat.notify(
                notifId + (Date().time / 1000L % Integer.MAX_VALUE).toInt()
                , builder.setContentTitle(title)
                    .setContentText(s)
                    .build()
            )

        }

        // Gunakan metode ini untuk mengecek apakah alarm tersebut sudah terdaftar di alarm manager
        private fun isAlarmSet(context: Context, type: String): Boolean {
            val a = PendingIntent.getBroadcast(
                context,
                (if (type == TYPE_REMAINDER_DAILY) PENDING_REMAINDER_DAILY_CODE else PENDING_REMAINDER_RELEASE_CODE),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE
            ) != null
            return a
        }

        private fun cancelAlarm(context: Context, type: String) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val requestCode = if (type.equals(
                    TYPE_REMAINDER_DAILY,
                    ignoreCase = true
                )
            ) PENDING_REMAINDER_DAILY_CODE else PENDING_REMAINDER_RELEASE_CODE
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
            pendingIntent.cancel()

            alarmManager.cancel(pendingIntent)
        }

    }

//    class MovieReleaseReceiver(context: Context, observer: Observer<List<MovieData>>) {
//        private val repo : MovieDataRepository
//        val data : LiveData<List<MovieData>>
//            get() = repo.mutMovieData
//        init {
//            repo = MovieDataRepository(MovieDatabase.getInstance(context).movieDao())
//            data.observeForever(observer)
//        }
//    }
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val notifIntent = Intent(context, MainActivity::class.java)
//        val obs = Observer<List<MovieData>>({
//
//        })
//        val movieRelease = MovieReleaseReceiver(context,obs)
        notifIntent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.getStringExtra(TYPE_REMAINDER)?.let { _type ->
            when (_type) {
                TYPE_REMAINDER_RELEASE -> {
                    intent.getStringArrayExtra(EXTRA_TITLE)?.let {
                        notifIntent.action = ListMovieFragment.NOTIFICATION_FEEDBACK
                        showAlarmNotification(
                            context, "Release Today", it, PENDING_REMAINDER_RELEASE_CODE
                            , notifIntent
                        )
                    }
                }
                TYPE_REMAINDER_DAILY -> {
                    showAlarmNotification(
                        context,
                        "Daily Remainder",
                        Array(1) { "Explore Movie Now!!" },
                        PENDING_REMAINDER_DAILY_CODE, notifIntent
                    )
                }
                else -> {
                    Log.i("onReceive", "Type Not found")
                }
            }
        }
    }

}



