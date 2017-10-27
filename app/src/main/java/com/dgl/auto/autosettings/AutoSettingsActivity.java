package com.dgl.auto.autosettings;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.preference.SeekBarPreference;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.ISettingManager;
import com.dgl.auto.RadioManager;
import com.dgl.auto.SettingManager;
import com.jaredrummler.android.colorpicker.ColorPreference;

import java.util.List;

public class AutoSettingsActivity extends AppCompatPreferenceActivity {
    private static ISettingManager settingManager = null;
    private static IRadioManager radioManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingManager = SettingManager.getInstance();
        radioManager = RadioManager.getInstance();

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
                || RadioPreferenceFragment.class.getName().equals(fragmentName)
                || ScreenPreferenceFragment.class.getName().equals(fragmentName)
                || InfoPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            SwitchPreference beepPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_general_beep));
            ListPreference boottimePref = (ListPreference)findPreference(getResources().getString(R.string.sp_general_boottime));
            SwitchPreference videoPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_general_playvideo));
            SwitchPreference stsPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_general_shortcut_touch_state));
            SwitchPreference smsPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_general_switch_media_status));
            SwitchPreference revAuxPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_general_reverse_aux));
            SwitchPreference mirrorPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_general_mirror_rearview));
            ListPreference swcPref = (ListPreference)findPreference(getResources().getString(R.string.sp_general_swctype));
            Preference usb0Pref = findPreference(getResources().getString(R.string.sp_general_usb0type));
            Preference usb1Pref = findPreference(getResources().getString(R.string.sp_general_usb1type));

            boolean beep = false;
            int boottime = 0;
            boolean playVideo = false;
            boolean shortcutTouchState = false;
            boolean switchMediaStatus = false;
            boolean reverseAux = false;
            boolean mirrorRearView = false;
            int swcType = 0;
            int usb0Type = 0;
            int usb1Type = 0;

            if (settingManager != null) {
                try { beep = settingManager.getBeep(); } catch (RemoteException e) { beepPref.setEnabled(false); }
                try { boottime = settingManager.getBootTime(); } catch (RemoteException e) { boottimePref.setEnabled(false); }
                try { videoPref.setEnabled(settingManager.getCanWatchVideo()); } catch (RemoteException e) { videoPref.setEnabled(false); }
                try { playVideo = settingManager.getCanWatchVideoWhileDriver(); } catch (RemoteException e) { videoPref.setEnabled(false); }
                try { shortcutTouchState = settingManager.getShortcutTouchState(); } catch (RemoteException e) { stsPref.setEnabled(false); }
                try { switchMediaStatus = settingManager.GetSwitchMediaStatus(); } catch (RemoteException e) { smsPref.setEnabled(false); }
                try { reverseAux = settingManager.getReverseAuxLine(); } catch (RemoteException e) { revAuxPref.setEnabled(false); }
                try { mirrorRearView = settingManager.getReverseMirror(); } catch (RemoteException e) { mirrorPref.setEnabled(false); }
                try { swcType = settingManager.getSWCTypeValue(); } catch (RemoteException e) { swcPref.setEnabled(false); }
                try { usb0Type = settingManager.getUSB0TypeValue(); } catch (RemoteException e) { usb0Pref.setEnabled(false); }
                try { usb1Type = settingManager.getUSB1TypeValue(); } catch (RemoteException e) { usb1Pref.setEnabled(false); }
            } else {
                beepPref.setEnabled(false);
                boottimePref.setEnabled(false);
                videoPref.setEnabled(false);
                stsPref.setEnabled(false);
                smsPref.setEnabled(false);
                revAuxPref.setEnabled(false);
                mirrorPref.setEnabled(false);
                swcPref.setEnabled(false);
                usb0Pref.setEnabled(false);
                usb1Pref.setEnabled(false);
            }

            beepPref.setChecked(beep);
            boottimePref.setValue(String.valueOf(boottime));
            boottimePref.setSummary(boottimePref.getEntry());
            videoPref.setChecked(playVideo);
            stsPref.setChecked(shortcutTouchState);
            smsPref.setChecked(switchMediaStatus);
            revAuxPref.setChecked(reverseAux);
            mirrorPref.setChecked(mirrorRearView);
            swcPref.setValue(String.valueOf(swcType));
            swcPref.setSummary(String.format(getResources().getString(R.string.pref_general_swctype_summary), swcPref.getEntry()));
            usb0Pref.setSummary(String.valueOf(usb0Type));
            usb1Pref.setSummary(String.valueOf(usb1Type));

            beepPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            boottimePref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            videoPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            stsPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            smsPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            revAuxPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            mirrorPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            swcPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
        }
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
            Preference voloffsetPref = findPreference(getResources().getString(R.string.sp_sound_volume_offset));

            int currVol = 0;
            int currBalance = 0;
            int currFade = 0;
            int currEq = 0;
            int currBass = 0;
            int currMiddle = 0;
            int currTreble = 0;
            int currSubwoofer = 0;
            boolean currLoud = false;
            byte[] offset = {};

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
                try { offset = settingManager.getVolumeOffset(); } catch (RemoteException e) {voloffsetPref.setEnabled(false); }
            } else {
                volumePref.setEnabled(false);
                balancePref.setEnabled(false);
                fadePref.setEnabled(false);
                eqPref.setEnabled(false);
                loudPref.setEnabled(false);
                voloffsetPref.setEnabled(false);
            }

            volumePref.setProgress(currVol);
            balancePref.setProgress(currBalance);
            fadePref.setProgress(currFade);
            loudPref.setChecked(currLoud);

            String[] names = eqPref.getContext().getResources().getStringArray(R.array.sound_equalizer_presets_names);
            String summary = currEq == 0 ? eqPref.getContext().getResources().getString(R.string.pref_sound_eq_summarycustom) : eqPref.getContext().getResources().getString(R.string.pref_sound_eq_summary);
            eqPref.setSummary(String.format(summary, names[currEq], currBass, currMiddle, currTreble, currSubwoofer));

            String bytes = "";
            for (byte b: offset) {
                bytes = bytes + String.valueOf(b) + " ";
            }
            summary = String.format(voloffsetPref.getContext().getResources().getString(R.string.pref_sound_volume_offset_summary), bytes);
            voloffsetPref.setSummary(summary);

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
            if (volume > 20) {
                volumePref.setIcon(R.drawable.ic_volume_up_black_24dp);
            } else if (volume == 0) {
                volumePref.setIcon(R.drawable.ic_volume_mute_black_24dp);
            } else {
                volumePref.setIcon(R.drawable.ic_volume_down_black_24dp);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RadioPreferenceFragment extends PreferenceFragment {
        private static RadioPreferenceFragment mInstance;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mInstance = this;

            addPreferencesFromResource(R.xml.pref_radio);

            ListPreference regionPref = (ListPreference)findPreference(getResources().getString(R.string.sp_radio_region));

            int currRegion = 0;

            if (settingManager != null) {
                try { currRegion = settingManager.getRadioField(); } catch (RemoteException e) { regionPref.setEnabled(false); }
            } else {
                regionPref.setEnabled(false);
            }

            regionPref.setValueIndex(currRegion);
            regionPref.setTitle(regionPref.getEntry());
            String summary = "";
            if (radioManager != null) {
                try {
                    int minAM = radioManager.getMinAMFreq();
                    int maxAM = radioManager.getMaxAMFreq();
                    int stepAM = radioManager.getAMStep();
                    float minFM = (float)radioManager.getMinFMFreq() / 100;
                    float maxFM = (float)radioManager.getMaxFMFreq() / 100;
                    float stepFM = (float)radioManager.getFMStep() / 100;
                    summary = String.format(regionPref.getContext().getResources().getString(R.string.pref_radio_region_summary), minAM, maxAM, stepAM, minFM, maxFM, stepFM);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            regionPref.setSummary(summary);

            regionPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
        }

        public static RadioPreferenceFragment getInstance() {
            return mInstance;
        }

        public void updateRegionInfo() {
            ListPreference regionPref = (ListPreference)findPreference(getResources().getString(R.string.sp_radio_region));
            try {
                int currRegion = settingManager.getRadioField();
                regionPref.setValueIndex(currRegion);
                regionPref.setTitle(regionPref.getEntry());
                int minAM = radioManager.getMinAMFreq();
                int maxAM = radioManager.getMaxAMFreq();
                int stepAM = radioManager.getAMStep();
                float minFM = (float)radioManager.getMinFMFreq() / 100;
                float maxFM = (float)radioManager.getMaxFMFreq() / 100;
                float stepFM = (float)radioManager.getFMStep() / 100;
                String summary = String.format(regionPref.getContext().getResources().getString(R.string.pref_radio_region_summary), minAM, maxAM, stepAM, minFM, maxFM, stepFM);
                regionPref.setSummary(summary);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ScreenPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_screen);

            SeekBarPreference brightnessPref = (SeekBarPreference)findPreference(getResources().getString(R.string.sp_screen_brightness));
            SeekBarPreference contrastPref = (SeekBarPreference)findPreference(getResources().getString(R.string.sp_screen_contrast));
            SwitchPreference detectPref = (SwitchPreference)findPreference(getResources().getString(R.string.sp_screen_detect_illumination));
            ColorPreference colorPref = (ColorPreference)findPreference(getResources().getString(R.string.sp_screen_illumination_color));

            int currBrightness = 0;
            int currContrast = 0;
            int currHue = 0;
            int currSaturation = 0;
            int currValue = 0;
            boolean currDetect = false;

            if (settingManager != null) {
                try { currBrightness = settingManager.getScreenBrightness(); } catch (RemoteException e) { brightnessPref.setEnabled(false); }
                try { currContrast = settingManager.getContrast(); } catch (RemoteException e) { contrastPref.setEnabled(false); }
                try { currHue = settingManager.getHueSetting(); } catch (RemoteException e) { colorPref.setEnabled(false); }
                try { currSaturation = settingManager.getSaturation(); } catch (RemoteException e) { colorPref.setEnabled(false); }
                try { currValue = settingManager.getBright(); } catch (RemoteException e) { colorPref.setEnabled(false); }
                try { currDetect = settingManager.getIllumeDetection(); } catch (RemoteException e) { detectPref.setEnabled(false); }
            } else {
                brightnessPref.setEnabled(false);
                contrastPref.setEnabled(false);
                colorPref.setEnabled(false);
                detectPref.setEnabled(false);
            }

            brightnessPref.setProgress(currBrightness);
            contrastPref.setProgress(currContrast);
            detectPref.setChecked(currDetect);

            float h = (float)currHue / 127 * 360;
            float s = (float)currSaturation / 127;
            float v = (float)currValue / 127;
            float[] hsv = {h, s, v};
            colorPref.saveValue(Color.HSVToColor(hsv));

            brightnessPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            contrastPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            detectPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            colorPref.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class InfoPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_info);

            if (settingManager != null) {
                try {
                    String carNumber = settingManager.getCarNumber();
                    Preference pref = findPreference(getResources().getString(R.string.sp_info_carnumber));
                    pref.setSummary(carNumber);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    String version = settingManager.getSystemVersion();
                    Preference pref = findPreference(getResources().getString(R.string.sp_info_system));
                    pref.setSummary(version);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    String version = settingManager.GetMcuVersion();
                    Preference pref = findPreference(getResources().getString(R.string.sp_info_mcu));
                    pref.setSummary(version);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    String version = settingManager.getBTVersion();
                    Preference pref = findPreference(getResources().getString(R.string.sp_info_bt));
                    pref.setSummary(version);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    String version = settingManager.getCanVersion();
                    Preference pref = findPreference(getResources().getString(R.string.sp_info_can));
                    pref.setSummary(version);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    String id = settingManager.GetEmmcId();
                    Preference pref = findPreference(getResources().getString(R.string.sp_info_emmc));
                    pref.setSummary(id);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Preference.OnPreferenceChangeListener mcuPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if (!preference.hasKey()) { return false; }

            switch (preference.getKey()) {
                case "general_beep": {
                    try {
                        settingManager.setBeep((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "general_boottime": {
                    try {
                        settingManager.setBootTime(Integer.valueOf((String)value));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "general_playvideo": {
                    try {
                        settingManager.setCanWatchVideoWhileDriver((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "general_shortcut_touch_state": {
                    try {
                        settingManager.setShortcutTouchState((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "general_switch_media_status": {
                    try {
                        settingManager.SetSwitchMediaStatus((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "general_reverse_aux": {
                    try {
                        settingManager.setReverseAuxLine((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "general_mirror_rearview": {
                    try {
                        settingManager.setReverseMirror((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "general_swctype": {
                    try {
                        settingManager.setSWCTypeValue(Integer.valueOf((String)value));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }

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

                case "radio_region": {
                    try {
                        settingManager.setRadioField(Integer.valueOf((String)value));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }

                case "screen_brightness": {
                    try {
                        //settingManager.setBrightness((int)value);
                        ContentResolver c = preference.getContext().getContentResolver();
                        Settings.System.putInt(c, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        Settings.System.putInt(c, Settings.System.SCREEN_BRIGHTNESS, (int)value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "screen_contrast": {
                    try {
                        settingManager.setContrast((int)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "screen_detect_illumination": {
                    try {
                        settingManager.setIllumeDetection((boolean)value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                case "screen_illumination_color": {
                    try {
                        float[] hsv = {0xFF, 0xFF, 0xFF};
                        Color.colorToHSV((int)value, hsv);
                        int h = Math.round(hsv[0] / 360 * 127);
                        int s = Math.round(hsv[1] * 127);
                        int v = Math.round(hsv[2] * 127);
                        settingManager.setHueSetting(h);
                        settingManager.setSaturation(s);
                        settingManager.setBright(v);
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
            /*String stringValue = value.toString();

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
            }*/
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
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            SwitchPreference test = (SwitchPreference)findPreference("example_switch");
            test.setOnPreferenceChangeListener(mcuPreferenceChangeListener);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
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
    }*/
}
