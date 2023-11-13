package com.networklogger

import android.os.Bundle
import android.view.View
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsPageFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        (findPreference<EditTextPreference>("endPoints") as EditTextPreference).setOnPreferenceChangeListener { preference, newValue ->
            val storedValues = prefs.getStringSet("endPointsList", setOf())
            storedValues?.toMutableList()?.add(newValue.toString())
            prefs.edit().putStringSet("endPointsList", storedValues)
            false
        }
    }
}