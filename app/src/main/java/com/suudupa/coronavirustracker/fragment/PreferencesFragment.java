package com.suudupa.coronavirustracker.fragment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.activity.MainActivity;
import com.suudupa.coronavirustracker.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setupFavoriteRegionPreference();
        setupLanguagePreference();
    }

    private void setupFavoriteRegionPreference() {
        final ListPreference favoriteRegionPreference = findPreference(getString(R.string.favoriteRegionKey));
        List<String> allRegions = new ArrayList<>(MainActivity.regions);
        Utils.sortAlphabetical(allRegions);
        String [] regions = allRegions.toArray(new String[0]);
        favoriteRegionPreference.setEntries(regions);
        favoriteRegionPreference.setEntryValues(regions);
        updateDialogMessage(favoriteRegionPreference, allRegions);
        favoriteRegionPreference.setSummary(getString(R.string.favoriteRegionText) + " Current selection: %s");
        favoriteRegionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                showToast(getCorrespondingEntry((ListPreference) preference, (String) newValue));
                preference.setSummary(getString(R.string.favoriteRegionText) + " Current selection: %s");
                return true;
            }
        });
    }

    private void setupLanguagePreference() {
        final ListPreference languagePreference = findPreference(getString(R.string.languageKey));
        languagePreference.setSummary(getString(R.string.languageText) + " Current selection: %s");
        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                showToast(getCorrespondingEntry((ListPreference) preference, (String) newValue));
                preference.setSummary(getString(R.string.languageText) + " Current selection: %s");
                return true;
            }
        });
    }

    private void updateDialogMessage(ListPreference listPreference, List<String> elements) {
        if (elements == null || elements.size() == 0) {
            listPreference.setDialogMessage(R.string.favoriteRegionErrorText);
        }
    }

    private void showToast(String newValue) {
        Toast.makeText(getContext(), newValue + " selected", Toast.LENGTH_SHORT).show();
    }

    private String getCorrespondingEntry(ListPreference listPreference, String entryValue) {
        int index = listPreference.findIndexOfValue(entryValue);
        return (String) listPreference.getEntries()[index];
    }
}