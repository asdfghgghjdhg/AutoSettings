package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class SpeedPreference extends DialogPreference implements NumberPicker.OnValueChangeListener {

    private static final int DEFAULT_VALUE = 0;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 300;
    private static final int DEFAULT_STEP_VALUE = 10;

    private final int mSpeedMinValue;
    private final int mSpeedMaxValue;
    private final int mSpeedStepValue;

    private int speed = 0;

    public SpeedPreference(Context context) {
        super(context);

        mSpeedMinValue = DEFAULT_MIN_VALUE;
        mSpeedMaxValue = DEFAULT_MAX_VALUE;
        mSpeedStepValue = DEFAULT_STEP_VALUE;

        setWidgetLayoutResource(R.layout.speed_preference_widget);
        setDialogLayoutResource(R.layout.speed_preference_dialog);
    }

    public SpeedPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPreference);
        mSpeedMinValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedMinValue, DEFAULT_MIN_VALUE);
        mSpeedMaxValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedMaxValue, DEFAULT_MAX_VALUE);
        mSpeedStepValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedStepValue, DEFAULT_STEP_VALUE);
        a.recycle();

        setWidgetLayoutResource(R.layout.speed_preference_widget);
        setDialogLayoutResource(R.layout.speed_preference_dialog);
    }

    public SpeedPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPreference);
        mSpeedMinValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedMinValue, DEFAULT_MIN_VALUE);
        mSpeedMaxValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedMaxValue, DEFAULT_MAX_VALUE);
        mSpeedStepValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedStepValue, DEFAULT_STEP_VALUE);
        a.recycle();

        setWidgetLayoutResource(R.layout.speed_preference_widget);
        setDialogLayoutResource(R.layout.speed_preference_dialog);
    }

    public SpeedPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPreference);
        mSpeedMinValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedMinValue, DEFAULT_MIN_VALUE);
        mSpeedMaxValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedMaxValue, DEFAULT_MAX_VALUE);
        mSpeedStepValue = a.getInt(R.styleable.com_dgl_auto_autosettings_SpeedPreferences_speedStepValue, DEFAULT_STEP_VALUE);
        a.recycle();

        setWidgetLayoutResource(R.layout.speed_preference_widget);
        setDialogLayoutResource(R.layout.speed_preference_dialog);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final View view = super.onCreateView(parent);

        view.setPadding(view.getPaddingLeft() + 15, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView speedText = view.findViewById(R.id.textView);
        if (speedText != null) {
            speedText.setText(String.format(getContext().getString(R.string.speed_preference_widget_text), speed));
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            speed = getPersistedInt(DEFAULT_VALUE);
        } else {
            speed = (Integer)defaultValue;
            persistInt(speed);
        }
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        NumberPicker picker = view.findViewById(R.id.speedPicker);

        ArrayList<String> values = new ArrayList<>();
        int i = mSpeedMinValue;
        while (i <= mSpeedMaxValue) {
            values.add(String.valueOf(i));
            i = i + mSpeedStepValue;
        }
        picker.setMinValue(0);
        picker.setMaxValue(values.size() - 1);
        picker.setDisplayedValues(values.toArray(new String[values.size()]));
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(this);

        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        NumberPicker picker = view.findViewById(R.id.speedPicker);

        String[] values = picker.getDisplayedValues();
        int pos = 0;
        while (pos < values.length) {
            if (values[pos].equals(String.valueOf(speed))) { break; }
            pos++;
        }
        if (pos >= values.length) { pos = 0; }
        picker.setValue(pos);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            persistInt(speed);
            notifyChanged();
            callChangeListener(speed);
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        try { speed = Integer.valueOf(numberPicker.getDisplayedValues()[newVal]); } finally { }
    }
}
