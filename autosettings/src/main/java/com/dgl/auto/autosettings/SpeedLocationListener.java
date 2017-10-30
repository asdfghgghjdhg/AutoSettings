package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
        // TODO: Обработать изменение скорости
        float currSpeed = location.getSpeed() * (float)3.6;
        Log.i("SpeedLocationListener", String.valueOf(location.getSpeed()));


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
