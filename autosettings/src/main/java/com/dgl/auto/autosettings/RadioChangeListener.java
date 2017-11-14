package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.mcumanager.MCUManager;

public class RadioChangeListener implements IRadioManager.IDataChange {
    private static final String LOG_TAG = "RadioChangeListener";
    private Context mContext;

    public boolean skipTunerInfoChange;

    RadioChangeListener(Context context) {
        skipTunerInfoChange = false;
        mContext = context;
    }

    @Override
    public int onTunerInfoChange() {
        if (skipTunerInfoChange) {
            skipTunerInfoChange = false;
            return 0;
        }

        Log.i(LOG_TAG, "onTunerInfoChange");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            int freq = MCUManager.RadioControl.getFrequency();
            String pref;
            if (MCUManager.RadioControl.getBand() == MCUManager.RadioControl.BAND_AM) {
                pref = mContext.getString(R.string.sp_radio_lastAMfreq);
            } else {
                pref = mContext.getString(R.string.sp_radio_lastFMfreq);
            }
            editor.putInt(pref, freq);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        editor.apply();
        return 0;
    }

    @Override
    public int onTunerPresetList() {
        Log.i(LOG_TAG, "onTunerPresetList");

        return 0;
    }

    @Override
    public int onTunerRangeChange() {
        Log.i(LOG_TAG, "onTunerRangeChange");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        try {
            String pref;
            if (MCUManager.RadioControl.getBand() == MCUManager.RadioControl.BAND_AM) {
                pref = mContext.getString(R.string.sp_radio_lastAMfreq);
            } else {
                pref = mContext.getString(R.string.sp_radio_lastFMfreq);
            }
            if (sharedPreferences.contains(pref)) {
                int freq = sharedPreferences.getInt(pref, 0);
                MCUManager.RadioControl.setFrequency(freq);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

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
