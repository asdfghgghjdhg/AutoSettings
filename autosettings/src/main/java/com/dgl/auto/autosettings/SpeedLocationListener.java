package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.dgl.auto.ISettingManager;
import com.dgl.auto.SettingManager;

public class SpeedLocationListener implements LocationListener, GpsStatus.Listener {
    private Context mContext;
    private float prevSpeed = 0;

    SpeedLocationListener(Context context) {
        mContext = context;
    }

    @Override
    public void onGpsStatusChanged(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO: Доработать!
        float currSpeed = location.getSpeed() * (float)3.6;
        //Log.i("SpeedLocationListener", "Speed:" + String.valueOf(currSpeed));

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        int minSpeed = sharedPreferences.getInt(mContext.getResources().getString(R.string.sp_sound_min_speed), 0);
        if ((currSpeed > minSpeed) || ((currSpeed < prevSpeed) & (prevSpeed > minSpeed))) {
            float ds = currSpeed - prevSpeed;
            float dv = ds * 40 / 300;
            //Log.i("SpeedLocationListener", "dv:" + String.valueOf(dv));
            if ((dv > -1) & (dv < 1)) { return; }

            int volume = 0;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                try {
                    volume = sm.getMcuVol();
                    volume = volume + Math.round(dv);
                    //Log.i("SpeedLocationListener", "volume:" + String.valueOf(volume));
                    sm.setMcuVol(volume);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }


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
