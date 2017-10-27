package com.dgl.auto.autosettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.RemoteException;
import android.preference.PreferenceFragment;
import android.preference.SeekBarPreference;
import android.widget.Toast;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.ISettingManager;
import com.dgl.auto.RadioManager;
import com.dgl.auto.SettingManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class AutoSettingsReceiver extends BroadcastReceiver {

    private static ISettingManager settingManager = null;
    private static IRadioManager radioManager = null;
    public static boolean booted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        File fileToWrite = new File(context.getFilesDir(), "broadcast.txt");
        String date_str = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(System.currentTimeMillis());
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

        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) || action.equalsIgnoreCase("android.intent.action.QUICKBOOT_POWERON")/* || (action.equalsIgnoreCase(Intent.ACTION_USER_PRESENT))*/) {
            setMCUValues(context);
            context.startService(new Intent(context, AutoSettingsService.class));
            booted = true;
        }

        if (action.equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED)) {
            getMCUValues(context);
            booted = true;
        }

        if (action.equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED)) {
            getMCUValues(context);
            booted = true;
        }

        if (action.equalsIgnoreCase("AutoMuteStatus")) {
            if (!booted) { return; }
            volumeChange(context);
        }
    }

    private void setMCUValues(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (settingManager != null) {
            // Звук
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_volume))) {
                    settingManager.setMcuVol(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_volume), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_volume), settingManager.getMcuVol());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_balance))) {
                    settingManager.setBalance(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_balance), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_balance), settingManager.getBalance());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_fade))) {
                    settingManager.setFade(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_fade), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_fade), settingManager.getFade());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_equalizer_preset))) {
                    settingManager.setEQ(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_equalizer_preset), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_preset), settingManager.getEQ());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_equalizer_bass))) {
                    settingManager.setBass(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_equalizer_bass), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_bass), settingManager.getBass());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_equalizer_middle))) {
                    settingManager.setMiddle(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_equalizer_middle), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_middle), settingManager.getMiddle());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_equalizer_treble))) {
                    settingManager.setTreble(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_equalizer_treble), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_treble), settingManager.getTreble());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_equalizer_subwoofer))) {
                    settingManager.setSubwoofer(sharedPreferences.getInt(context.getResources().getString(R.string.sp_sound_equalizer_subwoofer), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_subwoofer), settingManager.getSubwoofer());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_sound_loud))) {
                    settingManager.setLound(sharedPreferences.getBoolean(context.getResources().getString(R.string.sp_sound_loud), false));
                } else {
                    editor.putBoolean(context.getResources().getString(R.string.sp_sound_loud), settingManager.getLound());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Радио
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_radio_region))) {
                    int savedRegion = Integer.valueOf(sharedPreferences.getString(context.getResources().getString(R.string.sp_radio_region), "0"));
                    if (savedRegion != settingManager.getRadioField()) { settingManager.setRadioField(savedRegion); }
                } else {
                    editor.putString(context.getResources().getString(R.string.sp_radio_region), String.valueOf(settingManager.getRadioField()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Экран
            /*try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_screen_brightness))) {
                    //settingManager.setBrightness(sharedPreferences.getInt(context.getResources().getString(R.string.sp_screen_brightness), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_screen_brightness), settingManager.getScreenBrightness());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_screen_contrast))) {
                    settingManager.setContrast(sharedPreferences.getInt(context.getResources().getString(R.string.sp_screen_contrast), 0));
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_screen_contrast), settingManager.getContrast());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_screen_detect_illumination))) {
                    settingManager.setIllumeDetection(sharedPreferences.getBoolean(context.getResources().getString(R.string.sp_screen_detect_illumination), false));
                } else {
                    editor.putBoolean(context.getResources().getString(R.string.sp_screen_detect_illumination), settingManager.getIllumeDetection());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (sharedPreferences.contains(context.getResources().getString(R.string.sp_screen_illumination_color))) {
                    float[] hsv = {0xFF, 0xFF, 0xFF};
                    Color.colorToHSV(sharedPreferences.getInt(context.getResources().getString(R.string.sp_screen_illumination_color), 0), hsv);
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
                    editor.putInt(context.getResources().getString(R.string.sp_screen_illumination_color), Color.HSVToColor(hsv));
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
                    sp = context.getResources().getString(R.string.sp_radio_lastAMfreq);
                } else {
                    sp = context.getResources().getString(R.string.sp_radio_lastFMfreq);
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

        editor.commit();
    }

    private void getMCUValues(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (settingManager != null) {
            // Звук
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_volume), settingManager.getMcuVol());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_balance), settingManager.getBalance());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_fade), settingManager.getFade());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_bass), settingManager.getBass());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_middle), settingManager.getMiddle());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_treble), settingManager.getTreble());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_subwoofer), settingManager.getSubwoofer());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putInt(context.getResources().getString(R.string.sp_sound_equalizer_preset), settingManager.getEQ());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getResources().getString(R.string.sp_sound_loud), settingManager.getLound());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Радио
            try {
                editor.putString(context.getResources().getString(R.string.sp_radio_region), String.valueOf(settingManager.getRadioField()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Экран
            /*try {
                editor.putInt(context.getResources().getString(R.string.sp_screen_brightness), settingManager.getScreenBrightness());
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            try {
                editor.putInt(context.getResources().getString(R.string.sp_screen_contrast), settingManager.getContrast());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                editor.putBoolean(context.getResources().getString(R.string.sp_screen_detect_illumination), settingManager.getIllumeDetection());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                float h = (float)settingManager.getHueSetting() / 127 * 360;
                float s = (float)settingManager.getSaturation() / 127;
                float v = (float)settingManager.getBright() / 127;
                float[] hsv = {h, s, v};
                editor.putInt(context.getResources().getString(R.string.sp_screen_illumination_color), Color.HSVToColor(hsv));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (radioManager != null) {
            try {
                int currBand = radioManager.getBand();
                if ((currBand == IRadioManager.IRadioConstant.BAND_AM_1) || (currBand == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    editor.putInt(context.getResources().getString(R.string.sp_radio_lastAMfreq), radioManager.getCurrFreq());
                } else {
                    editor.putInt(context.getResources().getString(R.string.sp_radio_lastFMfreq), radioManager.getCurrFreq());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            /*try {
                char[] freqs = radioManager.getFreqList();
                for (int i = 0; i < freqs.length; i++) {
                    editor.putInt(String.format(context.getResources().getString(R.string.sp_radio_presets), i + 1), (int)freqs[i]);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
        }

        editor.commit();
    }

    private void volumeChange(Context context) {
        if (settingManager == null) { return; }

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            int mVolume = settingManager.getMcuVol();
            editor.putInt(context.getResources().getString(R.string.sp_sound_volume), mVolume);
            AutoSettingsActivity.SoundPreferenceFragment fragment = AutoSettingsActivity.SoundPreferenceFragment.getInstance();
            if (fragment != null) {
                fragment.updateVolume(mVolume);
            }
        } catch (RemoteException e) { e.printStackTrace(); }
        editor.commit();
    }
}
