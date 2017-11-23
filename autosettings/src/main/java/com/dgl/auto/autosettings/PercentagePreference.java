package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class PercentagePreference extends DialogPreference implements NumberPicker.OnValueChangeListener {

    private static final int DEFAULT_VALUE = 100;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_STEP_VALUE = 10;

    private final int mMinValue;
    private final int mMaxValue;
    private final int mStepValue;

    private int percentage = 0;

    public PercentagePreference(Context context) {
        super(context);

        mMinValue = DEFAULT_MIN_VALUE;
        mMaxValue = DEFAULT_MAX_VALUE;
        mStepValue = DEFAULT_STEP_VALUE;

        setWidgetLayoutResource(R.layout.percentage_preference_widget);
        setDialogLayoutResource(R.layout.percentage_preference_dialog);
    }

    public PercentagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.com_dgl_auto_autosettings_PercentagePreferences);
        mMinValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_minValue, DEFAULT_MIN_VALUE);
        mMaxValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_maxValue, DEFAULT_MAX_VALUE);
        mStepValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_stepValue, DEFAULT_STEP_VALUE);
        a.recycle();

        setWidgetLayoutResource(R.layout.percentage_preference_widget);
        setDialogLayoutResource(R.layout.percentage_preference_dialog);
    }

    public PercentagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.com_dgl_auto_autosettings_PercentagePreferences);
        mMinValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_minValue, DEFAULT_MIN_VALUE);
        mMaxValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_maxValue, DEFAULT_MAX_VALUE);
        mStepValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_stepValue, DEFAULT_STEP_VALUE);
        a.recycle();

        setWidgetLayoutResource(R.layout.percentage_preference_widget);
        setDialogLayoutResource(R.layout.percentage_preference_dialog);
    }

    public PercentagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.com_dgl_auto_autosettings_PercentagePreferences);
        mMinValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_minValue, DEFAULT_MIN_VALUE);
        mMaxValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_maxValue, DEFAULT_MAX_VALUE);
        mStepValue = a.getInt(R.styleable.com_dgl_auto_autosettings_PercentagePreferences_stepValue, DEFAULT_STEP_VALUE);
        a.recycle();

        setWidgetLayoutResource(R.layout.percentage_preference_widget);
        setDialogLayoutResource(R.layout.percentage_preference_dialog);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView percText = view.findViewById(R.id.textView);
        if (percText != null) {
            percText.setText(String.format(getContext().getString(R.string.percentage_preference_widget_text), percentage));
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            percentage = getPersistedInt(DEFAULT_VALUE);
        } else {
            percentage = (Integer)defaultValue;
            persistInt(percentage);
        }
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        NumberPicker picker = view.findViewById(R.id.percentagePicker);

        ArrayList<String> values = new ArrayList<>();
        int i = mMinValue;
        while (i <= mMaxValue) {
            values.add(String.valueOf(i));
            i = i + mStepValue;
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

        NumberPicker picker = view.findViewById(R.id.percentagePicker);

        String[] values = picker.getDisplayedValues();
        int pos = 0;
        while (pos < values.length) {
            if (values[pos].equals(String.valueOf(percentage))) { break; }
            pos++;
        }
        if (pos >= values.length) { pos = 0; }
        picker.setValue(pos);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            persistInt(percentage);
            notifyChanged();
            callChangeListener(percentage);
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        try { percentage = Integer.valueOf(numberPicker.getDisplayedValues()[newVal]); } finally { }
    }

    public void setPercentage(int value) {
        if ((value == percentage) || (value < mMinValue) || (value > mMaxValue)) return;
        percentage = value;
    }
}
