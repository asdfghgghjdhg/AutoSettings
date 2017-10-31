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

    public static String ENABLE_LOCATION_LISTENER = "EnableLocationListener";

    private ISettingManager settingManager;
    private IRadioManager radioManager;
    private ISettingManager.IDataChange mSettingsChangeListener;
    private IRadioManager.IDataChange mRadioChangeListener;
    private SpeedLocationListener mSpeedListener;

    private boolean speedListenerActive;

    public void onCreate() {
        super.onCreate();
        speedListenerActive = false;
        Log.i("AutoSettingsService", "onCreate");
    }

    public void onDestroy() {
        if ((mSpeedListener != null) && speedListenerActive) {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) { locationManager.removeUpdates(mSpeedListener); }
        }
        speedListenerActive = false;
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

        if (mSpeedListener == null) {
            mSpeedListener = new SpeedLocationListener(this);
        }

        boolean startLocationListener;
        if (intent != null) {
            startLocationListener = intent.getBooleanExtra(ENABLE_LOCATION_LISTENER, false);
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            startLocationListener = sharedPreferences.getBoolean(getString(R.string.sp_sound_speed_compensation), false);
        }

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (startLocationListener) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (!speedListenerActive) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mSpeedListener);
                        speedListenerActive = true;
                    }
                }
            } else {
                if (speedListenerActive) {
                    locationManager.removeUpdates(mSpeedListener);
                    speedListenerActive = false;
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
