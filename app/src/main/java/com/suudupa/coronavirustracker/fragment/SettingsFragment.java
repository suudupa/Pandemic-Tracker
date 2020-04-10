package com.suudupa.coronavirustracker.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.suudupa.coronavirustracker.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}