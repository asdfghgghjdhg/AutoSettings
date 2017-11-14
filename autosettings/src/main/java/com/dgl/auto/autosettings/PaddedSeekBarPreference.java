package com.dgl.auto.autosettings;

import android.content.Context;
import android.preference.SeekBarPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PaddedSeekBarPreference extends SeekBarPreference {
    public PaddedSeekBarPreference(Context context) {
        super(context);
    }

    public PaddedSeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaddedSeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PaddedSeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final View layout = super.onCreateView(parent);

        layout.setPadding(layout.getPaddingLeft() - 15, layout.getPaddingTop(), layout.getPaddingRight(), layout.getPaddingBottom());
        return layout;
    }
}
