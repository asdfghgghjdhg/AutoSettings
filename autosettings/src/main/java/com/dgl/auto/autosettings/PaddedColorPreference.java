package com.dgl.auto.autosettings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PaddedColorPreference extends com.jaredrummler.android.colorpicker.ColorPreference {
    public PaddedColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaddedColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final View layout = super.onCreateView(parent);

        layout.setPadding(layout.getPaddingLeft() + 15, layout.getPaddingTop(), layout.getPaddingRight(), layout.getPaddingBottom());
        return layout;
    }
}
