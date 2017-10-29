package com.dgl.auto.autosettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.dgl.auto.ISettingManager;
import com.dgl.auto.SettingManager;

import org.json.JSONException;
import org.json.JSONObject;

public class EqualizerPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, Spinner.OnItemSelectedListener {

    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/res/com.dgl.auto.autosettings";

    private static final String ATTR_BASS_DEFAULT_VALUE = "bassDefaultValue";
    private static final String ATTR_BASS_MAX_VALUE = "bassMaxValue";
    private static final String ATTR_MIDDLE_DEFAULT_VALUE = "middleDefaultValue";
    private static final String ATTR_MIDDLE_MAX_VALUE = "middleMaxValue";
    private static final String ATTR_TREBLE_DEFAULT_VALUE = "trebleDefaultValue";
    private static final String ATTR_TREBLE_MAX_VALUE = "trebleMaxValue";
    private static final String ATTR_SUBWOOFER_DEFAULT_VALUE = "subwooferDefaultValue";
    private static final String ATTR_SUBWOOFER_MAX_VALUE = "subwooferMaxValue";
    private static final String ATTR_DEFAULT_PRESET = "defaultPreset";

    private static final int DEFAULT_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 14;

    private final int mBassDefaultValue;
    private final int mBassMaxValue;
    private final int mMiddleDefaultValue;
    private final int mMiddleMaxValue;
    private final int mTrebleDefaultValue;
    private final int mTrebleMaxValue;
    private final int mSubwooferDefaultValue;
    private final int mSubwooferMaxValue;
    private final int mDefaultPreset;

    private int mCurrentBassValue;
    private int mCurrentMiddleValue;
    private int mCurrentTrebleValue;
    private int mCurrentSubValue;
    private int mCurrentPreset;

    private int mBassValue;
    private int mMiddleValue;
    private int mTrebleValue;
    private int mSubValue;
    private int mPreset;

    private SeekBar mBassSeekBar;
    private SeekBar mMiddleSeekBar;
    private SeekBar mTrebleSeekBar;
    private SeekBar mSubSeekBar;
    private Spinner mPresetsSpinner;

    public EqualizerPreference(Context context) {
        super(context);

        setPersistent(false);

        mBassMaxValue = DEFAULT_MAX_VALUE;
        mBassDefaultValue = DEFAULT_VALUE;
        mMiddleMaxValue = DEFAULT_MAX_VALUE;
        mMiddleDefaultValue = DEFAULT_VALUE;
        mTrebleMaxValue = DEFAULT_MAX_VALUE;
        mTrebleDefaultValue = DEFAULT_VALUE;
        mSubwooferMaxValue = DEFAULT_MAX_VALUE;
        mSubwooferDefaultValue = DEFAULT_VALUE;
        mDefaultPreset = DEFAULT_VALUE;

        setDialogLayoutResource(R.layout.equalizer_preference);
    }

    public EqualizerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPersistent(false);

        mBassMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_BASS_MAX_VALUE, DEFAULT_MAX_VALUE);
        mBassDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_BASS_DEFAULT_VALUE, DEFAULT_VALUE);
        mMiddleMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIDDLE_MAX_VALUE, DEFAULT_MAX_VALUE);
        mMiddleDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIDDLE_DEFAULT_VALUE, DEFAULT_VALUE);
        mTrebleMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_TREBLE_MAX_VALUE, DEFAULT_MAX_VALUE);
        mTrebleDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_TREBLE_DEFAULT_VALUE, DEFAULT_VALUE);
        mSubwooferMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_SUBWOOFER_MAX_VALUE, DEFAULT_MAX_VALUE);
        mSubwooferDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_SUBWOOFER_DEFAULT_VALUE, DEFAULT_VALUE);
        mDefaultPreset = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_DEFAULT_PRESET, DEFAULT_VALUE);

        setDialogLayoutResource(R.layout.equalizer_preference);
    }

    public EqualizerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setPersistent(false);

        mBassMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_BASS_MAX_VALUE, DEFAULT_MAX_VALUE);
        mBassDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_BASS_DEFAULT_VALUE, DEFAULT_VALUE);
        mMiddleMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIDDLE_MAX_VALUE, DEFAULT_MAX_VALUE);
        mMiddleDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIDDLE_DEFAULT_VALUE, DEFAULT_VALUE);
        mTrebleMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_TREBLE_MAX_VALUE, DEFAULT_MAX_VALUE);
        mTrebleDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_TREBLE_DEFAULT_VALUE, DEFAULT_VALUE);
        mSubwooferMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_SUBWOOFER_MAX_VALUE, DEFAULT_MAX_VALUE);
        mSubwooferDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_SUBWOOFER_DEFAULT_VALUE, DEFAULT_VALUE);
        mDefaultPreset = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_DEFAULT_PRESET, DEFAULT_VALUE);

        setDialogLayoutResource(R.layout.equalizer_preference);
    }

    public EqualizerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setPersistent(false);

        mBassMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_BASS_MAX_VALUE, DEFAULT_MAX_VALUE);
        mBassDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_BASS_DEFAULT_VALUE, DEFAULT_VALUE);
        mMiddleMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIDDLE_MAX_VALUE, DEFAULT_MAX_VALUE);
        mMiddleDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIDDLE_DEFAULT_VALUE, DEFAULT_VALUE);
        mTrebleMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_TREBLE_MAX_VALUE, DEFAULT_MAX_VALUE);
        mTrebleDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_TREBLE_DEFAULT_VALUE, DEFAULT_VALUE);
        mSubwooferMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_SUBWOOFER_MAX_VALUE, DEFAULT_MAX_VALUE);
        mSubwooferDefaultValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_SUBWOOFER_DEFAULT_VALUE, DEFAULT_VALUE);
        mDefaultPreset = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_DEFAULT_PRESET, DEFAULT_VALUE);

        setDialogLayoutResource(R.layout.equalizer_preference);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final View layout = super.onCreateView(parent);

        layout.setPadding(layout.getPaddingLeft() + 15, layout.getPaddingTop(), layout.getPaddingRight(), layout.getPaddingBottom());
        return layout;
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        //LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = inflater.inflate(R.layout.equalizer_preference, null);

        mBassSeekBar = view.findViewById(R.id.seekBarBass);
        mBassSeekBar.setMax(mBassMaxValue);
        mBassSeekBar.setOnSeekBarChangeListener(this);

        mMiddleSeekBar = view.findViewById(R.id.seekBarMiddle);
        mMiddleSeekBar.setMax(mMiddleMaxValue);
        mMiddleSeekBar.setOnSeekBarChangeListener(this);

        mTrebleSeekBar = view.findViewById(R.id.seekBarTreble);
        mTrebleSeekBar.setMax(mTrebleMaxValue);
        mTrebleSeekBar.setOnSeekBarChangeListener(this);

        mSubSeekBar = view.findViewById(R.id.seekBarSubwoofer);
        mSubSeekBar.setMax(mSubwooferMaxValue);
        mSubSeekBar.setOnSeekBarChangeListener(this);

        mPresetsSpinner = view.findViewById(R.id.spinnerPresets);
        mPresetsSpinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        SharedPreferences sharedPreferences = getSharedPreferences();
        mCurrentBassValue = sharedPreferences.getInt(getContext().getResources().getString(R.string.sp_sound_equalizer_bass), mBassDefaultValue);
        mCurrentMiddleValue = sharedPreferences.getInt(getContext().getResources().getString(R.string.sp_sound_equalizer_middle), mMiddleDefaultValue);
        mCurrentTrebleValue = sharedPreferences.getInt(getContext().getResources().getString(R.string.sp_sound_equalizer_treble), mTrebleDefaultValue);
        mCurrentSubValue = sharedPreferences.getInt(getContext().getResources().getString(R.string.sp_sound_equalizer_subwoofer), mSubwooferDefaultValue);
        mCurrentPreset = sharedPreferences.getInt(getContext().getResources().getString(R.string.sp_sound_equalizer_preset), mDefaultPreset);

        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            try {
                mCurrentBassValue = sm.getBass();
                mCurrentMiddleValue = sm.getMiddle();
                mCurrentTrebleValue = sm.getTreble();
                mCurrentSubValue = sm.getSubwoofer();
                mCurrentPreset = sm.getEQ();

                mBassValue = mCurrentBassValue;
                mMiddleValue = mCurrentMiddleValue;
                mTrebleValue = mCurrentTrebleValue;
                mSubValue = mCurrentSubValue;
                mPreset = mCurrentPreset;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        mBassSeekBar.setProgress(mCurrentBassValue);
        mMiddleSeekBar.setProgress(mCurrentMiddleValue);
        mTrebleSeekBar.setProgress(mCurrentTrebleValue);
        mSubSeekBar.setProgress(mCurrentSubValue);
        mPresetsSpinner.setSelection(mCurrentPreset);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            SharedPreferences.Editor editor = getEditor();
            editor.putInt(getContext().getResources().getString(R.string.sp_sound_equalizer_bass), mCurrentBassValue);
            editor.putInt(getContext().getResources().getString(R.string.sp_sound_equalizer_middle), mCurrentMiddleValue);
            editor.putInt(getContext().getResources().getString(R.string.sp_sound_equalizer_treble), mCurrentTrebleValue);
            editor.putInt(getContext().getResources().getString(R.string.sp_sound_equalizer_subwoofer), mCurrentSubValue);
            editor.putInt(getContext().getResources().getString(R.string.sp_sound_equalizer_preset), mCurrentPreset);
            editor.commit();

            callChangeListener(null);
        } else {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                try {
                    sm.setEQ(mPreset);
                    sm.setBass(mBassValue);
                    sm.setMiddle(mMiddleValue);
                    sm.setTreble(mTrebleValue);
                    sm.setSubwoofer(mSubValue);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getBass() {
        return mCurrentBassValue;
    }

    public int getMiddle() {
        return mCurrentMiddleValue;
    }

    public int getTreble() {
        return mCurrentTrebleValue;
    }

    public int getSubwoofer() {
        return mCurrentSubValue;
    }

    public int getPreset() {
        return mCurrentPreset;
    }

    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        if (!fromTouch) { return; }

        ISettingManager sm = SettingManager.getInstance();

        if ((mCurrentPreset != 0) && (seek.getId() != R.id.seekBarSubwoofer))  {
            mCurrentPreset = 0;
            mPresetsSpinner.setSelection(mCurrentPreset);
            if (sm != null) {
                try {
                    sm.setEQ(mCurrentPreset);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        switch (seek.getId()) {
            case R.id.seekBarBass: {
                mCurrentBassValue = value;
                if (sm != null) {
                    try {
                        sm.setBass(value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case R.id.seekBarMiddle: {
                mCurrentMiddleValue = value;
                if (sm != null) {
                    try {
                        sm.setMiddle(value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case R.id.seekBarTreble: {
                mCurrentTrebleValue = value;
                if (sm != null) {
                    try {
                        sm.setTreble(value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case R.id.seekBarSubwoofer: {
                mCurrentSubValue = value;
                if (sm != null) {
                    try {
                        sm.setSubwoofer(value);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seek) { }

    public void onStopTrackingTouch(SeekBar seek) { }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCurrentPreset = position;
        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            try {
                sm.setEQ(mCurrentPreset);
                if (mCurrentPreset == 0) {
                    sm.setBass(mCurrentBassValue);
                    sm.setMiddle(mCurrentMiddleValue);
                    sm.setTreble(mCurrentTrebleValue);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (mCurrentPreset != 0) {
            String[] presets = getContext().getResources().getStringArray(R.array.sound_equalizer_presets);
            try {
                JSONObject data = new JSONObject(presets[mCurrentPreset]);
                mCurrentBassValue = data.getInt("bass");
                mCurrentMiddleValue = data.getInt("middle");
                mCurrentTrebleValue = data.getInt("treble");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mBassSeekBar.setProgress(mCurrentBassValue);
            mMiddleSeekBar.setProgress(mCurrentMiddleValue);
            mTrebleSeekBar.setProgress(mCurrentTrebleValue);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        mCurrentPreset = 0;
        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            try {
                sm.setEQ(mCurrentPreset);
                sm.setBass(mCurrentBassValue);
                sm.setMiddle(mCurrentMiddleValue);
                sm.setTreble(mCurrentTrebleValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
