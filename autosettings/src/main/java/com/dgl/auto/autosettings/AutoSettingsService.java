package com.dgl.auto.autosettings;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.ISettingManager;
import com.dgl.auto.RadioManager;
import com.dgl.auto.SettingManager;

public class AutoSettingsService extends Service {

    private ISettingManager settingManager;
    private IRadioManager radioManager;
    private ISettingManager.IDataChange mSettingsChangeListener;
    private IRadioManager.IDataChange mRadioChangeListener;
    private SpeedLocationListener mSpeedListener;

    public void onCreate() {
        super.onCreate();
        Log.i("AutoSettingsService", "onCreate");
        settingManager = null;
        radioManager = null;
        mSpeedListener = null;
        mSettingsChangeListener = null;
        mRadioChangeListener = null;
    }

    public void onDestroy() {
        if (mSpeedListener != null) {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) { locationManager.removeUpdates(mSpeedListener); }
            mSpeedListener.stop();
        }
        settingManager.setDataChangeListener(null);
        radioManager.setDataChangeListener(null);
        settingManager = null;
        radioManager = null;
        mSettingsChangeListener = null;
        mRadioChangeListener = null;
        mSpeedListener = null;
        Log.i("AutoSettingsService", "onDestroy");
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AutoSettingsService", "onStartCommand");

        if (settingManager == null) {
            settingManager = SettingManager.getInstance();
            settingManager.init(this);
            if (mSettingsChangeListener == null) {
                mSettingsChangeListener = new SettingsChangeListener(this);
            }
            settingManager.setDataChangeListener(mSettingsChangeListener);
        }

        if (radioManager == null) {
            radioManager = RadioManager.getInstance();
            radioManager.init(this);
            if (mRadioChangeListener == null) {
                mRadioChangeListener = new RadioChangeListener(this);
            }
            radioManager.setDataChangeListener(mRadioChangeListener);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            boolean startLocationListener;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            startLocationListener = sharedPreferences.getBoolean(getString(R.string.sp_sound_speed_compensation), false);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                if (startLocationListener) {
                    if (mSpeedListener == null) {
                        mSpeedListener = new SpeedLocationListener(this);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mSpeedListener);
                    }
                } else {
                    if (mSpeedListener != null) {
                        locationManager.removeUpdates(mSpeedListener);
                        mSpeedListener.stop();
                        mSpeedListener = null;
                    }
                }
            }

        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
