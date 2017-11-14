package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dgl.auto.ISettingManager;
import com.dgl.auto.mcumanager.MCUManager;

public class SettingsChangeListener implements ISettingManager.IDataChange {

    private static final String LOG_TAG = "SettingsChangeListener";
    private Context mContext;

    public boolean skipAudioSettingsChange;
    public boolean skipGeneralSettingsChange;

    SettingsChangeListener(Context context) {
        skipAudioSettingsChange = false;
        skipGeneralSettingsChange = false;
        mContext = context;
    }

    @Override
    public int onAudioSettingChange() {
        if (skipAudioSettingsChange) {
            skipAudioSettingsChange = false;
            return 0;
        }

        Log.i(LOG_TAG, "onAudioSettingsChange");

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(mContext.getString(R.string.sp_sound_balance), MCUManager.VolumeControl.getBalance());
            editor.putInt(mContext.getString(R.string.sp_sound_fade), MCUManager.VolumeControl.getFade());
            editor.putBoolean(mContext.getString(R.string.sp_sound_loud), MCUManager.EqualizerControl.getLoudMode());

            MCUManager.EqualizerControl.EqualizerPreset preset = MCUManager.EqualizerControl.getPreset();
            editor.putString(mContext.getString(R.string.sp_sound_eq_preset), mContext.getResources().getStringArray(R.array.mcu_equalizer_presets_values)[preset.getMCUIndex()]);
            editor.putInt(mContext.getString(R.string.sp_sound_eq_bass), MCUManager.EqualizerControl.getBass());
            editor.putInt(mContext.getString(R.string.sp_sound_eq_middle), MCUManager.EqualizerControl.getMiddle());
            editor.putInt(mContext.getString(R.string.sp_sound_eq_treble), MCUManager.EqualizerControl.getTreble());
            editor.putInt(mContext.getString(R.string.sp_sound_eq_subwoofer), MCUManager.EqualizerControl.getSubwoofer());

            editor.apply();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int onGeneralSettingChange() {
        if (skipGeneralSettingsChange) {
            skipGeneralSettingsChange = false;
            return 0;
        }

        Log.i(LOG_TAG, "onGeneralSettingsChange");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            editor.putString(mContext.getString(R.string.sp_general_boottime), mContext.getResources().getStringArray(R.array.mcu_boottime_values)[MCUManager.getBootTime()]);
            editor.putBoolean(mContext.getString(R.string.sp_general_shortcut_touch_state), MCUManager.getShortcutTouchState());
            editor.putBoolean(mContext.getString(R.string.sp_general_switch_media_status), MCUManager.MultimediaControl.getSwitchMediaStatus());editor.putBoolean(mContext.getString(R.string.sp_general_playvideo), MCUManager.MultimediaControl.getPlayVideoWhileDriving());

            int regionIndex = MCUManager.RadioControl.getRegionIndex();
            editor.putString(mContext.getString(R.string.sp_radio_region), mContext.getResources().getStringArray(R.array.mcu_radio_regions_values)[regionIndex]);

            editor.putInt(mContext.getString(R.string.sp_screen_contrast), MCUManager.ScreenControl.getContrast());
            editor.putBoolean(mContext.getString(R.string.sp_screen_detect_illumination), MCUManager.ScreenControl.getDetectIllumination());
            editor.putInt(mContext.getString(R.string.sp_screen_illumination_color), MCUManager.ScreenControl.getIlluminationColor());

            editor.putBoolean(mContext.getString(R.string.sp_rearview_addlines), MCUManager.RearViewCamera.getAddParkingLines());
            editor.putBoolean(mContext.getString(R.string.sp_rearview_mirror_view), MCUManager.RearViewCamera.getMirrorView());

            int swcTypeIndex;
            switch (MCUManager.SWCControl.getSWCType()) {
                case TYPE_1: swcTypeIndex = 0; break;
                case TYPE_2: swcTypeIndex = 1; break;
                default: swcTypeIndex = 0;
            }
            editor.putString(mContext.getString(R.string.sp_swc_swctype), mContext.getResources().getStringArray(R.array.mcu_swc_type_values)[swcTypeIndex]);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        editor.apply();

        return 0;
    }

    @Override
    public int onUpdateMcuVersion() {
        Log.i(LOG_TAG, "onUpdateMcuVersion");

        return 0;
    }
}
