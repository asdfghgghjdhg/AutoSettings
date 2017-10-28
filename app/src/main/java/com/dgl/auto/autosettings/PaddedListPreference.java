package com.dgl.auto.autosettings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PaddedListPreference extends ListPreference {
    public PaddedListPreference(Context context) {
        super(context);
    }

    public PaddedListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaddedListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PaddedListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final View layout = super.onCreateView(parent);

        layout.setPadding(layout.getPaddingLeft() + 15, layout.getPaddingTop(), layout.getPaddingRight(), layout.getPaddingBottom());
        return layout;
    }
}
