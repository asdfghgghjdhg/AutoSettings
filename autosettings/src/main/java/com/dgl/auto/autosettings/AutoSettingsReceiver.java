package com.dgl.auto.autosettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dgl.auto.constant.GlobalConstant;
import com.dgl.auto.mcumanager.MCUManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

public class AutoSettingsReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "AutoSettingsReceiver";

    private static final String ACTION_VOLUME_UP = "com.dgl.auto.action.VOLUME_UP";
    private static final String ACTION_VOLUME_DOWN = "com.dgl.auto.action.VOLUME_DOWN";
    private static final String ACTION_VOLUME_MUTE = "com.dgl.auto.action.VOLUME_MUTE";

    public static boolean booted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.i(LOG_TAG, "onReveive: " + action);

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

        // Основные
        try {
            String[] values = context.getResources().getStringArray(R.array.mcu_boottime_values);
            if (sharedPreferences.contains(context.getString(R.string.sp_general_boottime))) {
                int index = Arrays.asList(values).indexOf(sharedPreferences.getString(context.getString(R.string.sp_general_boottime), ""));
                MCUManager.setBootTime(index);
            } else {
                editor.putString(context.getString(R.string.sp_general_boottime), values[MCUManager.getBootTime()]);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_general_playvideo))) {
                MCUManager.MultimediaControl.setPlayVideoWhileDriving(sharedPreferences.getBoolean(context.getString(R.string.sp_general_playvideo), false));
            } else {
                editor.putBoolean(context.getString(R.string.sp_general_playvideo), MCUManager.MultimediaControl.getPlayVideoWhileDriving());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_general_shortcut_touch_state))) {
                MCUManager.setShortcutTouchState(sharedPreferences.getBoolean(context.getString(R.string.sp_general_shortcut_touch_state), false));
            } else {
                editor.putBoolean(context.getString(R.string.sp_general_shortcut_touch_state), MCUManager.getShortcutTouchState());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_general_switch_media_status))) {
                MCUManager.MultimediaControl.setSwitchMediaStatus(sharedPreferences.getBoolean(context.getString(R.string.sp_general_switch_media_status), false));
            } else {
                editor.putBoolean(context.getString(R.string.sp_general_switch_media_status), MCUManager.MultimediaControl.getSwitchMediaStatus());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Звук
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_volume))) {
                MCUManager.VolumeControl.setVolume(sharedPreferences.getInt(context.getString(R.string.sp_sound_volume), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_sound_volume), MCUManager.VolumeControl.getVolume());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_balance))) {
                MCUManager.VolumeControl.setBalance(sharedPreferences.getInt(context.getString(R.string.sp_sound_balance), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_sound_balance), MCUManager.VolumeControl.getBalance());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_fade))) {
                MCUManager.VolumeControl.setFade(sharedPreferences.getInt(context.getString(R.string.sp_sound_fade), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_sound_fade), MCUManager.VolumeControl.getFade());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            String[] values = context.getResources().getStringArray(R.array.mcu_equalizer_presets_values);
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_eq_preset))) {
                String value = sharedPreferences.getString(context.getString(R.string.sp_sound_eq_preset), "");
                int index = Arrays.asList(values).indexOf(value);
                MCUManager.EqualizerControl.setPresetIndex(index);
            } else {
                String value = values[MCUManager.EqualizerControl.getPresetIndex()];
                editor.putString(context.getString(R.string.sp_sound_equalizer), value);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_eq_bass))) {
                MCUManager.EqualizerControl.setBass(sharedPreferences.getInt(context.getString(R.string.sp_sound_eq_bass), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_sound_eq_bass), MCUManager.EqualizerControl.getBass());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_eq_middle))) {
                MCUManager.EqualizerControl.setMiddle(sharedPreferences.getInt(context.getString(R.string.sp_sound_eq_middle), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_sound_eq_middle), MCUManager.EqualizerControl.getMiddle());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_eq_treble))) {
                MCUManager.EqualizerControl.setTreble(sharedPreferences.getInt(context.getString(R.string.sp_sound_eq_treble), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_sound_eq_treble), MCUManager.EqualizerControl.getTreble());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_eq_subwoofer))) {
                MCUManager.EqualizerControl.setSubwoofer(sharedPreferences.getInt(context.getString(R.string.sp_sound_eq_subwoofer), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_sound_eq_subwoofer), MCUManager.EqualizerControl.getSubwoofer());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_sound_loud))) {
                MCUManager.EqualizerControl.setLoudMode(sharedPreferences.getBoolean(context.getString(R.string.sp_sound_loud), false));
            } else {
                editor.putBoolean(context.getString(R.string.sp_sound_loud), MCUManager.EqualizerControl.getLoudMode());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Радио
        try {
            String[] values = context.getResources().getStringArray(R.array.mcu_radio_regions_values);
            if (sharedPreferences.contains(context.getString(R.string.sp_radio_region))) {
                String value = sharedPreferences.getString(context.getString(R.string.sp_radio_region), "");
                int index = Arrays.asList(values).indexOf(value);
                MCUManager.RadioControl.setRegionIndex(index);
            } else {
                editor.putString(context.getString(R.string.sp_radio_region), values[MCUManager.RadioControl.getRegionIndex()]);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            String pref;
            if (MCUManager.RadioControl.getBand() == MCUManager.RadioControl.BAND_AM) {
                pref = context.getString(R.string.sp_radio_lastAMfreq);
            } else {
                pref = context.getString(R.string.sp_radio_lastFMfreq);
            }
            if (sharedPreferences.contains(pref)) {
                MCUManager.RadioControl.setFrequency(sharedPreferences.getInt(pref, 0));
            } else {
                editor.putInt(pref, MCUManager.RadioControl.getFrequency());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Экран
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_screen_contrast))) {
                MCUManager.ScreenControl.setContrast(sharedPreferences.getInt(context.getString(R.string.sp_screen_contrast), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_screen_contrast), MCUManager.ScreenControl.getContrast());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_screen_detect_illumination))) {
                MCUManager.ScreenControl.setDetectIllumination(sharedPreferences.getBoolean(context.getString(R.string.sp_screen_detect_illumination), false));
            } else {
                editor.putBoolean(context.getString(R.string.sp_screen_detect_illumination), MCUManager.ScreenControl.getDetectIllumination());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_screen_illumination_color))) {
                MCUManager.ScreenControl.setIlluminationColor(sharedPreferences.getInt(context.getString(R.string.sp_screen_illumination_color), 0));
            } else {
                editor.putInt(context.getString(R.string.sp_screen_illumination_color), MCUManager.ScreenControl.getIlluminationColor());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Камера заднего вида
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_rearview_addlines))) {
                MCUManager.RearViewCamera.setAddParkingLines(sharedPreferences.getBoolean(context.getString(R.string.sp_rearview_addlines), false));
            } else {
                editor.putBoolean(context.getString(R.string.sp_rearview_addlines), MCUManager.RearViewCamera.getAddParkingLines());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if (sharedPreferences.contains(context.getString(R.string.sp_rearview_mirror_view))) {
                MCUManager.RearViewCamera.setMirrorView(sharedPreferences.getBoolean(context.getString(R.string.sp_rearview_mirror_view), false));
            } else {
                editor.putBoolean(context.getString(R.string.sp_rearview_mirror_view), MCUManager.RearViewCamera.getMirrorView());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (sharedPreferences.contains(context.getString(R.string.sp_rearview_disable_audio))) {
            boolean disableAudio = sharedPreferences.getBoolean(context.getString(R.string.sp_rearview_disable_audio), true);
            Intent intent = new Intent("BackAudioStatus");      // TODO: установить константу
            intent.putExtra("backaudio", disableAudio ? 0 : 1); // TODO: установить константу
            context.sendBroadcast(intent);
        }

        // Кнопки руля
        try {
            String[] values = context.getResources().getStringArray(R.array.mcu_swc_type_values);
            if (sharedPreferences.contains(context.getString(R.string.sp_swc_swctype))) {
                String value = sharedPreferences.getString(context.getString(R.string.sp_swc_swctype), "");
                int index = Arrays.asList(values).indexOf(value);
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
            } else {
                int index;
                switch (MCUManager.SWCControl.getSWCType()) {
                    case TYPE_1: index = 0; break;
                    case TYPE_2: index = 1; break;
                    default: index = 0;
                }
                editor.putString(context.getString(R.string.sp_swc_swctype), values[index]);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        editor.apply();
    }

    private void onInstall(Context context) {
        // TODO: Проверка файла SharedPreferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Основные
        try {
            editor.putString(context.getString(R.string.sp_general_boottime), context.getResources().getStringArray(R.array.mcu_boottime_values)[MCUManager.getBootTime()]);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putBoolean(context.getString(R.string.sp_general_playvideo), MCUManager.MultimediaControl.getPlayVideoWhileDriving());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putBoolean(context.getString(R.string.sp_general_shortcut_touch_state), MCUManager.getShortcutTouchState());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putBoolean(context.getString(R.string.sp_general_switch_media_status), MCUManager.MultimediaControl.getSwitchMediaStatus());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Звук
        try {
            editor.putInt(context.getString(R.string.sp_sound_volume), MCUManager.VolumeControl.getVolume());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putInt(context.getString(R.string.sp_sound_balance), MCUManager.VolumeControl.getBalance());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putInt(context.getString(R.string.sp_sound_fade), MCUManager.VolumeControl.getFade());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putInt(context.getString(R.string.sp_sound_eq_bass), MCUManager.EqualizerControl.getBass());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putInt(context.getString(R.string.sp_sound_eq_middle), MCUManager.EqualizerControl.getMiddle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putInt(context.getString(R.string.sp_sound_eq_treble), MCUManager.EqualizerControl.getTreble());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putInt(context.getString(R.string.sp_sound_eq_subwoofer), MCUManager.EqualizerControl.getSubwoofer());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            String[] values = context.getResources().getStringArray(R.array.mcu_equalizer_presets_values);
            editor.putString(context.getString(R.string.sp_sound_eq_preset), values[MCUManager.EqualizerControl.getPresetIndex()]);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putBoolean(context.getString(R.string.sp_sound_loud), MCUManager.EqualizerControl.getLoudMode());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Радио
        try {
            String[] values = context.getResources().getStringArray(R.array.mcu_radio_regions_values);
            editor.putString(context.getString(R.string.sp_radio_region), values[MCUManager.RadioControl.getRegionIndex()]);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            int freq = MCUManager.RadioControl.getFrequency();
            String pref;
            if (MCUManager.RadioControl.getBand() == MCUManager.RadioControl.BAND_AM) {
                pref = context.getString(R.string.sp_radio_lastAMfreq);
            } else {
                pref = context.getString(R.string.sp_radio_lastFMfreq);
            }
            editor.putInt(pref, freq);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Экран
        try {
            editor.putInt(context.getString(R.string.sp_screen_contrast), MCUManager.ScreenControl.getContrast());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putBoolean(context.getString(R.string.sp_screen_detect_illumination), MCUManager.ScreenControl.getDetectIllumination());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putInt(context.getString(R.string.sp_screen_illumination_color), MCUManager.ScreenControl.getIlluminationColor());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Камера заднего вида
        try {
            editor.putBoolean(context.getString(R.string.sp_rearview_addlines), MCUManager.RearViewCamera.getAddParkingLines());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            editor.putBoolean(context.getString(R.string.sp_rearview_mirror_view), MCUManager.RearViewCamera.getMirrorView());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Кнопки руля
        try {
            String[] values = context.getResources().getStringArray(R.array.mcu_swc_type_values);
            int index;
            switch (MCUManager.SWCControl.getSWCType()) {
                case TYPE_1: index = 0; break;
                case TYPE_2: index = 1; break;
                default: index = 0;
            }
            editor.putString(context.getString(R.string.sp_swc_swctype), values[index]);
        } catch (RemoteException e) {
            e.printStackTrace();
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            int mVolume = MCUManager.VolumeControl.getVolume();
            editor.putInt(context.getString(R.string.sp_sound_volume), mVolume);
        } catch (RemoteException e) { e.printStackTrace(); }
        editor.apply();
    }

    private void onKeyEvent(Context context) {

    }

    private void onRearViewAudioChange(Context context, boolean disabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.sp_rearview_disable_audio), disabled);
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
        try {
            boolean isMuted = MCUManager.VolumeControl.getMuted();
            MCUManager.VolumeControl.setMuted(!isMuted);
            Intent intent = new Intent(GlobalConstant.AutoBroadcastEvent.ACTION_MUTE_STATUS);
            intent.putExtra(GlobalConstant.AutoBroadcastEvent.KEY_MUTE_STATUS, !isMuted);
            context.sendBroadcast(intent);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
