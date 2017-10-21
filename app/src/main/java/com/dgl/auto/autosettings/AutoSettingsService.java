package com.dgl.auto.autosettings;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.ISettingManager;
import com.dgl.auto.RadioManager;
import com.dgl.auto.SettingManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class AutoSettingsService extends Service {

    private ISettingManager settingManager;
    private IRadioManager radioManager;
    private ISettingManager.IDataChange mSettingsChangeListener;
    private IRadioManager.IDataChange mRadioChangeListener;

    public void onCreate() {
        super.onCreate();
        Log.i("AutoSettingsService", "onCreate");
    }

    public void onDestroy() {
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

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
