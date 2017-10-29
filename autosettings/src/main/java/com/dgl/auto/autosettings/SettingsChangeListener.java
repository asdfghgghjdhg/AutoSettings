package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.RemoteException;
import android.util.Log;

import com.dgl.auto.ISettingManager;
import com.dgl.auto.SettingManager;

public class SettingsChangeListener implements ISettingManager.IDataChange {

    private Context mContext;

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

        editor.apply();
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
                editor.putBoolean(mContext.getResources().getString(R.string.sp_general_beep), sm.getBeep());
                editor.putString(mContext.getResources().getString(R.string.sp_general_boottime), String.valueOf(sm.getBootTime()));
                editor.putBoolean(mContext.getResources().getString(R.string.sp_general_playvideo), sm.getCanWatchVideoWhileDriver());
                editor.putBoolean(mContext.getResources().getString(R.string.sp_general_shortcut_touch_state), sm.getShortcutTouchState());
                editor.putBoolean(mContext.getResources().getString(R.string.sp_general_switch_media_status), sm.GetSwitchMediaStatus());
                editor.putBoolean(mContext.getResources().getString(R.string.sp_general_rearview_camera), sm.getReverseAuxLine());
                editor.putBoolean(mContext.getResources().getString(R.string.sp_general_mirror_rearview), sm.getReverseMirror());
                editor.putString(mContext.getResources().getString(R.string.sp_general_swctype), String.valueOf(sm.getSWCTypeValue()));
                editor.putString(mContext.getResources().getString(R.string.sp_general_usb0type), String.valueOf(sm.getUSB0TypeValue()));
                editor.putString(mContext.getResources().getString(R.string.sp_general_usb1type), String.valueOf(sm.getUSB1TypeValue()));

                editor.putString(mContext.getResources().getString(R.string.sp_radio_region), String.valueOf(sm.getRadioField()));

                //editor.putInt(mContext.getResources().getString(R.string.sp_screen_brightness), sm.getScreenBrightness());
                editor.putInt(mContext.getResources().getString(R.string.sp_screen_contrast), sm.getContrast());
                editor.putBoolean(mContext.getResources().getString(R.string.sp_screen_detect_illumination), sm.getIllumeDetection());

                float h = (float)sm.getHueSetting() / 127 * 360;
                float s = (float)sm.getSaturation() / 127;
                float v = (float)sm.getBright() / 127;
                float[] hsv = {h, s, v};
                editor.putInt(mContext.getResources().getString(R.string.sp_screen_illumination_color), Color.HSVToColor(hsv));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        editor.apply();

        return 0;
    }

    @Override
    public int onUpdateMcuVersion() {
        Log.i("SettingsChangeListener", "onUpdateMcuVersion");

        return 0;
    }
}
