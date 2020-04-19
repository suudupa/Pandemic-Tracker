package com.suudupa.coronavirustracker.fragment;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.activity.MainActivity;

import static com.suudupa.coronavirustracker.utility.Resources.GLOBAL;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setupFavoriteRegionPreference();
        setupLanguagePreference();
    }

    private void setupFavoriteRegionPreference() {
        ListPreference favoriteRegionPreference = findPreference(getString(R.string.favoriteRegionKey));
        String [] regions = MainActivity.regions.toArray(new String[MainActivity.regions.size()]);
//        if (favoriteRegionPreference.getEntries() == null) {
//            favoriteRegionPreference.setEntries(Utils.getCountryList());
//            favoriteRegionPreference.setEntryValues(Utils.getCountryList());
        favoriteRegionPreference.setEntries(regions);
        favoriteRegionPreference.setEntryValues(regions);
        favoriteRegionPreference.setDefaultValue(GLOBAL);
//        }
        favoriteRegionPreference.setSummary(getString(R.string.favoriteRegionText) + " Current selection: " + favoriteRegionPreference.getValue());
        favoriteRegionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(getString(R.string.favoriteRegionText) + " Current selection: " + newValue.toString());
                return true;
            }
        });
    }

    private void setupLanguagePreference() {
        ListPreference languagePreference = findPreference(getString(R.string.languageKey));
        languagePreference.setSummary(getString(R.string.languageText) + " Current selection: " + languagePreference.getEntry());
        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(getString(R.string.languageText) + " Current selection: " + newValue.toString());
                return true;
            }
        });
    }
}