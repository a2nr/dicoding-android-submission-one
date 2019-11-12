package io.github.a2nr.submissionmodul1

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_app,rootKey)
    }
}