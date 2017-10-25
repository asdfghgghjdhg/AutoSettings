package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;
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

public class SettingsChangeListener implements ISettingManager.IDataChange {

    protected Context mContext;

    SettingsChangeListener(Context context) {
        mContext = context;
    }

    @Override
    public int onAudioSettingChange() {
        Log.i("SettingsChangeListener", "onAudioSettingsChange");

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            try {
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_volume), sm.getMcuVol());
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_balance), sm.getBalance());
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_fade), sm.getFade());
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_equalizer_bass), sm.getBass());
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_equalizer_middle), sm.getMiddle());
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_equalizer_treble), sm.getTreble());
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_equalizer_subwoofer), sm.getSubwoofer());
                editor.putInt(mContext.getResources().getString(R.string.sp_sound_equalizer_preset), sm.getEQ());
                editor.putBoolean(mContext.getResources().getString(R.string.sp_sound_loud), sm.getLound());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        editor.commit();
        return 0;
    }

    @Override
    public int onGeneralSettingChange() {
        Log.i("SettingsChangeListener", "onGeneralSettingsChange");

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            try {
                editor.putString(mContext.getResources().getString(R.string.sp_radio_region), String.valueOf(sm.getRadioField()));

                editor.putInt(mContext.getResources().getString(R.string.sp_screen_brightness), sm.getScreenBrightness());
                editor.putInt(mContext.getResources().getString(R.string.sp_screen_contrast), sm.getContrast());
                editor.putBoolean(mContext.getResources().getString(R.string.sp_screen_detect_illumination), sm.getIllumeDetection());
                editor.putInt(mContext.getResources().getString(R.string.sp_screen_hue), sm.getHueSetting());
                editor.putInt(mContext.getResources().getString(R.string.sp_screen_saturation), sm.getSaturation());
                editor.putInt(mContext.getResources().getString(R.string.sp_screen_value), sm.getBright());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        editor.commit();

        return 0;
    }

    @Override
    public int onUpdateMcuVersion() {
        Log.i("SettingsChangeListener", "onUpdateMcuVersion");

        return 0;
    }
}
