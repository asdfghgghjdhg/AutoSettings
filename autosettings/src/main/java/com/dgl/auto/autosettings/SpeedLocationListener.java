package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dgl.auto.ISettingManager;
import com.dgl.auto.SettingManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpeedLocationListener implements LocationListener, GpsStatus.Listener {
    private static final String LOG_TAG = "SpeedLocationListener";
    private Context mContext;
    private ISettingManager settingManager;
    private ExecutorService es;

    private float prevSpeed;
    private float volumeChangeValue;
    private boolean stopVolumeChange;

    private class VolumeChangeRunnable implements Runnable {

        @Override
        public void run() {
            while (!stopVolumeChange) {
                int dVolume = 0;
                if (volumeChangeValue > 1) { dVolume = 1; }
                if (volumeChangeValue < -1) { dVolume = -1; }

                if (dVolume != 0) {
                    if (settingManager != null) {
                        try {
                            int currVol = settingManager.getMcuVol();
                            currVol = currVol + dVolume;
                            settingManager.setMcuVol(currVol);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    volumeChangeValue = volumeChangeValue + dVolume;
                }

                try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    SpeedLocationListener(Context context) {
        mContext = context;
        settingManager = SettingManager.getInstance();
        es = Executors.newFixedThreadPool(1);

        prevSpeed = 0;
        volumeChangeValue = 0;
        stopVolumeChange = false;

        es.execute(new VolumeChangeRunnable());
    }

    public void stopVolumeChange() {
        stopVolumeChange = true;
        es.shutdown();
    }

    @Override
    public void onGpsStatusChanged(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO: Доработать!
        float currSpeed = location.getSpeed() * (float)3.6;
        Log.i(LOG_TAG, "Speed:" + String.valueOf(currSpeed));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int minSpeed = sharedPreferences.getInt(mContext.getResources().getString(R.string.sp_sound_min_speed), 0);

        float ds = 0;
        if (currSpeed > minSpeed) {
            if (prevSpeed < minSpeed) { ds = currSpeed - minSpeed; } else { ds = currSpeed - prevSpeed; }
        } else {
            if (prevSpeed > minSpeed) { ds = prevSpeed - minSpeed; }
        }

        if (ds != 0) {
            float dv = ds * 40 / 300;
            Log.i(LOG_TAG, "dv:" + String.valueOf(dv));
            volumeChangeValue = volumeChangeValue + dv;
            Log.i(LOG_TAG, "Volume Change:" + String.valueOf(volumeChangeValue));

            /*int volume;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                try {
                    volume = sm.getMcuVol();
                    volume = volume + Math.round(dv);
                    //Log.i(LOG_TAG, "volume:" + String.valueOf(volume));
                    sm.setMcuVol(volume);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }*/


        }

        if (currSpeed != prevSpeed) { prevSpeed = currSpeed; }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
