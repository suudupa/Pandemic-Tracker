package com.suudupa.coronavirustracker.fragment;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.utility.Utils;

import static com.suudupa.coronavirustracker.utility.Resources.GLOBAL;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setupRegionListPreference();
    }

    private void setupRegionListPreference() {
        ListPreference regionListPreference = findPreference(getString(R.string.favoriteRegionKey));
        if (regionListPreference.getEntries() == null) {
            regionListPreference.setEntries(Utils.getCountryList());
            regionListPreference.setEntryValues(Utils.getCountryList());
            regionListPreference.setDefaultValue(GLOBAL);
        }
        regionListPreference.setSummary(getString(R.string.favoriteRegionText) + " Current selection: " + regionListPreference.getValue());
        regionListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(getString(R.string.favoriteRegionText) + " Current selection: " + newValue.toString());
                return true;
            }
        });
    }
}