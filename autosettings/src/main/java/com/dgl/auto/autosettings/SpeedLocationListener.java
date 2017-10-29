package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class SpeedLocationListener implements LocationListener, GpsStatus.Listener {
    private Context mContext;

    SpeedLocationListener(Context context) {
        mContext = context;
    }

    @Override
    public void onGpsStatusChanged(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.dgl.auto.autosettings_preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(mContext.getResources().getString(R.string.sp_sound_speed_compensation), false)) {
            // TODO: Обработать изменение скорости
        }
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
