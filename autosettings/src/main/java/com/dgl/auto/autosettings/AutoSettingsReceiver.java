package com.dgl.auto.autosettings;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.ISettingManager;
import com.dgl.auto.RadioManager;
import com.dgl.auto.SettingManager;
import com.dgl.auto.constant.GlobalConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AutoSettingsReceiver extends BroadcastReceiver {

    private static final String ACTION_VOLUME_UP = "com.dgl.auto.action.VOLUME_UP";
    private static final String ACTION_VOLUME_DOWN = "com.dgl.auto.action.VOLUME_DOWN";
    private static final String ACTION_VOLUME_MUTE = "com.dgl.auto.action.VOLUME_MUTE";

    private static ISettingManager settingManager = null;
    private static IRadioManager radioManager = null;
    public static boolean booted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        File fileToWrite = new File(context.getFilesDir(), "broadcast.txt");
        String date_str = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US).format(System.currentTimeMillis());
        String output =  date_str + ": action:" + action + "\n";

        try {
            FileOutputStream outPutStream = new FileOutputStream(fileToWrite, true);
            OutputStreamWriter outPutStreamWriter = new OutputStreamWriter(outPutStream);
            outPutStreamWriter.write(output);
            outPutStreamWriter.close();
            outPutStream.flush();
            outPutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (settingManager == null) { settingManager = SettingManager.getInstance(); }
        if (radioManager == null) { radioManager = RadioManager.getInstance(); }

        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(action)/* || (Intent.ACTION_USER_PRESENT.equalsIgnoreCase(action))*/) {
            onBoot(context);
            booted = true;
        }

        if (Intent.ACTION_PACKAGE_REPLACED.equalsIgnoreCase(action) || Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(action)) {
            onInstall(context);
            booted = true;
        }

        // TODO: find or set autochips.intent.action.QB_POWEROFF constant
        if (action.equalsIgnoreCase("autochips.intent.action.QB_POWEROFF")) {
            onPowerOff(context);
            booted = false;
        }

        // TODO: find or set autochips.intent.action.QB_POWERON constant
        if (action.equalsIgnoreCase("autochips.intent.action.QB_POWERON")) {
            onPowerOn(context);
            booted = true;
        }

        if (GlobalConstant.AutoBroadcastEvent.ACTION_MUTE_STATUS.equalsIgnoreCase(action)) {
            if (!booted) { return; }
            onVolumeChange(context);
        }

        if (GlobalConstant.AutoBroadcastEvent.ACTION_KEY_EVENT.equalsIgnoreCase(action)) {
            onKeyEvent(context);
        }

        // TODO: find or set BackAudioStatus constant
        if (action.equalsIgnoreCase("BackAudioStatus")) {
            if (!booted) { return; }
            int backaudio = intent.getIntExtra("backaudio", 0);
            onRearViewAudioChange(context, backaudio != 1);
        }

        if (ACTION_VOLUME_DOWN.equalsIgnoreCase(action)) {
            volumeDown(context);
        }

        if (ACTION_VOLUME_UP.equalsIgnoreCase(action)) {
            volumeUp(context);
        }

        if (ACTION_VOLUME_MUTE.equalsIgnoreCase(action)) {
            volumeMute(context);
        }
    }

    private void onBoot(Context context) {
        context.startService(new Intent(context, AutoSettingsService.class));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (settingManager != null) {
            // Основные
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_beep))) {
                    settingManager.setBeep(sharedPreferences.getBoolean(context.getString(R.string.sp_general_beep), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_general_beep), settingManager.getBeep());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_boottime))) {
                    settingManager.setBootTime(Integer.valueOf(sharedPreferences.getString(context.getString(R.string.sp_general_boottime), "0")));
                } else {
                    editor.putString(context.getString(R.string.sp_general_boottime), String.valueOf(settingManager.getBootTime()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_playvideo))) {
                    settingManager.setCanWatchVideoWhileDriver(sharedPreferences.getBoolean(context.getString(R.string.sp_general_playvideo), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_general_playvideo), settingManager.getCanWatchVideoWhileDriver());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_shortcut_touch_state))) {
                    settingManager.setShortcutTouchState(sharedPreferences.getBoolean(context.getString(R.string.sp_general_shortcut_touch_state), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_general_shortcut_touch_state), settingManager.getShortcutTouchState());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_switch_media_status))) {
                    settingManager.SetSwitchMediaStatus(sharedPreferences.getBoolean(context.getString(R.string.sp_general_switch_media_status), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_general_switch_media_status), settingManager.GetSwitchMediaStatus());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_rearview_addlines))) {
                    settingManager.setReverseAuxLine(sharedPreferences.getBoolean(context.getString(R.string.sp_general_rearview_addlines), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_general_rearview_addlines), settingManager.getReverseAuxLine());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_mirror_rearview))) {
                    settingManager.setReverseMirror(sharedPreferences.getBoolean(context.getString(R.string.sp_general_mirror_rearview), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_general_mirror_rearview), settingManager.getReverseMirror());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_rearview_disable_audio))) {
                    boolean disableAudio = sharedPreferences.getBoolean(context.getString(R.string.sp_general_rearview_disable_audio), true);
                    // TODO: find or set BackAudioStatus constant
                    Intent intent = new Intent("BackAudioStatus");
                    intent.putExtra("backaudio", disableAudio ? 0 : 1);
                    context.sendBroadcast(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_swctype))) {
                    settingManager.setSWCTypeValue(Integer.valueOf(sharedPreferences.getString(context.getString(R.string.sp_general_swctype), "0")));
                } else {
                    editor.putString(context.getString(R.string.sp_general_swctype), String.valueOf(settingManager.getSWCTypeValue()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_usb0type))) {
                    settingManager.setUSB0TypeValue(Integer.valueOf(sharedPreferences.getString(context.getString(R.string.sp_general_usb0type), "0")));
                } else {
                    editor.putString(context.getString(R.string.sp_general_usb0type), String.valueOf(settingManager.getUSB0TypeValue()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_general_usb1type))) {
                    settingManager.setUSB1TypeValue(Integer.valueOf(sharedPreferences.getString(context.getString(R.string.sp_general_usb1type), "0")));
                } else {
                    editor.putString(context.getString(R.string.sp_general_usb1type), String.valueOf(settingManager.getUSB1TypeValue()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Звук
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_volume))) {
                    settingManager.setMcuVol(sharedPreferences.getInt(context.getString(R.string.sp_sound_volume), 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_volume), settingManager.getMcuVol());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_balance))) {
                    settingManager.setBalance(sharedPreferences.getInt(context.getString(R.string.sp_sound_balance), 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_balance), settingManager.getBalance());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_fade))) {
                    settingManager.setFade(sharedPreferences.getInt(context.getString(R.string.sp_sound_fade), 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_fade), settingManager.getFade());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_equalizer))) {
                    settingManager.setEQ(sharedPreferences.getInt(context.getString(R.string.sp_sound_equalizer), 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_equalizer), settingManager.getEQ());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.BASS_SUBKEY)) {
                    settingManager.setBass(sharedPreferences.getInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.BASS_SUBKEY, 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.BASS_SUBKEY, settingManager.getBass());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.MIDDLE_SUBKEY)) {
                    settingManager.setMiddle(sharedPreferences.getInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.MIDDLE_SUBKEY, 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.MIDDLE_SUBKEY, settingManager.getMiddle());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.TREBLE_SUBKEY)) {
                    settingManager.setTreble(sharedPreferences.getInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.TREBLE_SUBKEY, 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.TREBLE_SUBKEY, settingManager.getTreble());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.SUBWOOFER_SUBKEY)) {
                    settingManager.setSubwoofer(sharedPreferences.getInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.SUBWOOFER_SUBKEY, 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.SUBWOOFER_SUBKEY, settingManager.getSubwoofer());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_sound_loud))) {
                    settingManager.setLound(sharedPreferences.getBoolean(context.getString(R.string.sp_sound_loud), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_sound_loud), settingManager.getLound());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Радио
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_radio_region))) {
                    int savedRegion = Integer.valueOf(sharedPreferences.getString(context.getString(R.string.sp_radio_region), "0"));
                    if (savedRegion != settingManager.getRadioField()) { settingManager.setRadioField(savedRegion); }
                } else {
                    editor.putString(context.getString(R.string.sp_radio_region), String.valueOf(settingManager.getRadioField()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Экран
            /*try {
                if (sharedPreferences.contains(context.getString(R.string.sp_screen_brightness))) {
                    //settingManager.setBrightness(sharedPreferences.getInt(context.getString(R.string.sp_screen_brightness), 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_screen_brightness), settingManager.getScreenBrightness());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_screen_contrast))) {
                    settingManager.setContrast(sharedPreferences.getInt(context.getString(R.string.sp_screen_contrast), 0));
                } else {
                    editor.putInt(context.getString(R.string.sp_screen_contrast), settingManager.getContrast());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_screen_detect_illumination))) {
                    settingManager.setIllumeDetection(sharedPreferences.getBoolean(context.getString(R.string.sp_screen_detect_illumination), false));
                } else {
                    editor.putBoolean(context.getString(R.string.sp_screen_detect_illumination), settingManager.getIllumeDetection());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getString(R.string.sp_screen_illumination_color))) {
                    float[] hsv = {0xFF, 0xFF, 0xFF};
                    Color.colorToHSV(sharedPreferences.getInt(context.getString(R.string.sp_screen_illumination_color), 0), hsv);
                    int h = Math.round(hsv[0] / 360 * 127);
                    int s = Math.round(hsv[1] * 127);
                    int v = Math.round(hsv[2] * 127);
                    settingManager.setHueSetting(h);
                    settingManager.setSaturation(s);
                    settingManager.setBright(v);
                } else {
                    float h = (float)settingManager.getHueSetting() / 127 * 360;
                    float s = (float)settingManager.getSaturation() / 127;
                    float v = (float)settingManager.getBright() / 127;
                    float[] hsv = {h, s, v};
                    editor.putInt(context.getString(R.string.sp_screen_illumination_color), Color.HSVToColor(hsv));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (radioManager != null) {
            try {
                int currBand = radioManager.getBand();
                String sp;
                if ((currBand == IRadioManager.IRadioConstant.BAND_AM_1) || (currBand == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    sp = context.getString(R.string.sp_radio_lastAMfreq);
                } else {
                    sp = context.getString(R.string.sp_radio_lastFMfreq);
                }
                if (sharedPreferences.contains(sp)) {
                    radioManager.setFreq((char)sharedPreferences.getInt(sp, IRadioManager.IRadioConstant.RADIO_FM_DEFUALT_FREQ));
                } else {
                    editor.putInt(sp, radioManager.getCurrFreq());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        editor.apply();
    }

    private void onInstall(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (settingManager != null) {
            // Основные
            try {
                editor.putBoolean(context.getString(R.string.sp_general_beep), settingManager.getBeep());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putString(context.getString(R.string.sp_general_boottime), String.valueOf(settingManager.getBootTime()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getString(R.string.sp_general_playvideo), settingManager.getCanWatchVideoWhileDriver());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getString(R.string.sp_general_shortcut_touch_state), settingManager.getShortcutTouchState());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getString(R.string.sp_general_switch_media_status), settingManager.GetSwitchMediaStatus());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getString(R.string.sp_general_rearview_addlines), settingManager.getReverseAuxLine());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getString(R.string.sp_general_mirror_rearview), settingManager.getReverseMirror());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putString(context.getString(R.string.sp_general_swctype), String.valueOf(settingManager.getSWCTypeValue()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putString(context.getString(R.string.sp_general_usb0type), String.valueOf(settingManager.getUSB0TypeValue()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putString(context.getString(R.string.sp_general_usb1type), String.valueOf(settingManager.getUSB1TypeValue()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Звук
            try {
                editor.putInt(context.getString(R.string.sp_sound_volume), settingManager.getMcuVol());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getString(R.string.sp_sound_balance), settingManager.getBalance());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getString(R.string.sp_sound_fade), settingManager.getFade());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.BASS_SUBKEY, settingManager.getBass());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.MIDDLE_SUBKEY, settingManager.getMiddle());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.TREBLE_SUBKEY, settingManager.getTreble());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getString(R.string.sp_sound_equalizer)+EqualizerPreference.SUBWOOFER_SUBKEY, settingManager.getSubwoofer());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getString(R.string.sp_sound_equalizer), settingManager.getEQ());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getString(R.string.sp_sound_loud), settingManager.getLound());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Радио
            try {
                editor.putString(context.getString(R.string.sp_radio_region), String.valueOf(settingManager.getRadioField()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Экран
            /*try {
                editor.putInt(context.getString(R.string.sp_screen_brightness), settingManager.getScreenBrightness());
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            try {
                editor.putInt(context.getString(R.string.sp_screen_contrast), settingManager.getContrast());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getString(R.string.sp_screen_detect_illumination), settingManager.getIllumeDetection());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                float h = (float)settingManager.getHueSetting() / 127 * 360;
                float s = (float)settingManager.getSaturation() / 127;
                float v = (float)settingManager.getBright() / 127;
                float[] hsv = {h, s, v};
                editor.putInt(context.getString(R.string.sp_screen_illumination_color), Color.HSVToColor(hsv));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (radioManager != null) {
            try {
                int currBand = radioManager.getBand();
                if ((currBand == IRadioManager.IRadioConstant.BAND_AM_1) || (currBand == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    editor.putInt(context.getString(R.string.sp_radio_lastAMfreq), radioManager.getCurrFreq());
                } else {
                    editor.putInt(context.getString(R.string.sp_radio_lastFMfreq), radioManager.getCurrFreq());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            /*try {
                char[] freqs = radioManager.getFreqList();
                for (int i = 0; i < freqs.length; i++) {
                    editor.putInt(String.format(context.getString(R.string.sp_radio_presets), i + 1), (int)freqs[i]);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
        }

        editor.apply();
    }

    private void onPowerOff(Context context) {
        context.stopService(new Intent(context, AutoSettingsService.class));

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(new Intent(AutoSettingsActivity.ACTION_FINISH));
    }

    private void onPowerOn(Context context) {
        context.startService(new Intent(context, AutoSettingsService.class));
    }

    private void onVolumeChange(Context context) {
        if (settingManager == null) { return; }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            int mVolume = settingManager.getMcuVol();
            editor.putInt(context.getString(R.string.sp_sound_volume), mVolume);
            AutoSettingsActivity.SoundPreferenceFragment fragment = AutoSettingsActivity.SoundPreferenceFragment.getInstance();
            if (fragment != null) {
                fragment.updateVolume(mVolume);
            }
        } catch (RemoteException e) { e.printStackTrace(); }
        editor.apply();
    }

    private void onKeyEvent(Context context) {

    }

    private void onRearViewAudioChange(Context context, boolean disabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.sp_general_rearview_disable_audio), disabled);
        AutoSettingsActivity.GeneralPreferenceFragment fragment = AutoSettingsActivity.GeneralPreferenceFragment.getInstance();
        if (fragment != null) {
            try {
                SwitchPreference pref = (SwitchPreference)fragment.findPreference(context.getString(R.string.sp_general_rearview_disable_audio));
                pref.setChecked(disabled);
            } finally { }
        }
        editor.apply();
    }

    private void volumeDown(Context context) {
        Intent intent = new Intent("AutoKeyDown");      // TODO: Set constant value
        intent.putExtra("key", 0x10e);            // TODO: Set constant value
        context.sendBroadcast(intent);
        intent = new Intent("AutoKeyUp");               // TODO: Set constant value
        intent.putExtra("key", 0x10e);            // TODO: Set constant value
        context.sendBroadcast(intent);
    }

    private void volumeUp(Context context) {
        Intent intent = new Intent("AutoKeyDown");      // TODO: Set constant value
        intent.putExtra("key", 0x10d);            // TODO: Set constant value
        context.sendBroadcast(intent);
        intent = new Intent("AutoKeyUp");               // TODO: Set constant value
        intent.putExtra("key", 0x10d);            // TODO: Set constant value
        context.sendBroadcast(intent);
    }

    private void volumeMute(Context context) {
        if (settingManager != null) {
            try {
                boolean isMuted = settingManager.isMcuMute();
                settingManager.setMcuMute(isMuted ? 0 : 1);
                Intent intent = new Intent(GlobalConstant.AutoBroadcastEvent.ACTION_MUTE_STATUS);
                intent.putExtra(GlobalConstant.AutoBroadcastEvent.KEY_MUTE_STATUS, !isMuted);
                context.sendBroadcast(intent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
