package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.RadioManager;

public class RadioChangeListener implements IRadioManager.IDataChange {
    private static final String LOG_TAG = "RadioChangeListener";
    private Context mContext;

    //private static boolean updateInfo = true;

    RadioChangeListener(Context context) {
        mContext = context;
    }

    @Override
    public int onTunerInfoChange() {
        Log.i(LOG_TAG, "onTunerInfoChange");

        //if (!updateInfo) { return 0; }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        IRadioManager rm = RadioManager.getInstance();
        if (rm != null) {
            try {
                if (!rm.getSeekStatus() && !rm.getScanSatus()) {
                    int currBand = rm.getBand();
                    if ((currBand == IRadioManager.IRadioConstant.BAND_AM_1) || (currBand == IRadioManager.IRadioConstant.BAND_AM_2)) {
                        editor.putInt(mContext.getString(R.string.sp_radio_lastAMfreq), rm.getCurrFreq());
                    } else {
                        editor.putInt(mContext.getString(R.string.sp_radio_lastFMfreq), rm.getCurrFreq());
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        editor.apply();
        return 0;
    }

    @Override
    public int onTunerPresetList() {
        Log.i(LOG_TAG, "onTunerPresetList");

        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        IRadioManager rm = RadioManager.getInstance();
        if (rm != null) {
            try {
                char[] freqs = rm.getFreqList();
                for (int i = 0; i < freqs.length; i++) {
                    editor.putInt(String.format(mContext.getString(R.string.sp_radio_presets), i + 1), (int)freqs[i]);
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
        Log.i(LOG_TAG, "onTunerRangeChange");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        IRadioManager rm = RadioManager.getInstance();
        if (rm != null) {
            try {
                int currBand = rm.getBand();
                int freq;
                if ((currBand == IRadioManager.IRadioConstant.BAND_AM_1) || (currBand == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    freq = sharedPreferences.getInt(mContext.getString(R.string.sp_radio_lastAMfreq), IRadioManager.IRadioConstant.RADIO_AM_DEFUALT_FREQ);
                    if ((freq < rm.getMinAMFreq()) || (freq > rm.getMaxAMFreq())) {
                        freq = IRadioManager.IRadioConstant.RADIO_AM_DEFUALT_FREQ;
                    }
                } else {
                    freq = sharedPreferences.getInt(mContext.getString(R.string.sp_radio_lastFMfreq), IRadioManager.IRadioConstant.RADIO_FM_DEFUALT_FREQ);
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
        Log.i(LOG_TAG, "onTunerReady");

        return 0;
    }

    @Override
    public int onTunerUpdateRdsInfo() {
        Log.i(LOG_TAG, "onTunerUpdateRdsInfo");

        return 0;
    }
}
