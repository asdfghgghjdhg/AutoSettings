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
    public static final int DEFAULT_MIN_SPEED = 40;
    public static final int DEFAULT_MAX_SPEED = 300;

    private static final String LOG_TAG = "SpeedLocationListener";
    private static final int VOLUME_CHANGE_TIMEOUT = 750;
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
                            if (currVol < 0) { currVol = 0; }
                            if (currVol > 40) { currVol = 40; }
                            settingManager.setMcuVol(currVol);
                            Log.i(LOG_TAG, "Current Volume:" + String.valueOf(currVol));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    volumeChangeValue = volumeChangeValue - dVolume;
                }

                //Log.i(LOG_TAG, "Volume Change:" + String.valueOf(volumeChangeValue));
                try { Thread.sleep(VOLUME_CHANGE_TIMEOUT); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    SpeedLocationListener(Context context) {
        mContext = context;
        settingManager = SettingManager.getInstance();
        es = Executors.newFixedThreadPool(1);

        prevSpeed = -1;
        volumeChangeValue = 0;
        stopVolumeChange = false;

        es.execute(new VolumeChangeRunnable());
    }

    public void stop() {
        stopVolumeChange = true;
        es.shutdown();
        prevSpeed = -1;
        volumeChangeValue = 0;
    }

    @Override
    public void onGpsStatusChanged(int i) {
        // TODO: При потере сигнала?
    }

    @Override
    public void onLocationChanged(Location location) {
        float currSpeed = location.getSpeed() * (float)3.6;
        //Log.i(LOG_TAG, "Speed:" + String.valueOf(currSpeed));

        if (prevSpeed >= 0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            int minSpeed = sharedPreferences.getInt(mContext.getString(R.string.sp_sound_min_speed), DEFAULT_MIN_SPEED);
            int maxSpeed = sharedPreferences.getInt(mContext.getString(R.string.sp_sound_max_speed), DEFAULT_MAX_SPEED);

            float ds = 0;
            if (currSpeed > minSpeed) {
                if (prevSpeed < minSpeed) { ds = currSpeed - minSpeed; } else { ds = currSpeed - prevSpeed; }
            } else {
                if (prevSpeed > minSpeed) { ds = minSpeed - prevSpeed; }
            }

            if (ds != 0) {
                float dv = ds * 40 / maxSpeed;
                //Log.i(LOG_TAG, "dv:" + String.valueOf(dv));
                volumeChangeValue = volumeChangeValue + dv;
                //Log.i(LOG_TAG, "Volume Change:" + String.valueOf(volumeChangeValue));
            }
        }

        if (currSpeed != prevSpeed) { prevSpeed = currSpeed; }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // TODO: При потере сигнала?
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
