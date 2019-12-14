package io.github.a2nr.jetpakcourse.receiver

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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import io.github.a2nr.jetpakcourse.ListMovieFragment
import io.github.a2nr.jetpakcourse.MainActivity
import io.github.a2nr.jetpakcourse.R
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val DEBUG_TIME = 2
        const val PENDING_REMAINDER_DAILY_CODE = 1
        const val PENDING_REMAINDER_RELEASE_CODE = 2
        const val TYPE_REMAINDER = "TYPE_REMAINDER"
        const val TYPE_REMAINDER_RELEASE = "TYPE_RELEASE"
        const val MINUTE_RELEASE = 0
        const val HOUR_RELEASE = 7
        const val TYPE_REMAINDER_DAILY = "TYPE_DAILY"
        const val MINUTE_DAILY = 0
        const val HOUR_DAILY = 8
        fun createRemainderRelease(context: Context?) {
            context?.let { _context ->
                PreferenceManager.getDefaultSharedPreferences(_context).let { it ->
                    if (it.getBoolean(
                            _context.resources.getString(R.string.key_setting_remaind_release),
                            false
                        )
                    ) {
                        if ((!isAlarmSet(
                                _context,
                                TYPE_REMAINDER_RELEASE
                            ))) {
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
                                    set(Calendar.SECOND, second + DEBUG_TIME)
                                }
                            } else {
                                cal.apply {
                                    set(Calendar.HOUR_OF_DAY,
                                        HOUR_RELEASE
                                    )
                                    set(Calendar.MINUTE,
                                        MINUTE_RELEASE
                                    )
                                    set(Calendar.SECOND, 0)
                                }
                            }

                            alarmManager.setInexactRepeating(
                                AlarmManager.RTC_WAKEUP,
                                cal.timeInMillis, AlarmManager.INTERVAL_DAY,
                                PendingIntent.getBroadcast(
                                    _context,
                                    PENDING_REMAINDER_RELEASE_CODE,
                                    Intent(_context, AlarmReceiver::class.java).apply {
                                        putExtra(
                                            TYPE_REMAINDER,
                                            TYPE_REMAINDER_RELEASE
                                        )
                                    }, 0
                                )
                            )

                            Log.i("createRemainderRelease", "created")
                        } else {
                            Log.i("createRemainderRelease", "Already Created")
                        }
                    } else {
                        if (isAlarmSet(
                                _context,
                                TYPE_REMAINDER_RELEASE
                            )
                        ) {
                            cancelAlarm(
                                _context,
                                TYPE_REMAINDER_RELEASE
                            )
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
                        if (!isAlarmSet(
                                _context,
                                TYPE_REMAINDER_DAILY
                            )
                        ) {
                            val alarmManager =
                                (_context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                            val intent = Intent(_context, AlarmReceiver::class.java)
                                .apply {
                                    putExtra(
                                        TYPE_REMAINDER,
                                        TYPE_REMAINDER_DAILY
                                    )
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
                                    set(Calendar.SECOND, second + DEBUG_TIME)
                                }
                            } else {
                                cal.apply {
                                    set(Calendar.HOUR_OF_DAY,
                                        HOUR_DAILY
                                    )
                                    set(Calendar.MINUTE,
                                        MINUTE_DAILY
                                    )
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
                        if (isAlarmSet(
                                _context,
                                TYPE_REMAINDER_DAILY
                            )
                        ) {
                            cancelAlarm(
                                _context,
                                TYPE_REMAINDER_DAILY
                            )
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
            val ntfBuilder =
                when (notifId) {
                    PENDING_REMAINDER_DAILY_CODE -> builder.setContentTitle(title)
                        .setContentText(s)
                        .build()
                    //PENDING_REMAINDER_RELEASE_CODE
                    else -> {
                        val notfStyle = NotificationCompat.InboxStyle()
                            .addLine(message[0])
                            .addLine(message[1])
                            .addLine(message[2])
                            .addLine(message[3])
                            .setBigContentTitle("Release Now")
                            .setSummaryText("+${message.size - 4} more")

                        builder.setContentTitle(title)
                            .setContentText(s)
                            .setStyle(notfStyle)
                            .build()
                    }
                }
            notificationManagerCompat.notify(
                notifId + (Date().time / 1000L % Integer.MAX_VALUE).toInt()
                , ntfBuilder
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

        fun cancelAlarm(context: Context, type: String) {
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


    private val mut = MutableLiveData<List<MovieData>>()
    private val dat: LiveData<List<MovieData>>
        get() = mut
    private val alarmJob = Job()
    private val alarmCoroutine = CoroutineScope(Dispatchers.Main + alarmJob)
    private lateinit var str: Array<String>

    override fun onReceive(context: Context, intent: Intent) {
        val notifIntent = Intent(context, MainActivity::class.java)
        notifIntent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.getStringExtra(TYPE_REMAINDER)?.let { _type ->
            when (_type) {
                TYPE_REMAINDER_RELEASE -> {
                    val repo = MovieDataRepository(null)
                    var callObserveRemove: (() -> Unit)? = null
                    val obs = Observer<List<MovieData>> { data ->
                        str = Array(data.size) {
                            data[it].title
                        }
                        callObserveRemove?.invoke()
                        showAlarmNotification(
                            context,
                            "Release Today",
                            str,
                            PENDING_REMAINDER_RELEASE_CODE
                            ,
                            notifIntent
                        )
                    }
                    callObserveRemove = {
                        dat.removeObserver(obs)
                    }
                    notifIntent.action =
                        ListMovieFragment.NOTIFICATION_FEEDBACK
                    dat.observeForever(obs)
                    alarmCoroutine.launch {
                        mut.value = withContext(Dispatchers.IO) {
                            repo.getReleaseMovie(
                                SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                                ).format(Calendar.getInstance().time)
                            )
                        }
                    }
                }
                TYPE_REMAINDER_DAILY -> {
                    showAlarmNotification(
                        context,
                        "Daily Remainder",
                        Array(1) { "Explore Movie Now!!" },
                        PENDING_REMAINDER_DAILY_CODE,
                        notifIntent
                    )
                }
                else -> {
                    Log.i("onReceive", "Type Not found")
                }
            }
        }
    }

}



