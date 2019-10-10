package dmtr.pimenov.popularmovies.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import dmtr.pimenov.popularmovies.R;

import java.util.Arrays;
import java.util.List;

/**
 * This activity is holder of SettingsFragment. SettingsFragment has 2 kind of settings:
 * for data selection and for UI behaviour
 * When Activity is being closed we check whether Settings for data selection were changed
 * in this case Activity returns result RESULT_PREF_CHANGED
 * otherwise - RESULT_PREF_UNCHANGED
 * SettingsActivity must be start by StartActivityForResult
 */

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    public static final int RESULT_PREF_CHANGED = 1;
    public static final int RESULT_PREF_UNCHANGED = 2;

    List<String> controlList;
    private boolean mPreferencesHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String[] keys = {getString(R.string.pref_sort_mode_key),
                getString(R.string.pref_favorite_key)
        };
        controlList = Arrays.asList(keys);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * If user uses home button in ActionBar to navigate backward
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected: finishing Activity");
            setResultCode();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * If user uses On Screen Back button to navigate backward
     */
    @Override
    public void onBackPressed() {
        setResultCode();
        super.onBackPressed();
    }

    private void setResultCode() {
        int resCode = (mPreferencesHasChanged) ? RESULT_PREF_CHANGED : RESULT_PREF_UNCHANGED;
        setResult(resCode, null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: " + key);
        if (getResources().getString(R.string.pref_transition_key).equals(key)) {
            return;
        }
        mPreferencesHasChanged = controlList.contains(key);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
