package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class AppPreference extends ListPreference {

    public AppPreference(Context context) {
        super(context);

        setWidgetLayoutResource(R.layout.app_preference_widget);
    }

    public AppPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWidgetLayoutResource(R.layout.app_preference_widget);
    }

    public AppPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWidgetLayoutResource(R.layout.app_preference_widget);
    }

    public AppPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setWidgetLayoutResource(R.layout.app_preference_widget);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView appTitle = view.findViewById(R.id.appTitle);
        ImageView appIcon = view.findViewById(R.id.appIcon);
        if ((appTitle != null) & (appIcon != null)) {
            if (getEntry() == null) {
                appTitle.setVisibility(View.INVISIBLE);
                appIcon.setVisibility(View.INVISIBLE);
            } else {
                appTitle.setVisibility(View.VISIBLE);
                appIcon.setVisibility(View.VISIBLE);
                appTitle.setText(getEntry());

                try {
                    Drawable icon = getContext().getPackageManager().getApplicationIcon(getValue());
                    if (icon != null) {
                        appIcon.setImageDrawable(icon);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
