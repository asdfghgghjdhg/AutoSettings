package com.dgl.auto.autosettings;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PaddedSwitchPreference extends SwitchPreference {

    public PaddedSwitchPreference(Context context) {
        super(context);
    }

    public PaddedSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaddedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PaddedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final View layout = super.onCreateView(parent);

        layout.setPadding(layout.getPaddingLeft() + 15, layout.getPaddingTop(), layout.getPaddingRight(), layout.getPaddingBottom());
        return layout;
    }
}
