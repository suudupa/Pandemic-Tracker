package com.suudupa.coronavirustracker.fragment;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.utility.Utils;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setupRegionListPreference();
    }

    private void setupRegionListPreference() {
        ListPreference regionListPreference = findPreference("fRegionKey");
        //TODO: make sure this is only done once
        regionListPreference.setEntries(Utils.getCountryList());
        regionListPreference.setEntryValues(Utils.getCountryList());
        regionListPreference.setDefaultValue(Utils.getCountry());
    }
}