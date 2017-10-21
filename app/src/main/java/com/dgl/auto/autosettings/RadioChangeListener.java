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

    RadioChangeListener(Context context) {
        mContext = context;
    }

    @Override
    public int onTunerInfoChange() {
        Log.i("RadioChangeListener", "onTunerInfoChange");

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        IRadioManager rm = RadioManager.getInstance();
        if (rm != null) {
            try {
                if (!rm.getSeekStatus() && !rm.getScanSatus()) {
                    editor.putInt(mContext.getResources().getString(R.string.sp_radio_currband), rm.getBand());
                    editor.putInt(mContext.getResources().getString(R.string.sp_radio_currfreq), rm.getCurrFreq());
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

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
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

        editor.commit();
        return 0;
    }

    @Override
    public int onTunerRangeChange() {
        Log.i("RadioChangeListener", "onTunerRangeChange");

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
