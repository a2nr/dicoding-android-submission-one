package io.github.a2nr.jetpakcourse

import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import io.github.a2nr.jetpakcourse.receiver.AlarmReceiver

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_app, rootKey)
        val context = this.requireContext()
        val preference = PreferenceManager
            .getDefaultSharedPreferences(context)
        preference.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                context.resources.getString(R.string.key_setting_remaind_release) -> {
                    if (sharedPreferences.getBoolean(key, false)) {
                        AlarmReceiver.createRemainderRelease(context)
                        Snackbar.make(
                            this.requireView(),
                            "Remainder Release Aktif",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()

                    } else {
                        AlarmReceiver.cancelAlarm(context, AlarmReceiver.TYPE_REMAINDER_RELEASE)
                        Snackbar.make(
                            this.requireView(),
                            "Remainder Release Tidak Aktif",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                context.resources.getString(R.string.key_setting_remaind_daily) -> {
                    if (preference.getBoolean(key, false)
                    ) {
                        AlarmReceiver.createRemainderDaily(context)
                        Snackbar.make(
                            this.requireView(),
                            "Remainder Daily Aktif",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        AlarmReceiver.cancelAlarm(context, AlarmReceiver.TYPE_REMAINDER_DAILY)
                        Snackbar.make(
                            this.requireView(),
                            "Remainder Daily Tidak Aktif",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                else -> Log.e("", "")

            }
        }
    }
}