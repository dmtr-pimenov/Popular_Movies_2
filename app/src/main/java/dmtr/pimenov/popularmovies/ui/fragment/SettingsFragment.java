package dmtr.pimenov.popularmovies.ui.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;

import dmtr.pimenov.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * SettingsFragment has settings for data retrieval:
 * sort mode and Favorite mode
 * and one item to define UI behaviour
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        List<Preference> prefList = getAllPreferences(preferenceScreen, null);
        for (Preference pref : prefList) {
            if ((pref instanceof ListPreference)) {
                setListPrefSummary(pref);
            }
        }

        // disable Transition preference on pre-Lollipop devices
        // because Transition effects was introduced since API level 21
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Preference preference = preferenceScreen.findPreference(getString(R.string.pref_transition_key));
            if (preference != null) {
                preference.setEnabled(false);
            }
        }
    }

    /**
     * For ListPreference sets Summary
     * as summary value is used Label of option
     * @param preference
     */
    private void setListPrefSummary(Preference preference) {
        if (!(preference instanceof ListPreference)) {
            return;
        }
        ListPreference lPref = (ListPreference) preference;
        String value = lPref.getValue();
        int indexOfValue = lPref.findIndexOfValue(value);
        if (indexOfValue >= 0) {
            CharSequence entry = lPref.getEntries()[indexOfValue];
            lPref.setSummary(entry);
        }
    }

    /**
     * Recursively goes through the list of Preferences
     * and builds flat list of preferences
     * all descendants of the PreferenceGroup will be skipped
     * @param group
     * @param result
     * @return
     */
    private List<Preference> getAllPreferences(PreferenceGroup group, List<Preference> result) {
        List<Preference> res;
        if (result == null) {
            res = new ArrayList<>();
        } else {
            res = result;
        }
        int count = group.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = group.getPreference(i);
            if (!(p instanceof PreferenceGroup)) {
                res.add(p);
            } else {
                getAllPreferences((PreferenceGroup) p, res);
            }
        }
        return res;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null && preference instanceof ListPreference) {
            setListPrefSummary(preference);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}