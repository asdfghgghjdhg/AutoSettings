package com.dgl.auto.autosettings;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.preference.SeekBarPreference;
import android.widget.Toast;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.ISettingManager;
import com.dgl.auto.RadioManager;
import com.dgl.auto.SettingManager;

import java.util.List;

public class AutoSettingsActivity extends AppCompatPreferenceActivity {
    private static ISettingManager settingManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingManager = SettingManager.getInstance();

        setupActionBar();
        startService(new Intent(this, AutoSettingsService.class));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || SoundPreferenceFragment.class.getName().equals(fragmentName)
                || ScreenPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SoundPreferenceFragment extends PreferenceFragment {
        private static SoundPreferenceFragment mInstance;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mInstance = this;

            addPreferencesFromResource(R.xml.pref_sound);

            SeekBarPreference volumePref = (SeekBarPreference)findPreference(getResources().getString(R.string.sp_sound_volume));
            SeekBarPreference balancePref = (SeekBarPreference)findPreference(getResources().getString(R.string.sp_sound_balance));
            SeekBarPreference fadePref = (SeekBarPreference)findPreference(getResources().getString(R.string.sp_sound_fade));
            EqualizerPreference eqPref = (EqualizerPreference)findPreference(getResources().getString(R.string.sp_sound_equalizer));
            SwitchPreference loudPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_sound_loud));

            int currVol = 0;
            int currBalance = 0;
            int currFade = 0;
            int currEq = 0;
            int currBass = 0;
            int currMiddle = 0;
            int currTreble = 0;
            int currSubwoofer = 0;
            boolean currLoud = false;

            if (settingManager != null) {
                try { currVol = settingManager.getMcuVol(); } catch (RemoteException e) { volumePref.setEnabled(false); }
                try { currBalance = settingManager.getBalance(); } catch (RemoteException e) { balancePref.setEnabled(false); }
                try { currFade = settingManager.getFade(); } catch (RemoteException e) { fadePref.setEnabled(false); }
                try {
                    currEq = settingManager.getEQ();
                    currBass = settingManager.getBass();
                    currMiddle = settingManager.getMiddle();
                    currTreble = settingManager.getTreble();
                    currSubwoofer = settingManager.getSubwoofer();
                } catch (RemoteException e) { eqPref.setEnabled(false); }
                try { currLoud = settingManager.getLound(); } catch (RemoteException e) { loudPref.setEnabled(false); }
            } else {
                volumePref.setEnabled(false);
                balancePref.setEnabled(false);
                fadePref.setEnabled(false);
                eqPref.setEnabled(false);
                loudPref.setEnabled(false);
            }

            volumePref.setProgress(currVol);
            balancePref.setProgress(currBalance);
            fadePref.setProgress(currFade);
            loudPref.setChecked(currLoud);

            String[] names = eqPref.getContext().getResources().getStringArray(R.array.sound_equalizer_presets_names);
            String summary = currEq == 0 ? eqPref.getContext().getResources().getString(R.string.pref_sound_eq_summarycustom) : eqPref.getContext().getResources().getString(R.string.pref_sound_eq_summary);
            eqPref.setSummary(String.format(summary, names[currEq], currBass, currMiddle, currTreble, currSubwoofer));

            volumePref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            balancePref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            fadePref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            eqPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            loudPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
        }

        public static SoundPreferenceFragment getInstance() {
            return mInstance;
        }

        public void updateVolume(int volume) {
            SeekBarPreference volumePref = (SeekBarPreference)findPreference(getResources().getString(R.string.sp_sound_volume));
            volumePref.setProgress(volume);
        }
    }

    private static Preference.OnPreferenceChangeListener mcuPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if (!preference.hasKey()) { return false; }

            switch (preference.getKey()) {
                case "sound_volume": {
                    try {
                        settingManager.setMcuVol((int)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "sound_balance": {
                    try {
                        settingManager.setBalance((int)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "sound_fade": {
                    try {
                        settingManager.setFade((int)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "sound_equalizer": {
                    EqualizerPreference eqPref = (EqualizerPreference)preference;
                    int currPreset = eqPref.getPreset();
                    int currBass = eqPref.getBass();
                    int currMiddle = eqPref.getMiddle();
                    int currTreble = eqPref.getTreble();
                    int currSub = eqPref.getSubwoofer();
                    String[] names = preference.getContext().getResources().getStringArray(R.array.sound_equalizer_presets_names);
                    String summary = currPreset == 0 ? preference.getContext().getResources().getString(R.string.pref_sound_eq_summarycustom) : preference.getContext().getResources().getString(R.string.pref_sound_eq_summary);
                    preference.setSummary(String.format(summary, names[currPreset], currBass, currMiddle, currTreble, currSub));

                    return true;
                }
                case "sound_loud": {
                    try {
                        settingManager.setLound((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            }

            return false;
        }
    };







    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }









    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            /*addPreferencesFromResource(R.xml.pref_general);

            SwitchPreference test = (SwitchPreference)findPreference("example_switch");
            test.setOnPreferenceChangeListener(mcuPreferenceChangeListener);*/
            /*setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));*/
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AutoSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ScreenPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            /*addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));*/
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AutoSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
