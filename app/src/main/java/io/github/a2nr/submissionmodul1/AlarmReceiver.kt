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
import androidx.preference.PreferenceManager
import io.github.a2nr.submissionmodul1.repository.MovieData
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val PENDING_REMAINDER_DAILY_CODE = 101
        const val PENDING_REMAINDER_RELEASE_CODE = 100
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
                            (_context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                                .apply {
                                    val intent = Intent(_context, AlarmReceiver::class.java)
                                        .apply {
                                            putExtra(TYPE_REMAINDER, TYPE_REMAINDER_RELEASE)
                                            putExtra(EXTRA_TITLE, Array(releaseMovie.size) {
                                                releaseMovie[it].title
                                            })
                                        }
                                    PendingIntent.getBroadcast(
                                        _context,
                                        PENDING_REMAINDER_RELEASE_CODE,
                                        intent,
                                        0
                                    )
                                        .let { pending ->
                                            this@apply.setInexactRepeating(
                                                AlarmManager.RTC_WAKEUP,
                                                Calendar.getInstance()
                                                    .apply {
                                                        set(Calendar.HOUR_OF_DAY, HOUR_RELEASE)
                                                        set(Calendar.MINUTE, MINUTE_RELEASE)
                                                        set(Calendar.SECOND, 0)
                                                    }.timeInMillis, AlarmManager.INTERVAL_DAY,
                                                pending
                                            )
                                        }
                                }
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
                        if (!isAlarmSet(_context, TYPE_REMAINDER_RELEASE)) {
                            (_context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                                .apply {
                                    val intent = Intent(_context, AlarmReceiver::class.java)
                                        .apply {
                                            putExtra(TYPE_REMAINDER, TYPE_REMAINDER_DAILY)
                                        }
                                    PendingIntent.getBroadcast(
                                        _context,
                                        PENDING_REMAINDER_DAILY_CODE,
                                        intent,
                                        0
                                    )
                                        .let { pending ->
                                            this@apply.setInexactRepeating(
                                                AlarmManager.RTC_WAKEUP,
                                                Calendar.getInstance()
                                                    .apply {
                                                        set(Calendar.HOUR_OF_DAY, HOUR_DAILY)
                                                        set(Calendar.MINUTE, MINUTE_DAILY)
                                                        set(Calendar.SECOND, 0)
                                                    }.timeInMillis, AlarmManager.INTERVAL_DAY,
                                                pending
                                            )
                                        }
                                }
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
            message: String,
            notifId: Int
        ) {

            val CHANNEL_ID = "Channel_1"
            val CHANNEL_NAME = "AlarmManager channel"

            val notificationManagerCompat =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_favorite_24px)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)

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

            val notification = builder.build()

            notificationManagerCompat.notify(notifId, notification)

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

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        intent.getStringExtra(TYPE_REMAINDER)?.let { _type ->
            when (_type) {
                TYPE_REMAINDER_RELEASE -> {
                    intent.getStringExtra(EXTRA_TITLE)?.let {
                        showAlarmNotification(
                            context,
                            "Release Today",
                            it,
                            PENDING_REMAINDER_RELEASE_CODE
                        )
                    }
                }
                TYPE_REMAINDER_DAILY -> {
                    showAlarmNotification(
                        context,
                        "Daily Remainder",
                        "Explore Movie Now!!",
                        PENDING_REMAINDER_DAILY_CODE
                    )
                }
                else -> {
                    Log.i("onReceive", "Type Not found")
                }
            }
        }
    }

}


