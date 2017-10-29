package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.RadioManager;

public class RadioChangeListener implements IRadioManager.IDataChange {

    protected Context mContext;

    public static boolean updateInfo = true;

    RadioChangeListener(Context context) {
        mContext = context;
    }

    @Override
    public int onTunerInfoChange() {
        Log.i("RadioChangeListener", "onTunerInfoChange");

        if (!updateInfo) { return 0; }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        IRadioManager rm = RadioManager.getInstance();
        if (rm != null) {
            try {
                if (!rm.getSeekStatus() && !rm.getScanSatus()) {
                    int currBand = rm.getBand();
                    if ((currBand == IRadioManager.IRadioConstant.BAND_AM_1) || (currBand == IRadioManager.IRadioConstant.BAND_AM_2)) {
                        editor.putInt(mContext.getResources().getString(R.string.sp_radio_lastAMfreq), rm.getCurrFreq());
                    } else {
                        editor.putInt(mContext.getResources().getString(R.string.sp_radio_lastFMfreq), rm.getCurrFreq());
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        editor.commit();
        return 0;
    }

    @Override
    public int onTunerPresetList() {
        Log.i("RadioChangeListener", "onTunerPresetList");

        /*SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        IRadioManager rm = RadioManager.getInstance();
        if (rm != null) {
            try {
                char[] freqs = rm.getFreqList();
                for (int i = 0; i < freqs.length; i++) {
                    editor.putInt(String.format(mContext.getResources().getString(R.string.sp_radio_presets), i + 1), (int)freqs[i]);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        editor.commit();*/
        return 0;
    }

    @Override
    public int onTunerRangeChange() {
        Log.i("RadioChangeListener", "onTunerRangeChange");

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        IRadioManager rm = RadioManager.getInstance();
        if (rm != null) {
            try {
                int currBand = rm.getBand();
                int freq;
                if ((currBand == IRadioManager.IRadioConstant.BAND_AM_1) || (currBand == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    freq = sharedPreferences.getInt(mContext.getResources().getString(R.string.sp_radio_lastAMfreq), IRadioManager.IRadioConstant.RADIO_AM_DEFUALT_FREQ);
                    if ((freq < rm.getMinAMFreq()) || (freq > rm.getMaxAMFreq())) {
                        freq = IRadioManager.IRadioConstant.RADIO_AM_DEFUALT_FREQ;
                    }
                } else {
                    freq = sharedPreferences.getInt(mContext.getResources().getString(R.string.sp_radio_lastFMfreq), IRadioManager.IRadioConstant.RADIO_FM_DEFUALT_FREQ);
                    if ((freq < rm.getMinFMFreq()) || (freq > rm.getMaxFMFreq())) {
                        freq = IRadioManager.IRadioConstant.RADIO_FM_DEFUALT_FREQ;
                    }
                }
                rm.setFreq((char) freq);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        AutoSettingsActivity.RadioPreferenceFragment fragment = AutoSettingsActivity.RadioPreferenceFragment.getInstance();
        fragment.updateRegionInfo();

        return 0;
    }

    @Override
    public int onTunerReady() {
        Log.i("RadioChangeListener", "onTunerReady");

        return 0;
    }

    @Override
    public int onTunerUpdateRdsInfo() {
        Log.i("RadioChangeListener", "onTunerUpdateRdsInfo");

        return 0;
    }
}
