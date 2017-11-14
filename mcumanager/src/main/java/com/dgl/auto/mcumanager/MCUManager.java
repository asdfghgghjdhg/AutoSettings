package com.dgl.auto.mcumanager;

import android.graphics.Color;
import android.os.RemoteException;
import android.util.Log;

import com.dgl.auto.IRadioManager;
import com.dgl.auto.ISettingManager;
import com.dgl.auto.RadioManager;
import com.dgl.auto.SettingManager;
import com.dgl.auto.constant.ISettingConstant;

public class MCUManager {
    private static final String LOG_TAG = "MCUManager";

    public static class CarInfo {

        public static String getCarNumber() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getCarNumber();
            }

            throw new RemoteException();
        }

    }

    public static class EqualizerControl {

        public static final int MAX_BASS_VALUE = 14;
        public static final int MAX_MIDDLE_VALUE = 14;
        public static final int MAX_TREBLE_VALUE = 14;
        public static final int MAX_SUBWOOFER_VALUE = 14;

        public static class EqualizerPreset {
            int mMCUIndex;
            private int mBassValue;
            private int mMiddleValue;
            private int mTrebleValue;

            EqualizerPreset() {
                mMCUIndex = ISettingConstant.EQType.EQ_USER;
                mBassValue = 7;
                mMiddleValue = 7;
                mTrebleValue = 7;
            }

            EqualizerPreset(int bassValue, int middleValue, int trebleValue) {
                if (bassValue < 0) bassValue = 0;
                if (bassValue > MAX_BASS_VALUE) bassValue = MAX_BASS_VALUE;
                mBassValue = bassValue;

                if (middleValue < 0) middleValue = 0;
                if (middleValue > MAX_MIDDLE_VALUE) middleValue = MAX_MIDDLE_VALUE;
                mMiddleValue = middleValue;

                if (trebleValue < 0) trebleValue = 0;
                if (trebleValue > MAX_TREBLE_VALUE) trebleValue = MAX_TREBLE_VALUE;
                mTrebleValue = trebleValue;

                mMCUIndex = ISettingConstant.EQType.EQ_USER;
            }

            public int getBass() { return mBassValue; }
            public int getMCUIndex() { return mMCUIndex; }
            public int getMiddle() { return mMiddleValue; }
            public int getTreble() { return mTrebleValue; }

            public void setBass(int value) {
                if (value < 0) value = 0;
                if (value > MAX_BASS_VALUE) value = MAX_BASS_VALUE;
                mBassValue = value;
            }

            public void setMiddle(int value) {
                if (value < 0) value = 0;
                if (value > MAX_MIDDLE_VALUE) value = MAX_MIDDLE_VALUE;
                mMiddleValue = value;
            }

            public void setTreble(int value) {
                if (value < 0) value = 0;
                if (value > MAX_TREBLE_VALUE) value = MAX_TREBLE_VALUE;
                mTrebleValue = value;
            }
        }

        private static class MCUEqualizerPreset extends EqualizerPreset {
            MCUEqualizerPreset(int mcuIndex, int bassValue, int middleValue, int trebleValue) {
                super(bassValue, middleValue, trebleValue);

                mMCUIndex = mcuIndex;
            }
        }

        public static final EqualizerPreset    CUSTOM_PRESET      = new EqualizerPreset();
        //public static final MCUEqualizerPreset FLAT_PRESET        = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_FLAT,7,7,7);
        //public static final MCUEqualizerPreset JAZZ_PRESET        = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_JAZZ,13,7,11);
        //public static final MCUEqualizerPreset POP_PRESET         = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_POP,11,7,13);
        //public static final MCUEqualizerPreset CLASSIC_PRESET     = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_CLASSIC,11,7,11);
        //public static final MCUEqualizerPreset ROCK_PRESET        = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_ROCK,13,7,13);
        //public static final MCUEqualizerPreset NEWS_PRESET        = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_NEWS,5,7,5);
        //public static final MCUEqualizerPreset CITY_PRESET        = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_CITY,11,7,12);
        //public static final MCUEqualizerPreset ELECTRONIC_PRESET  = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_ELECTRONIC,12,7,13);
        //public static final MCUEqualizerPreset MOVIE_PRESET       = new MCUEqualizerPreset(ISettingConstant.EQType.EQ_MOVIE,10,7,5);
        //public static final MCUEqualizerPreset TECHNO_PRESET      = new MCUEqualizerPreset(10,12,7,10);

        public static final MCUEqualizerPreset JAZZ_PRESET        = new MCUEqualizerPreset(1,13,7,11);
        public static final MCUEqualizerPreset POP_PRESET         = new MCUEqualizerPreset(2,11,7,13);
        public static final MCUEqualizerPreset CLASSIC_PRESET     = new MCUEqualizerPreset(3,11,7,11);
        public static final MCUEqualizerPreset ROCK_PRESET        = new MCUEqualizerPreset(4,13,7,13);
        public static final MCUEqualizerPreset NEWS_PRESET        = new MCUEqualizerPreset(5,5,7,5);
        public static final MCUEqualizerPreset CITY_PRESET        = new MCUEqualizerPreset(6,11,7,12);
        public static final MCUEqualizerPreset ELECTRONIC_PRESET  = new MCUEqualizerPreset(7,12,7,13);
        public static final MCUEqualizerPreset MOVIE_PRESET       = new MCUEqualizerPreset(8,10,7,5);
        public static final MCUEqualizerPreset TECHNO_PRESET      = new MCUEqualizerPreset(9,12,7,10);

        public static final EqualizerPreset[] PRESETS = {
                CUSTOM_PRESET,
                //FLAT_PRESET,
                JAZZ_PRESET,
                POP_PRESET,
                CLASSIC_PRESET,
                ROCK_PRESET,
                NEWS_PRESET,
                CITY_PRESET,
                ELECTRONIC_PRESET,
                MOVIE_PRESET,
                TECHNO_PRESET
        };

        public static int getBass() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int presetIndex = sm.getEQ();
                if ((presetIndex == 0) || (presetIndex >= PRESETS.length)) {
                    return sm.getBass();
                } else {
                    return PRESETS[presetIndex].getBass();
                }
            }

            throw new RemoteException();
        }

        public static boolean getLoudMode() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getLound();
            }

            throw new RemoteException();
        }

        public static int getMiddle() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int presetIndex = sm.getEQ();
                if ((presetIndex == 0) || (presetIndex >= PRESETS.length)) {
                    return sm.getMiddle();
                } else {
                    return PRESETS[presetIndex].getMiddle();
                }
            }

            throw new RemoteException();
        }

        public static EqualizerPreset getPreset() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int presetIndex = sm.getEQ();
                if (presetIndex == 0) {
                    return new EqualizerPreset(sm.getBass(), sm.getMiddle(), sm.getTreble());
                }
                if ((presetIndex > 0) && (presetIndex < PRESETS.length)) {
                    return PRESETS[presetIndex];
                }
                return new MCUEqualizerPreset(presetIndex, sm.getBass(), sm.getMiddle(), sm.getTreble());
            }

            throw new RemoteException();
        }

        public static int getPresetIndex() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getEQ();
            }

            throw new RemoteException();
        }

        public static int getTreble() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int presetIndex = sm.getEQ();
                if ((presetIndex == 0) || (presetIndex >= PRESETS.length)) {
                    return sm.getTreble();
                } else {
                    return PRESETS[presetIndex].getTreble();
                }
            }

            throw new RemoteException();
        }

        public static int getSubwoofer() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getSubwoofer();
            }

            throw new RemoteException();
        }

        public static void setBass(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_BASS_VALUE) value = MAX_BASS_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int presetIndex = sm.getEQ();
                if ((presetIndex == 0) || (presetIndex >= PRESETS.length)) {
                    sm.setBass(value);
                } else {
                    if (PRESETS[presetIndex].getBass() != value) {
                        int middle = PRESETS[presetIndex].getMiddle();
                        int treble = PRESETS[presetIndex].getTreble();
                        sm.setEQ(0);
                        sm.setBass(value);
                        sm.setMiddle(middle);
                        sm.setTreble(treble);
                    }
                }
                return;
            }

            throw new RemoteException();
        }

        public static void setLoudMode(boolean on) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setLound(on);
                return;
            }

            throw new RemoteException();
        }

        public static void setMiddle(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_MIDDLE_VALUE) value = MAX_MIDDLE_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int presetIndex = sm.getEQ();
                if ((presetIndex == 0) || (presetIndex >= PRESETS.length)) {
                    sm.setMiddle(value);
                } else {
                    if (PRESETS[presetIndex].getMiddle() != value) {
                        int bass = PRESETS[presetIndex].getBass();
                        int treble = PRESETS[presetIndex].getTreble();
                        sm.setEQ(0);
                        sm.setBass(bass);
                        sm.setMiddle(value);
                        sm.setTreble(treble);
                    }
                }
                return;
            }

            throw new RemoteException();
        }

        public static void setTreble(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_TREBLE_VALUE) value = MAX_TREBLE_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int presetIndex = sm.getEQ();
                if ((presetIndex == 0) || (presetIndex >= PRESETS.length)) {
                    sm.setTreble(value);
                } else {
                    if (PRESETS[presetIndex].getTreble() != value) {
                        int bass = PRESETS[presetIndex].getBass();
                        int middle = PRESETS[presetIndex].getMiddle();
                        sm.setEQ(0);
                        sm.setBass(bass);
                        sm.setMiddle(middle);
                        sm.setTreble(value);
                    }
                }
                return;
            }

            throw new RemoteException();
        }

        public static void setPreset(EqualizerPreset preset) throws RemoteException {
            int presetIndex = preset.getMCUIndex();
            if (presetIndex == 0) {
                for (int i = 1; i < PRESETS.length; i++) {
                    if ((PRESETS[i].getBass() == preset.getBass()) && (PRESETS[i].getMiddle() == preset.getMiddle()) && (PRESETS[i].getTreble() == preset.getTreble())) {
                        presetIndex = i;
                        break;
                    }
                }
            }

            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setEQ(presetIndex);
                if (presetIndex == 0) {
                    sm.setBass(preset.getBass());
                    sm.setMiddle(preset.getMiddle());
                    sm.setTreble(preset.getTreble());
                }
                return;
            }

            throw new RemoteException();
        }

        public static void setPresetIndex(int index) throws RemoteException {
            if ((index >= 0) && (index < PRESETS.length)) {
                ISettingManager sm = SettingManager.getInstance();
                if (sm != null) {
                    sm.setEQ(index);
                    return;
                }
            }

            throw new RemoteException();
        }

        public static void setSubwoofer(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_SUBWOOFER_VALUE) value = MAX_SUBWOOFER_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setSubwoofer(value);
                return;
            }

            throw new RemoteException();
        }
    }

    public static class MCUInfo {

        public static String getBluetoothVersion() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getBTVersion();
            }

            throw new RemoteException();
        }

        public static String getCanBusVersion() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getCanVersion();
            }

            throw new RemoteException();
        }

        public static String getEMMCId() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.GetEmmcId();
            }

            throw new RemoteException();
        }

        public static String getMCUVersion() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.GetMcuVersion();
            }

            throw new RemoteException();
        }

        public static String getSystemVersion() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getSystemVersion();
            }

            throw new RemoteException();
        }

    }

    public static class MultimediaControl {

        public static boolean getPlayVideoWhileDriving() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getCanWatchVideoWhileDriver();
            }

            throw new RemoteException();
        }

        public static boolean getSwitchMediaStatus() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.GetSwitchMediaStatus();
            }

            throw new RemoteException();
        }

        public static void setPlayVideoWhileDriving(boolean on) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setCanWatchVideoWhileDriver(on);
                return;
            }

            throw new RemoteException();
        }

        public static void setSwitchMediaStatus(boolean on) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.SetSwitchMediaStatus(on);
                return;
            }

            throw new RemoteException();
        }

    }

    public static class RadioControl {

        public enum RadioBand {AM, FM}
        public static final RadioBand BAND_AM = RadioBand.AM;
        public static final RadioBand BAND_FM = RadioBand.FM;

        public enum RadioBandIndex {AM1, AM2, FM1, FM2, FM3}
        public static final RadioBandIndex BAND_AM1 = RadioBandIndex.AM1;
        public static final RadioBandIndex BAND_AM2 = RadioBandIndex.AM2;
        public static final RadioBandIndex BAND_FM1 = RadioBandIndex.FM1;
        public static final RadioBandIndex BAND_FM2 = RadioBandIndex.FM2;
        public static final RadioBandIndex BAND_FM3 = RadioBandIndex.FM3;

        public static final int DEFAULT_AM_FREQUENCY = IRadioManager.IRadioConstant.RADIO_AM_DEFUALT_FREQ;
        public static final int DEFAULT_FM_FREQUENCY = IRadioManager.IRadioConstant.RADIO_FM_DEFUALT_FREQ * 10;

        public static class RadioRegion {
            private int mRegionIndex;
            private int mMinAMFrequency;
            private int mMaxAMFrequency;
            private int mAMStep;
            private int mMinFMFrequency;
            private int mMaxFMFrequency;
            private int mFMStep;

            RadioRegion(int index, int minAMFreq, int maxAMFreq, int stepAM, int minFMFreq, int maxFMFreq, int stepFM) {
                mRegionIndex = index;
                mMinAMFrequency = minAMFreq;
                mMaxAMFrequency = maxAMFreq;
                mAMStep = stepAM;
                mMinFMFrequency = minFMFreq;
                mMaxFMFrequency = maxFMFreq;
                mFMStep = stepFM;
            }

            public int getRegionIndex() { return mRegionIndex; }
            public int getMinAMFrequency() { return mMinAMFrequency; }
            public int getMaxAMFrequency() { return mMaxAMFrequency; }
            public int getAMStep() { return mAMStep; }
            public int getMinFMFrequency() { return mMinFMFrequency; }
            public int getMaxFMFrequency() { return mMaxFMFrequency; }
            public int getFMStep() { return mFMStep; }
        }

        public static final RadioRegion REGION_USA      = new RadioRegion(IRadioManager.IRadioConstant.REGION_USA, 530, 1710, 10, 87900, 107900, 200);
        public static final RadioRegion REGION_EUROPE   = new RadioRegion(IRadioManager.IRadioConstant.REGION_Europe, 531, 1602, 9, 87500, 108000, 50);
        public static final RadioRegion REGION_LATIN    = new RadioRegion(IRadioManager.IRadioConstant.REGION_Latin, 520, 1620, 10, 87500, 108000, 100);
        public static final RadioRegion REGION_OIRT     = new RadioRegion(IRadioManager.IRadioConstant.REGION_OIRT, 531, 1620, 9, 65000, 74000, 50);
        public static final RadioRegion REGION_CHINA    = new RadioRegion(IRadioManager.IRadioConstant.REGION_China, 531, 1629, 9, 87500, 108000, 100);
        public static final RadioRegion REGION_JAPAN    = new RadioRegion(IRadioManager.IRadioConstant.REGION_JAPAN, 522, 1629, 9, 76000, 90000, 100);

        public static final RadioRegion[] REGIONS = {REGION_USA, REGION_EUROPE, REGION_LATIN, REGION_OIRT, REGION_CHINA, REGION_JAPAN};

        public static RadioBand getBand() throws RemoteException {
            IRadioManager rm = RadioManager.getInstance();
            if (rm != null) {
                int index = rm.getBand();
                if ((index == IRadioManager.IRadioConstant.BAND_AM_1) || (index == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    return RadioBand.AM;
                } else {
                    return RadioBand.FM;
                }
            }

            throw new RemoteException();
        }

        public static RadioBandIndex getBandIndex() throws RemoteException {
            IRadioManager rm = RadioManager.getInstance();
            if (rm != null) {
                int index = rm.getBand();
                if (index == IRadioManager.IRadioConstant.BAND_AM_1) {
                    return BAND_AM1;
                }
                if (index == IRadioManager.IRadioConstant.BAND_AM_2) {
                    return BAND_AM2;
                }
                if (index == IRadioManager.IRadioConstant.BAND_FM_1) {
                    return BAND_FM1;
                }
                if (index == IRadioManager.IRadioConstant.BAND_FM_2) {
                    return BAND_FM2;
                }
                if (index == IRadioManager.IRadioConstant.BAND_FM_3) {
                    return BAND_FM3;
                }
            }

            throw new RemoteException();
        }

        public static int getFrequency() throws RemoteException {
            IRadioManager rm = RadioManager.getInstance();
            if (rm != null) {
                int band = rm.getBand();
                if ((band == IRadioManager.IRadioConstant.BAND_AM_1) || (band == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    return rm.getCurrFreq();
                } else {
                    return rm.getCurrFreq() * 10;
                }
            }

            throw new RemoteException();
        }

        public static RadioRegion getRegion() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int index = sm.getRadioField();
                int i = 0;
                while (i < REGIONS.length) {
                    if (REGIONS[i].getRegionIndex() == index) break;
                    i++;
                }
                if (i < REGIONS.length) {
                    return REGIONS[i];
                }
            }

            throw new RemoteException();
        }

        public static int getRegionIndex() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getRadioField();
            }

            throw new RemoteException();
        }

        public static void setFrequency(int frequency) throws RemoteException {
            IRadioManager rm = RadioManager.getInstance();
            if (rm != null) {
                int band = rm.getBand();
                if ((band == IRadioManager.IRadioConstant.BAND_AM_1) || (band == IRadioManager.IRadioConstant.BAND_AM_2)) {
                    //if (frequency == rm.getCurrFreq()) { return; }
                    if ((frequency < rm.getMinAMFreq()) || (frequency > rm.getMaxAMFreq())) {
                        frequency = DEFAULT_AM_FREQUENCY;
                    }
                    rm.setFreq((char)frequency);
                    return;
                } else {
                    //if (frequency == rm.getCurrFreq() * 10) { return; }
                    if ((frequency < rm.getMinFMFreq() * 10) || (frequency > rm.getMaxFMFreq() * 10)) {
                        frequency = DEFAULT_FM_FREQUENCY;
                    }
                    frequency = frequency / 10;
                    rm.setFreq((char)frequency);
                    return;
                }
            }

            throw new RemoteException();
        }

        public static void setRegion(RadioRegion region) throws RemoteException {
            int regionIndex = region.getRegionIndex();
            if (regionIndex < 0) regionIndex = 0;
            if (regionIndex >= REGIONS.length) regionIndex = 0;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                if (regionIndex != sm.getRadioField()) { sm.setRadioField(regionIndex); }
                return;
            }

            throw new RemoteException();
        }

        public static void setRegionIndex(int regionIndex) throws RemoteException {
            if (regionIndex < 0) regionIndex = 0;
            if (regionIndex >= REGIONS.length) regionIndex = 0;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                if (regionIndex != sm.getRadioField()) { sm.setRadioField(regionIndex); }
                return;
            }

            throw new RemoteException();
        }
    }

    public static class RearViewCamera {

        public static boolean getAddParkingLines() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getReverseAuxLine();
            }

            throw new RemoteException();
        }

        public static boolean getMirrorView() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getReverseMirror();
            }

            throw new RemoteException();
        }

        public static void setAddParkingLines(boolean on) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setReverseAuxLine(on);
                return;
            }

            throw new RemoteException();
        }

        public static void setMirrorView(boolean on) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setReverseMirror(on);
                return;
            }

            throw new RemoteException();
        }

    }

    public static class ScreenControl {

        public static final int MAX_CONTRAST_VALUE = 127;
        public static final class HSB_CONSTANTS {
            public static final int MAX_HUE_VALUE = 127;
            public static final int MAX_SATURATION_VALUE = 127;
            public static final int MAX_BRIGHTNESS_VALUE = 127;
        }

        public static int getContrast() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getContrast();
            }

            throw new RemoteException();
        }

        public static boolean getDetectIllumination() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getIllumeDetection();
            }

            throw new RemoteException();
        }

        public static int getIlluminationBrightness() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getBright();
            }

            throw new RemoteException();
        }

        public static int getIlluminationColor() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                float h = (float)sm.getHueSetting() / HSB_CONSTANTS.MAX_HUE_VALUE * 360;
                float s = (float)sm.getSaturation() / HSB_CONSTANTS.MAX_SATURATION_VALUE;
                float v = (float)sm.getBright() / HSB_CONSTANTS.MAX_BRIGHTNESS_VALUE;
                float[] hsv = {h, s, v};
                return Color.HSVToColor(hsv);
            }

            throw new RemoteException();
        }

        public static int getIlluminationHue() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getHueSetting();
            }

            throw new RemoteException();
        }

        public static int getIlluminationSaturation() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getSaturation();
            }

            throw new RemoteException();
        }

        public static void setContrast(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_CONTRAST_VALUE) value = MAX_CONTRAST_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setContrast(value);
                return;
            }

            throw new RemoteException();
        }

        public static void setDetectIllumination(boolean on) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setIllumeDetection(on);
                return;
            }

            throw new RemoteException();
        }

        public static void setIlluminationColor(int color) throws RemoteException {
            float[] hsv = {0xFF, 0xFF, 0xFF};
            Color.colorToHSV(color, hsv);
            int h = Math.round(hsv[0] / 360 * HSB_CONSTANTS.MAX_HUE_VALUE);
            int s = Math.round(hsv[1] * HSB_CONSTANTS.MAX_SATURATION_VALUE);
            int v = Math.round(hsv[2] * HSB_CONSTANTS.MAX_BRIGHTNESS_VALUE);
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setHueSetting(h);
                sm.setSaturation(s);
                sm.setBright(v);
                return;
            }

            throw new RemoteException();
        }

        public static void setIlluminationBrightness(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > HSB_CONSTANTS.MAX_BRIGHTNESS_VALUE) value = HSB_CONSTANTS.MAX_BRIGHTNESS_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setBright(value);
                return;
            }

            throw new RemoteException();
        }

        public static void setIlluminationHue(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > HSB_CONSTANTS.MAX_HUE_VALUE) value = HSB_CONSTANTS.MAX_HUE_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setHueSetting(value);
                return;
            }

            throw new RemoteException();
        }

        public static void setIlluminationSaturation(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > HSB_CONSTANTS.MAX_SATURATION_VALUE) value = HSB_CONSTANTS.MAX_SATURATION_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setSaturation(value);
                return;
            }

            throw new RemoteException();
        }
    }

    public static class SWCControl {

        public enum SWCType {TYPE_1, TYPE_2}

        public static SWCType getSWCType() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int value = sm.getSWCTypeValue();
                switch (value) {
                    case 0: return SWCType.TYPE_1;
                    case 1: return SWCType.TYPE_2;
                }
            }

            throw new RemoteException();
        }

        public static void setSWCType(SWCType type) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                switch (type) {
                    case TYPE_1: {
                        sm.setSWCTypeValue(0);
                        return;
                    }
                    case TYPE_2: {
                        sm.setSWCTypeValue(1);
                        return;
                    }
                }
            }

            throw new RemoteException();
        }

    }

    public static class USBControl {

        public enum USBType { USB11, USB20 }

        public static USBType getUSB0Type() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int value = sm.getUSB0TypeValue();
                switch (value) {
                    case 0: return USBType.USB20;
                    case 1: return USBType.USB11;
                }
            }

            throw new RemoteException();
        }

        public static USBType getUSB1Type() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                int value = sm.getUSB1TypeValue();
                switch (value) {
                    case 0: return USBType.USB20;
                    case 1: return USBType.USB11;
                }
            }

            throw new RemoteException();
        }

        public static void setUSB0Type(USBType type) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                switch (type) {
                    case USB11: {
                        sm.setUSB0TypeValue(1);
                        return;
                    }
                    case USB20: {
                        sm.setUSB0TypeValue(1);
                        return;
                    }
                }
            }

            throw new RemoteException();
        }

        public static void setUSB1Type(USBType type) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                switch (type) {
                    case USB11: {
                        sm.setUSB1TypeValue(1);
                        return;
                    }
                    case USB20: {
                        sm.setUSB1TypeValue(1);
                        return;
                    }
                }
            }

            throw new RemoteException();
        }

    }

    public static class VolumeControl {

        public static final int MAX_VOLUME_VALUE = 40;
        public static final int MAX_BALANCE_VALUE = 14;
        public static final int MAX_FADE_VALUE = 14;

        public static int getBalance() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getBalance();
            }

            throw new RemoteException();
        }

        public static int getFade() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getFade();
            }

            throw new RemoteException();
        }

        public static boolean getMuted() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.isMcuMute();
            }

            throw new RemoteException();
        }

        public static int getVolume() throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                return sm.getMcuVol();
            }

            throw new RemoteException();
        }

        public static void setBalance(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_BALANCE_VALUE) value = MAX_BALANCE_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setBalance(value);
                return;
            }

            throw new RemoteException();
        }

        public static void setFade(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_FADE_VALUE) value = MAX_FADE_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setFade(value);
                return;
            }

            throw new RemoteException();
        }

        public static void setMuted(boolean muted) throws RemoteException {
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setMcuMute(muted ? 1 : 0);
                return;
            }

            throw new RemoteException();
        }

        public static void setVolume(int value) throws RemoteException {
            if (value < 0) value = 0;
            if (value > MAX_VOLUME_VALUE) value = MAX_VOLUME_VALUE;
            ISettingManager sm = SettingManager.getInstance();
            if (sm != null) {
                sm.setMcuVol(value);
                return;
            }

            throw new RemoteException();
        }
    }

    public static int getBootTime() throws RemoteException {
        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            return sm.getBootTime();
        }

        throw new RemoteException();
    }

    public static boolean getShortcutTouchState() throws RemoteException {
        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            return sm.getShortcutTouchState();
        }

        throw new RemoteException();
    }

    public static void setBootTime(int value) throws RemoteException {
        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            sm.setBootTime(value);
            return;
        }

        throw new RemoteException();
    }

    public static void setShortcutTouchState(boolean on) throws RemoteException {
        ISettingManager sm = SettingManager.getInstance();
        if (sm != null) {
            sm.setShortcutTouchState(on);
            return;
        }

        throw new RemoteException();
    }

}
