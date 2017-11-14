package com.dgl.auto.autosettings;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.SeekBarPreference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.dgl.auto.mcumanager.MCUManager;
import com.jaredrummler.android.colorpicker.ColorPreference;

import java.util.Arrays;
import java.util.List;

public class AutoSettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener, ServiceConnection {
    public static final String ACTION_FINISH = "com.dgl.auto.autosettings.action.FINISH";
    private static final String LOG_TAG = "AutoSettingsActivity";

    private BroadcastReceiver mActivityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_FINISH.equals(intent.getAction())){ finish(); }
        }
    };

    private AutoSettingsService mAutoSettingsService;
    private boolean mServiceBounded;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate");

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_FINISH);
        localBroadcastManager.registerReceiver(mActivityBroadcastReceiver, intentFilter);

        setupActionBar();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.sp_sound_speed_compensation), false);
            editor.apply();
        }

        Intent serviceIntent = new Intent(this, AutoSettingsService.class);
        startService(serviceIntent);
        mServiceConnection = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent serviceIntent = new Intent(this, AutoSettingsService.class);
        bindService(serviceIntent, mServiceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    protected void onStop() {
        if (mServiceBounded) {
            unbindService(mServiceConnection);
            mServiceBounded = false;
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(mActivityBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        AutoSettingsService.AutoSettingsServiceBinder binder = (AutoSettingsService.AutoSettingsServiceBinder)iBinder;
        if (binder != null) {
            mAutoSettingsService = binder.getService();
            mServiceBounded = true;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mServiceBounded = false;
        mAutoSettingsService = null;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || SoundPreferenceFragment.class.getName().equals(fragmentName)
                || EqualizerPreferenceFragment.class.getName().equals(fragmentName)
                || RadioPreferenceFragment.class.getName().equals(fragmentName)
                || ScreenPreferenceFragment.class.getName().equals(fragmentName)
                || RearViewPreferenceFragment.class.getName().equals(fragmentName)
                || SWCPreferenceFragment.class.getName().equals(fragmentName)
                || InfoPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;
        private Preference.OnPreferenceChangeListener mPreferenceChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            mSPChangeListener = this;
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(mSPChangeListener);

            mPreferenceChangeListener = (AutoSettingsActivity)getActivity();

            ListPreference pBootTime = (ListPreference)findPreference(getString(R.string.sp_general_boottime));
            if (pBootTime != null) {
                try {
                    String[] values = getResources().getStringArray(R.array.mcu_boottime_values);
                    pBootTime.setValue(values[MCUManager.getBootTime()]);
                    pBootTime.setSummary(pBootTime.getEntry());
                    pBootTime.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pBootTime.setEnabled(false);
                    pBootTime.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SwitchPreference pShortcut = (SwitchPreference)findPreference(getString(R.string.sp_general_shortcut_touch_state));
            if (pShortcut != null) {
                try {
                    pShortcut.setChecked(MCUManager.getShortcutTouchState());
                    pShortcut.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pShortcut.setEnabled(false);
                    pShortcut.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SwitchPreference pSwitchMedia = (SwitchPreference)findPreference(getString(R.string.sp_general_switch_media_status));
            if (pSwitchMedia != null) {
                try {
                    pSwitchMedia.setChecked(MCUManager.MultimediaControl.getSwitchMediaStatus());
                    pSwitchMedia.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pSwitchMedia.setEnabled(false);
                    pSwitchMedia.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SwitchPreference pPlayVideo = (SwitchPreference)findPreference(getString(R.string.sp_general_playvideo));
            if (pPlayVideo != null) {
                try {
                    boolean on = MCUManager.MultimediaControl.getPlayVideoWhileDriving();
                    pPlayVideo.setChecked(on);
                    if (on) { pPlayVideo.setIcon(R.drawable.ic_videocam_black_24dp); } else { pPlayVideo.setIcon(R.drawable.ic_videocam_off_black_24dp); };
                    pPlayVideo.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pPlayVideo.setEnabled(false);
                    pPlayVideo.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSPChangeListener);
            super.onDestroy();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (!isAdded()) { return; }
            if (s == null) { return; }

            if (s.equals(getString(R.string.sp_general_boottime))) {
                ListPreference pBootTime = (ListPreference)findPreference(s);
                if (pBootTime != null) {
                    pBootTime.setValue(sharedPreferences.getString(s, ""));
                    pBootTime.setSummary(pBootTime.getEntry());
                }
            }
            if (s.equals(getString(R.string.sp_general_switch_media_status))) {
                SwitchPreference pSwitchMedia = (SwitchPreference)findPreference(s);
                if (pSwitchMedia != null) { pSwitchMedia.setChecked(sharedPreferences.getBoolean(s, false)); }
            }
            if (s.equals(getString(R.string.sp_general_shortcut_touch_state))) {
                SwitchPreference pShortcut = (SwitchPreference)findPreference(s);
                if (pShortcut != null) { pShortcut.setChecked(sharedPreferences.getBoolean(s, false)); }
            }
            if (s.equals(getString(R.string.sp_general_playvideo))) {
                SwitchPreference pPlayVideo = (SwitchPreference)findPreference(s);
                if (pPlayVideo != null) {
                    boolean on = sharedPreferences.getBoolean(s, false);
                    pPlayVideo.setChecked(on);
                    if (on) { pPlayVideo.setIcon(R.drawable.ic_videocam_black_24dp); } else { pPlayVideo.setIcon(R.drawable.ic_videocam_off_black_24dp); };
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SoundPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;
        private Preference.OnPreferenceChangeListener mPreferenceChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_sound);
            setHasOptionsMenu(true);

            mSPChangeListener = this;
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(mSPChangeListener);

            mPreferenceChangeListener = (AutoSettingsActivity)getActivity();

            SeekBarPreference pVolume = (SeekBarPreference)findPreference(getString(R.string.sp_sound_volume));
            if (pVolume != null) {
                try {
                    pVolume.setProgress(MCUManager.VolumeControl.getVolume());
                    pVolume.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pVolume.setEnabled(false);
                    pVolume.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SeekBarPreference pBalance = (SeekBarPreference)findPreference(getString(R.string.sp_sound_balance));
            if (pBalance != null) {
                try {
                    pBalance.setProgress(MCUManager.VolumeControl.getBalance());
                    pBalance.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pBalance.setEnabled(false);
                    pBalance.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SeekBarPreference pFade = (SeekBarPreference)findPreference(getString(R.string.sp_sound_fade));
            if (pFade != null) {
                try {
                    pFade.setProgress(MCUManager.VolumeControl.getFade());
                    pFade.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pFade.setEnabled(false);
                    pFade.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            Preference pEq = findPreference(getString(R.string.sp_sound_equalizer));
            if (pEq != null) {
                try {
                    MCUManager.EqualizerControl.EqualizerPreset currPreset = MCUManager.EqualizerControl.getPreset();
                    int currSubwoofer = MCUManager.EqualizerControl.getSubwoofer();
                    String presetName = getResources().getStringArray(R.array.mcu_equalizer_presets_names)[currPreset.getMCUIndex()];
                    int bassPerc = Math.round((float)currPreset.getBass() * 100 / MCUManager.EqualizerControl.MAX_BASS_VALUE);
                    int middlePerc = Math.round((float)currPreset.getMiddle() * 100 / MCUManager.EqualizerControl.MAX_MIDDLE_VALUE);
                    int treblePerc = Math.round((float)currPreset.getTreble() * 100 / MCUManager.EqualizerControl.MAX_TREBLE_VALUE);
                    int subPerc = Math.round((float)currSubwoofer * 100 / MCUManager.EqualizerControl.MAX_SUBWOOFER_VALUE);
                    pEq.setSummary(String.format(getString(R.string.pref_sound_eq_summary), presetName, bassPerc, middlePerc, treblePerc, subPerc));
                } catch (RemoteException e) {
                    pEq.setEnabled(false);
                    pEq.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SwitchPreference pLoud = (SwitchPreference)findPreference(getString(R.string.sp_sound_loud));
            if (pLoud != null) {
                try {
                    pLoud.setChecked(MCUManager.EqualizerControl.getLoudMode());
                    pLoud.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pLoud.setEnabled(false);
                    pLoud.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SwitchPreference pSoundComp = (SwitchPreference)findPreference(getString(R.string.sp_sound_speed_compensation));
            if (pSoundComp != null) {
                pSoundComp.setOnPreferenceChangeListener(mPreferenceChangeListener);
            }

            SpeedPreference pMinSpeed = (SpeedPreference)findPreference(getString(R.string.sp_sound_min_speed));
            if (pMinSpeed != null) {
                pMinSpeed.setOnPreferenceChangeListener(mPreferenceChangeListener);
                pMinSpeed.setEnabled(sharedPreferences.getBoolean(getString(R.string.sp_sound_speed_compensation), false));
            }

            SpeedPreference pMaxSpeed = (SpeedPreference)findPreference(getString(R.string.sp_sound_max_speed));
            if (pMaxSpeed != null) {
                pMaxSpeed.setOnPreferenceChangeListener(mPreferenceChangeListener);
                pMaxSpeed.setEnabled(sharedPreferences.getBoolean(getString(R.string.sp_sound_speed_compensation), false));
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSPChangeListener);
            super.onDestroy();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (!isAdded()) { return; }
            if (s == null) { return; }

            if (s.equals(getString(R.string.sp_sound_volume))) {
                SeekBarPreference pref = (SeekBarPreference)findPreference(s);
                int volume = sharedPreferences.getInt(s, 0);
                if (pref != null) {
                    pref.setProgress(volume);
                    if (volume > 20) {
                        pref.setIcon(R.drawable.ic_volume_up_black_24dp);
                    } else if (volume == 0) {
                        pref.setIcon(R.drawable.ic_volume_mute_black_24dp);
                    } else {
                        pref.setIcon(R.drawable.ic_volume_down_black_24dp);
                    }
                }
            }

            if (s.equals(getString(R.string.sp_sound_balance))) {
                SeekBarPreference pref = (SeekBarPreference)findPreference(s);
                int balance = sharedPreferences.getInt(s, 0);
                if (pref != null) { pref.setProgress(balance); }
            }

            if (s.equals(getString(R.string.sp_sound_fade))) {
                SeekBarPreference pref = (SeekBarPreference)findPreference(s);
                int fade = sharedPreferences.getInt(s, 0);
                if (pref != null) { pref.setProgress(fade); }
            }

            if (s.equals(getString(R.string.sp_sound_eq_preset))
                    || s.equals(getString(R.string.sp_sound_eq_bass))
                    || s.equals(getString(R.string.sp_sound_eq_middle))
                    || s.equals(getString(R.string.sp_sound_eq_treble))
                    || s.equals(getString(R.string.sp_sound_eq_subwoofer))) {
                String value = sharedPreferences.getString(getString(R.string.sp_sound_eq_preset), getResources().getStringArray(R.array.mcu_equalizer_presets_values)[0]);
                String[] values = getResources().getStringArray(R.array.mcu_equalizer_presets_values);
                int index = Arrays.asList(values).indexOf(value);
                int bass = sharedPreferences.getInt(getString(R.string.sp_sound_eq_bass), 0);
                int middle = sharedPreferences.getInt(getString(R.string.sp_sound_eq_middle), 0);
                int treble = sharedPreferences.getInt(getString(R.string.sp_sound_eq_treble), 0);
                int subwoofer = sharedPreferences.getInt(getString(R.string.sp_sound_eq_subwoofer), 0);
                Preference pref = findPreference(getString(R.string.sp_sound_equalizer));
                String presetName = getResources().getStringArray(R.array.mcu_equalizer_presets_names)[index];
                int bassPerc = Math.round((float)bass * 100 / MCUManager.EqualizerControl.MAX_BASS_VALUE);
                int middlePerc = Math.round((float)middle * 100 / MCUManager.EqualizerControl.MAX_MIDDLE_VALUE);
                int treblePerc = Math.round((float)treble * 100 / MCUManager.EqualizerControl.MAX_TREBLE_VALUE);
                int subPerc = Math.round((float)subwoofer * 100 / MCUManager.EqualizerControl.MAX_SUBWOOFER_VALUE);
                pref.setSummary(String.format(getString(R.string.pref_sound_eq_summary), presetName, bassPerc, middlePerc, treblePerc, subPerc));
            }

            if (s.equals(getString(R.string.sp_sound_loud))) {
                SwitchPreference pref = (SwitchPreference)findPreference(s);
                boolean value = sharedPreferences.getBoolean(s, false);
                if (pref != null) { pref.setChecked(value); }
            }

            if (s.equals(getString(R.string.sp_sound_speed_compensation))) {
                SwitchPreference pref = (SwitchPreference)findPreference(s);
                SpeedPreference minSpeed = (SpeedPreference)findPreference(getString(R.string.sp_sound_min_speed));
                SpeedPreference maxSpeed = (SpeedPreference)findPreference(getString(R.string.sp_sound_max_speed));
                if (pref != null) {
                    boolean value = sharedPreferences.getBoolean(s, false);
                    pref.setChecked(value);
                    if (minSpeed != null) { minSpeed.setEnabled(value); }
                    if (maxSpeed != null) { maxSpeed.setEnabled(value); }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class EqualizerPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;
        private Preference.OnPreferenceChangeListener mPreferenceChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_sound_equalizer);
            setHasOptionsMenu(true);

            // TODO: Заголовок!

            mSPChangeListener = this;
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(mSPChangeListener);

            mPreferenceChangeListener = (AutoSettingsActivity)getActivity();

            ListPreference pEqPreset = (ListPreference)findPreference(getString(R.string.sp_sound_eq_preset));
            SeekBarPreference pEqBass = (SeekBarPreference)findPreference(getString(R.string.sp_sound_eq_bass));
            SeekBarPreference pEqMiddle = (SeekBarPreference)findPreference(getString(R.string.sp_sound_eq_middle));
            SeekBarPreference pEqTreble = (SeekBarPreference)findPreference(getString(R.string.sp_sound_eq_treble));
            SeekBarPreference pEqSub = (SeekBarPreference)findPreference(getString(R.string.sp_sound_eq_subwoofer));
            try {
                MCUManager.EqualizerControl.EqualizerPreset currPreset = MCUManager.EqualizerControl.getPreset();
                int currSubwoofer = MCUManager.EqualizerControl.getSubwoofer();

                if (pEqPreset != null) {
                    String currValue = (String)pEqPreset.getEntryValues()[currPreset.getMCUIndex()];
                    pEqPreset.setValue(currValue);
                    pEqPreset.setTitle(pEqPreset.getEntry());
                    pEqPreset.setOnPreferenceChangeListener(mPreferenceChangeListener);
                }
                if (pEqBass != null) {
                    pEqBass.setEnabled(currPreset.getMCUIndex() == MCUManager.EqualizerControl.CUSTOM_PRESET.getMCUIndex());
                    pEqBass.setProgress(currPreset.getBass());
                    pEqBass.setOnPreferenceChangeListener(mPreferenceChangeListener);
                }
                if (pEqMiddle != null) {
                    pEqMiddle.setEnabled(currPreset.getMCUIndex() == MCUManager.EqualizerControl.CUSTOM_PRESET.getMCUIndex());
                    pEqMiddle.setProgress(currPreset.getMiddle());
                    pEqMiddle.setOnPreferenceChangeListener(mPreferenceChangeListener);
                }
                if (pEqTreble != null) {
                    pEqTreble.setEnabled(currPreset.getMCUIndex() == MCUManager.EqualizerControl.CUSTOM_PRESET.getMCUIndex());
                    pEqTreble.setProgress(currPreset.getTreble());
                    pEqTreble.setOnPreferenceChangeListener(mPreferenceChangeListener);
                }
                if (pEqSub != null) {
                    pEqSub.setProgress(currSubwoofer);
                    pEqSub.setOnPreferenceChangeListener(mPreferenceChangeListener);
                }
            } catch (RemoteException e) {
                pEqPreset.setEnabled(false);
                pEqPreset.setSummary(R.string.pref_mcu_unaviable_summary);
                pEqBass.setEnabled(false);
                pEqMiddle.setEnabled(false);
                pEqTreble.setEnabled(false);
                pEqSub.setEnabled(false);
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSPChangeListener);
            super.onDestroy();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (!isAdded()) { return; }
            if (s == null) { return; }

            if (s.equals(getString(R.string.sp_sound_eq_preset))) {
                ListPreference pref = (ListPreference)findPreference(s);
                String value = sharedPreferences.getString(s, getResources().getStringArray(R.array.mcu_equalizer_presets_values)[0]);
                if (pref != null) {
                    pref.setValue(value);
                    pref.setTitle(pref.getEntry());

                    Preference pBass = findPreference(getString(R.string.sp_sound_eq_bass));
                    if (pBass != null) {
                        pBass.setEnabled(value.equals(getResources().getStringArray(R.array.mcu_equalizer_presets_values)[MCUManager.EqualizerControl.CUSTOM_PRESET.getMCUIndex()]));
                    }
                    Preference pMiddle = findPreference(getString(R.string.sp_sound_eq_middle));
                    if (pMiddle != null) {
                        pMiddle.setEnabled(value.equals(getResources().getStringArray(R.array.mcu_equalizer_presets_values)[MCUManager.EqualizerControl.CUSTOM_PRESET.getMCUIndex()]));
                    }
                    Preference pTreble = findPreference(getString(R.string.sp_sound_eq_treble));
                    if (pTreble != null) {
                        pTreble.setEnabled(value.equals(getResources().getStringArray(R.array.mcu_equalizer_presets_values)[MCUManager.EqualizerControl.CUSTOM_PRESET.getMCUIndex()]));
                    }
                }
            }

            if (s.equals(getString(R.string.sp_sound_eq_bass))) {
                SeekBarPreference pref = (SeekBarPreference)findPreference(s);
                int value = sharedPreferences.getInt(s, 0);
                if (pref != null) { pref.setProgress(value); }
            }

            if (s.equals(getString(R.string.sp_sound_eq_middle))) {
                SeekBarPreference pref = (SeekBarPreference)findPreference(s);
                int value = sharedPreferences.getInt(s, 0);
                if (pref != null) { pref.setProgress(value); }
            }

            if (s.equals(getString(R.string.sp_sound_eq_treble))) {
                SeekBarPreference pref = (SeekBarPreference)findPreference(s);
                int value = sharedPreferences.getInt(s, 0);
                if (pref != null) { pref.setProgress(value); }
            }

            if (s.equals(getString(R.string.sp_sound_eq_subwoofer))) {
                SeekBarPreference pref = (SeekBarPreference)findPreference(s);
                int value = sharedPreferences.getInt(s, 0);
                if (pref != null) { pref.setProgress(value); }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RadioPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;
        private Preference.OnPreferenceChangeListener mPreferenceChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_radio);
            setHasOptionsMenu(true);

            mSPChangeListener = this;
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(mSPChangeListener);

            mPreferenceChangeListener = (AutoSettingsActivity)getActivity();

            ListPreference pRegion = (ListPreference)findPreference(getString(R.string.sp_radio_region));
            if (pRegion != null) {
                try {
                    MCUManager.RadioControl.RadioRegion region = MCUManager.RadioControl.getRegion();
                    pRegion.setValueIndex(region.getRegionIndex());
                    pRegion.setTitle(pRegion.getEntry());
                    int minAM = region.getMinAMFrequency();
                    int maxAM = region.getMaxAMFrequency();
                    int stepAM = region.getAMStep();
                    float minFM = (float)region.getMinFMFrequency() / 1000;
                    float maxFM = (float)region.getMaxFMFrequency() / 1000;
                    float stepFM = (float)region.getFMStep() / 1000;
                    pRegion.setSummary(String.format(getString(R.string.pref_radio_region_summary), minAM, maxAM, stepAM, minFM, maxFM, stepFM));
                    pRegion.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pRegion.setEnabled(false);
                    pRegion.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSPChangeListener);
            super.onDestroy();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (!isAdded()) { return; }
            if (s == null) { return; }

            if (s.equals(getString(R.string.sp_radio_region))) {
                String value = sharedPreferences.getString(s, "");
                ListPreference pRegion = (ListPreference)findPreference(s);
                if (pRegion != null) {
                    pRegion.setValue(value);
                    pRegion.setTitle(pRegion.getEntry());

                    int index = pRegion.findIndexOfValue(value);
                    MCUManager.RadioControl.RadioRegion region = MCUManager.RadioControl.REGIONS[index];
                    int minAM = region.getMinAMFrequency();
                    int maxAM = region.getMaxAMFrequency();
                    int stepAM = region.getAMStep();
                    float minFM = (float) region.getMinFMFrequency() / 1000;
                    float maxFM = (float) region.getMaxFMFrequency() / 1000;
                    float stepFM = (float) region.getFMStep() / 1000;
                    pRegion.setSummary(String.format(getString(R.string.pref_radio_region_summary), minAM, maxAM, stepAM, minFM, maxFM, stepFM));
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ScreenPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;
        private Preference.OnPreferenceChangeListener mPreferenceChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_screen);
            setHasOptionsMenu(true);

            mSPChangeListener = this;
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(mSPChangeListener);

            mPreferenceChangeListener = (AutoSettingsActivity)getActivity();

            SeekBarPreference pContrast = (SeekBarPreference)findPreference(getString(R.string.sp_screen_contrast));
            if (pContrast != null) {
                try {
                    pContrast.setProgress(MCUManager.ScreenControl.getContrast());
                    pContrast.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pContrast.setEnabled(false);
                    pContrast.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            SwitchPreference pDetectIllum = (SwitchPreference)findPreference(getString(R.string.sp_screen_detect_illumination));
            if (pDetectIllum != null) {
                try {
                    pDetectIllum.setChecked(MCUManager.ScreenControl.getDetectIllumination());
                    pDetectIllum.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pDetectIllum.setEnabled(false);
                    pDetectIllum.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }

            ColorPreference pIllumColor = (ColorPreference)findPreference(getString(R.string.sp_screen_illumination_color));
            if (pIllumColor != null) {
                try {
                    pIllumColor.saveValue(MCUManager.ScreenControl.getIlluminationColor());
                    pIllumColor.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pIllumColor.setEnabled(false);
                    pIllumColor.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSPChangeListener);
            super.onDestroy();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (!isAdded()) { return; }
            if (s == null) { return; }

            if (s.equals(getString(R.string.sp_screen_contrast))) {
                SeekBarPreference pContrast = (SeekBarPreference) findPreference(s);
                int contrast = sharedPreferences.getInt(s, 0);
                if (pContrast != null) { pContrast.setProgress(contrast); }
            }

            if (s.equals(getString(R.string.sp_screen_detect_illumination))) {
                SwitchPreference pDetectIllum = (SwitchPreference)findPreference(s);
                boolean value = sharedPreferences.getBoolean(s, false);
                if (pDetectIllum != null) { pDetectIllum.setChecked(value); }
            }

            if (s.equals(getString(R.string.sp_screen_illumination_color))) {
                ColorPreference pIllumColor = (ColorPreference)findPreference(s);
                int color = sharedPreferences.getInt(s, Color.WHITE);
                if (pIllumColor != null) { pIllumColor.saveValue(color); }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RearViewPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;
        private Preference.OnPreferenceChangeListener mPreferenceChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_rearviewcamera);
            setHasOptionsMenu(true);

            mSPChangeListener = this;
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(mSPChangeListener);

            mPreferenceChangeListener = (AutoSettingsActivity)getActivity();

            SwitchPreference pAddLines = (SwitchPreference)findPreference(getString(R.string.sp_rearview_addlines));
            if (pAddLines != null) {
                try {
                    pAddLines.setChecked(MCUManager.RearViewCamera.getAddParkingLines());
                    pAddLines.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pAddLines.setEnabled(false);
                    pAddLines.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }
            SwitchPreference pMirrorView = (SwitchPreference)findPreference(getString(R.string.sp_rearview_mirror_view));
            if (pMirrorView != null) {
                try {
                    pMirrorView.setChecked(MCUManager.RearViewCamera.getMirrorView());
                    pMirrorView.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pMirrorView.setEnabled(false);
                    pMirrorView.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }
            SwitchPreference pDisableAudio = (SwitchPreference)findPreference(getString(R.string.sp_rearview_disable_audio));
            pDisableAudio.setOnPreferenceChangeListener(mPreferenceChangeListener);
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSPChangeListener);
            super.onDestroy();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (!isAdded()) { return; }
            if (s == null) { return; }

            if (s.equals(getString(R.string.sp_rearview_addlines))) {
                SwitchPreference pAddLines = (SwitchPreference)findPreference(s);
                boolean value = sharedPreferences.getBoolean(s, false);
                if (pAddLines != null) { pAddLines.setChecked(value); }
            }

            if (s.equals(getString(R.string.sp_rearview_mirror_view))) {
                SwitchPreference pMirrorView = (SwitchPreference)findPreference(s);
                boolean value = sharedPreferences.getBoolean(s, false);
                if (pMirrorView != null) { pMirrorView.setChecked(value); }
            }

            if (s.equals(getString(R.string.sp_rearview_disable_audio))) {
                SwitchPreference pDisableAudio = (SwitchPreference)findPreference(s);
                boolean value = sharedPreferences.getBoolean(s, false);
                if (pDisableAudio != null) { pDisableAudio.setChecked(value); }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SWCPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;
        private Preference.OnPreferenceChangeListener mPreferenceChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_swc);
            setHasOptionsMenu(true);

            mSPChangeListener = this;
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(mSPChangeListener);

            mPreferenceChangeListener = (AutoSettingsActivity)getActivity();

            ListPreference pSWCType = (ListPreference)findPreference(getString(R.string.sp_swc_swctype));
            if (pSWCType != null) {
                try {
                    int index;
                    switch (MCUManager.SWCControl.getSWCType()) {
                        case TYPE_1: index = 0; break;
                        case TYPE_2: index = 1; break;
                        default: index = 0;
                    }
                    String[] values = getResources().getStringArray(R.array.mcu_swc_type_values);
                    pSWCType.setValue(values[index]);
                    pSWCType.setSummary(pSWCType.getEntry());
                    pSWCType.setOnPreferenceChangeListener(mPreferenceChangeListener);
                } catch (RemoteException e) {
                    pSWCType.setEnabled(false);
                    pSWCType.setSummary(R.string.pref_mcu_unaviable_summary);
                }
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mSPChangeListener);
            super.onDestroy();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (!isAdded()) { return; }
            if (s == null) { return; }

            if (s.equals(getString(R.string.sp_swc_swctype))) {
                ListPreference pSWCType = (ListPreference)findPreference(s);
                String value = sharedPreferences.getString(s, "");
                if (pSWCType != null) {
                    pSWCType.setValue(value);
                    pSWCType.setSummary(pSWCType.getEntry());
                }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class InfoPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_info);
            setHasOptionsMenu(true);

            Preference pCarNumber = findPreference(getString(R.string.sp_info_carnumber));
            if (pCarNumber != null) {
                try {
                    pCarNumber.setSummary(MCUManager.CarInfo.getCarNumber());
                } catch (RemoteException e) {
                    pCarNumber.setEnabled(false);
                    pCarNumber.setSummary(getString(R.string.pref_mcu_unaviable_summary));
                }
            }
            Preference pSystemVer = findPreference(getString(R.string.sp_info_system));
            if (pSystemVer != null) {
                try {
                    pSystemVer.setSummary(MCUManager.MCUInfo.getSystemVersion());
                } catch (RemoteException e) {
                    pSystemVer.setEnabled(false);
                    pSystemVer.setSummary(getString(R.string.pref_mcu_unaviable_summary));
                }
            }
            Preference pMCUVer = findPreference(getString(R.string.sp_info_mcu));
            if (pMCUVer != null) {
                try {
                    pMCUVer.setSummary(MCUManager.MCUInfo.getMCUVersion());
                } catch (RemoteException e) {
                    pMCUVer.setEnabled(false);
                    pMCUVer.setSummary(getString(R.string.pref_mcu_unaviable_summary));
                }
            }
            Preference pBTVer = findPreference(getString(R.string.sp_info_bt));
            if (pBTVer != null) {
                try {
                    pBTVer.setSummary(MCUManager.MCUInfo.getBluetoothVersion());
                } catch (RemoteException e) {
                    pBTVer.setEnabled(false);
                    pBTVer.setSummary(getString(R.string.pref_mcu_unaviable_summary));
                }
            }
            Preference pCanVer = findPreference(getString(R.string.sp_info_can));
            if (pSystemVer != null) {
                try {
                    pCanVer.setSummary(MCUManager.MCUInfo.getCanBusVersion());
                } catch (RemoteException e) {
                    pCanVer.setEnabled(false);
                    pCanVer.setSummary(getString(R.string.pref_mcu_unaviable_summary));
                }
            }
            Preference pEMMCId = findPreference(getString(R.string.sp_info_emmc));
            if (pEMMCId != null) {
                try {
                    pEMMCId.setSummary(MCUManager.MCUInfo.getEMMCId());
                } catch (RemoteException e) {
                    pEMMCId.setEnabled(false);
                    pEMMCId.setSummary(getString(R.string.pref_mcu_unaviable_summary));
                }
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (!preference.hasKey()) { return false; }

        // Основные
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_general_boottime))) {
            try {
                String[] values = preference.getContext().getResources().getStringArray(R.array.mcu_boottime_values);
                int index = Arrays.asList(values).indexOf((String)value);
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.setBootTime(index);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_general_shortcut_touch_state))) {
            try {
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.setShortcutTouchState((boolean)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_general_switch_media_status))) {
            try {
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.MultimediaControl.setSwitchMediaStatus((boolean)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_general_playvideo))) {
            try {
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.MultimediaControl.setPlayVideoWhileDriving((boolean)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        // Звук
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_volume))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.VolumeControl.setVolume((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_balance))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.VolumeControl.setBalance((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_fade))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.VolumeControl.setFade((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_eq_preset))) {
            try {
                String[] values = preference.getContext().getResources().getStringArray(R.array.mcu_equalizer_presets_values);
                int index = Arrays.asList(values).indexOf((String)value);
                if ((index < 0) || (index >= MCUManager.EqualizerControl.PRESETS.length)) return false;
                MCUManager.EqualizerControl.EqualizerPreset preset = MCUManager.EqualizerControl.PRESETS[index];
                boolean isCustom = preset.getMCUIndex() == MCUManager.EqualizerControl.CUSTOM_PRESET.getMCUIndex();
                ListPreference pEqPreset = (ListPreference)preference;
                pEqPreset.setTitle(pEqPreset.getEntries()[index]);

                SeekBarPreference pBass = (SeekBarPreference)preference.getPreferenceManager().findPreference(getString(R.string.sp_sound_eq_bass));
                if (pBass != null) {
                    pBass.setEnabled(isCustom);
                    if (!isCustom) { pBass.setProgress(preset.getBass()); } else { preset.setBass(pBass.getProgress()); }
                }

                SeekBarPreference pMiddle = (SeekBarPreference)preference.getPreferenceManager().findPreference(getString(R.string.sp_sound_eq_middle));
                if (pMiddle != null) {
                    pMiddle.setEnabled(isCustom);
                    if (!isCustom) { pMiddle.setProgress(preset.getMiddle()); } else { preset.setMiddle(pMiddle.getProgress()); }
                }

                SeekBarPreference pTreble = (SeekBarPreference)preference.getPreferenceManager().findPreference(getString(R.string.sp_sound_eq_treble));
                if (pTreble != null) {
                    pTreble.setEnabled(isCustom);
                    if (!isCustom) { pTreble.setProgress(preset.getTreble()); } else { preset.setTreble(pTreble.getProgress()); }
                }

                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.EqualizerControl.setPreset(preset);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_eq_bass))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.EqualizerControl.setBass((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_eq_middle))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.EqualizerControl.setMiddle((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_eq_treble))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.EqualizerControl.setTreble((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_eq_subwoofer))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.EqualizerControl.setSubwoofer((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_loud))) {
            try {
                mAutoSettingsService.skipAudioSettingsChange();
                MCUManager.EqualizerControl.setLoudMode((boolean)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_speed_compensation))) {
            if ((boolean)value) {
                if (ActivityCompat.checkSelfPermission(preference.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(preference.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Запрос привилегий для получения текущей позиции

                    //ActivityCompat.requestPermissions(AutoSettingsActivity.mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                } else {
                    preference.getContext().startService(new Intent(preference.getContext(), AutoSettingsService.class));
                }
            } else {
                preference.getContext().startService(new Intent(preference.getContext(), AutoSettingsService.class));
            }

            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_min_speed))) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            int maxSpeed = sharedPreferences.getInt(preference.getContext().getString(R.string.sp_sound_max_speed), SpeedLocationListener.DEFAULT_MAX_SPEED);
            if ((int)value >= maxSpeed) { return false; }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_sound_max_speed))) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            int minSpeed = sharedPreferences.getInt(preference.getContext().getString(R.string.sp_sound_min_speed), SpeedLocationListener.DEFAULT_MIN_SPEED);
            if ((int)value <= minSpeed) { return false; }
            return true;
        }

        // Радио
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_radio_region))) {
            try {
                String[] values = getResources().getStringArray(R.array.mcu_radio_regions_values);
                int index = Arrays.asList(values).indexOf((String)value);
                mAutoSettingsService.skipGeneralSettingsChange();
                mAutoSettingsService.skipRadioInfoChange();
                MCUManager.RadioControl.setRegionIndex(index);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        // Экран
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_screen_contrast))) {
            try {
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.ScreenControl.setContrast((int)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_screen_detect_illumination))) {
            try {
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.ScreenControl.setDetectIllumination((boolean)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_screen_illumination_color))) {
            try {
                float[] hsv = {0xFF, 0xFF, 0xFF};
                Color.colorToHSV((int)value, hsv);
                int h = Math.round(hsv[0] / 360 * MCUManager.ScreenControl.HSB_CONSTANTS.MAX_HUE_VALUE);
                int s = Math.round(hsv[1] * MCUManager.ScreenControl.HSB_CONSTANTS.MAX_SATURATION_VALUE);
                int v = Math.round(hsv[2] * MCUManager.ScreenControl.HSB_CONSTANTS.MAX_BRIGHTNESS_VALUE);
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.ScreenControl.setIlluminationHue(h);
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.ScreenControl.setIlluminationSaturation(s);
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.ScreenControl.setIlluminationBrightness(v);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        // Камера заднего вида
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_rearview_addlines))) {
            try {
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.RearViewCamera.setAddParkingLines((boolean)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_rearview_mirror_view))) {
            try {
                mAutoSettingsService.skipGeneralSettingsChange();
                MCUManager.RearViewCamera.setMirrorView((boolean)value);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_rearview_disable_audio))) {
            Intent intent = new Intent("BackAudioStatus");          // TODO: установить константу
            intent.putExtra("backaudio", (boolean)value ? 0 : 1);   // TODO: установить константу
            preference.getContext().sendBroadcast(intent);
            return true;
        }

        //Кнопки руля
        if (preference.getKey().equalsIgnoreCase(getString(R.string.sp_swc_swctype))) {
            try {
                String[] values = preference.getContext().getResources().getStringArray(R.array.mcu_swc_type_values);
                int index = Arrays.asList(values).indexOf((String)value);
                mAutoSettingsService.skipGeneralSettingsChange();
                switch (index) {
                    case 0: {
                        MCUManager.SWCControl.setSWCType(MCUManager.SWCControl.SWCType.TYPE_1);
                        break;
                    }
                    case 1: {
                        MCUManager.SWCControl.setSWCType(MCUManager.SWCControl.SWCType.TYPE_2);
                        break;
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return false;
    }

}
