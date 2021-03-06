/*
 * Copyright (C) 2017 XenonHD Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.cmparts.input;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class BatteryBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

        private static final String TAG = "BatteryBar";


        private static final String PREF_BATT_BAR = "battery_bar_list";
        private static final String PREF_BATT_BAR_NO_NAVBAR = "battery_bar_no_navbar_list";
        private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
        private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
        private static final String PREF_BATT_BAR_CHARGING_COLOR = "battery_bar_charging_color";
        private static final String STATUS_BAR_BATTERY_LOW_COLOR_WARNING = "battery_bar_battery_low_color_warning";
        private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
        private static final String PREF_BATT_ANIMATE = "battery_bar_animate";
        private static final String STATUS_BAR_USE_GRADIENT_COLOR = "statusbar_battery_bar_use_gradient_color";
        private static final String STATUS_BAR_BAR_LOW_COLOR = "statusbar_battery_bar_low_color";
        private static final String STATUS_BAR_BAR_HIGH_COLOR = "statusbar_battery_bar_high_color";

        static final int DEFAULT_STATUS_CARRIER_COLOR = 0xffffffff;

        private ListPreference mBatteryBar;
        private ListPreference mBatteryBarNoNavbar;
        private ListPreference mBatteryBarStyle;
        private ListPreference mBatteryBarThickness;
        private SwitchPreference mBatteryBarChargingAnimation;
        private SwitchPreference mBatteryBarUseGradient;
        private ColorPickerPreference mBatteryBarColor;
        private ColorPickerPreference mBatteryBarChargingColor;
        private ColorPickerPreference mBatteryBarBatteryLowColorWarn;
        private ColorPickerPreference mBatteryBarBatteryLowColor;
        private ColorPickerPreference mBatteryBarBatteryHighColor;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.battery_bar);

            PreferenceScreen prefSet = getPreferenceScreen();
            ContentResolver resolver = getActivity().getContentResolver();

            final Resources res = getResources();

            int intColor;
            String hexColor;

            int defaultColor = 0xffffffff;
            int highColor = 0xff99CC00;
            int lowColor = 0xffff4444;

            mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
            mBatteryBar.setOnPreferenceChangeListener(this);
            mBatteryBar.setValue((Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR, 0)) + "");
            mBatteryBar.setSummary(mBatteryBar.getEntry());

            mBatteryBarNoNavbar = (ListPreference) findPreference(PREF_BATT_BAR_NO_NAVBAR);
            mBatteryBarNoNavbar.setOnPreferenceChangeListener(this);
            mBatteryBarNoNavbar.setValue((Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR, 0)) + "");
            mBatteryBarNoNavbar.setSummary(mBatteryBarNoNavbar.getEntry());

            mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
            mBatteryBarStyle.setOnPreferenceChangeListener(this);
            mBatteryBarStyle.setValue((Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0)) + "");
            mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntry());

            mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
            mBatteryBarColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_COLOR, defaultColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryBarColor.setSummary(hexColor);

            mBatteryBarChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_CHARGING_COLOR);
            mBatteryBarChargingColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, defaultColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryBarChargingColor.setSummary(hexColor);

            mBatteryBarBatteryLowColorWarn = (ColorPickerPreference) findPreference(STATUS_BAR_BATTERY_LOW_COLOR_WARNING);
            mBatteryBarBatteryLowColorWarn.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, defaultColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryBarBatteryLowColorWarn.setSummary(hexColor);

            mBatteryBarUseGradient = (SwitchPreference) findPreference(STATUS_BAR_USE_GRADIENT_COLOR);
            mBatteryBarUseGradient.setChecked(Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, 0) == 1);
            mBatteryBarUseGradient.setOnPreferenceChangeListener(this);

            mBatteryBarBatteryLowColor = (ColorPickerPreference) findPreference(STATUS_BAR_BAR_LOW_COLOR);
            mBatteryBarBatteryLowColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR, defaultColor);
            hexColor = String.format("#%08x", (0xffff0000 & lowColor));
            mBatteryBarBatteryLowColor.setSummary(hexColor);

            mBatteryBarBatteryHighColor = (ColorPickerPreference) findPreference(STATUS_BAR_BAR_HIGH_COLOR);
            mBatteryBarBatteryHighColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR, highColor);
            hexColor = String.format("#%08x", (0xff00ff00 & intColor));
            mBatteryBarBatteryHighColor.setSummary(hexColor);

            mBatteryBarChargingAnimation = (SwitchPreference) findPreference(PREF_BATT_ANIMATE);
            mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

            mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
            mBatteryBarThickness.setOnPreferenceChangeListener(this);
            mBatteryBarThickness.setValue((Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1)) + "");
            mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntry());

            boolean hasNavBarByDefault = getResources().getBoolean(
                    com.android.internal.R.bool.config_showNavigationBar);
            //boolean enableNavigationBar = Settings.Secure.getInt(resolver,
            //    Settings.Secure.NAVIGATION_BAR_VISIBLE, hasNavBarByDefault ? 1 : 0) == 1;

            //if (!hasNavBarByDefault || !enableNavigationBar) {
                prefSet.removePreference(mBatteryBar);
            //} else {
            //    prefSet.removePreference(mBatteryBarNoNavbar);
            //}

            updateBatteryBarOptions();

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            ContentResolver resolver = getActivity().getContentResolver();
            if (preference == mBatteryBarColor) {
                String hex = ColorPickerPreference.convertToARGB(Integer
                        .valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
                return true;
            } else if (preference == mBatteryBarChargingColor) {
                String hex = ColorPickerPreference.convertToARGB(Integer
                        .valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, intHex);
                return true;
            } else if (preference == mBatteryBarBatteryLowColorWarn) {
                String hex = ColorPickerPreference.convertToARGB(Integer
                        .valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, intHex);
                return true;
            } else if (preference == mBatteryBarBatteryLowColor) {
                String hex = ColorPickerPreference.convertToARGB(Integer
                        .valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR_LOW_COLOR, intHex);
                return true;
            } else if (preference == mBatteryBarBatteryHighColor) {
                String hex = ColorPickerPreference.convertToARGB(Integer
                        .valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR_HIGH_COLOR, intHex);
                return true;
            } else if (preference == mBatteryBar) {
                int val = Integer.valueOf((String) newValue);
                int index = mBatteryBar.findIndexOfValue((String) newValue);
                Settings.System.putInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR, val);
                mBatteryBar.setSummary(mBatteryBar.getEntries()[index]);
                updateBatteryBarOptions();
            } else if (preference == mBatteryBarNoNavbar) {
                int val = Integer.valueOf((String) newValue);
                int index = mBatteryBarNoNavbar.findIndexOfValue((String) newValue);
                Settings.System.putInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR, val);
                mBatteryBarNoNavbar.setSummary(mBatteryBarNoNavbar.getEntries()[index]);
                updateBatteryBarOptions();
                return true;
            } else if (preference == mBatteryBarStyle) {
                int val = Integer.valueOf((String) newValue);
                int index = mBatteryBarStyle.findIndexOfValue((String) newValue);
                Settings.System.putInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR_STYLE, val);
                mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntries()[index]);
                return true;
            } else if (preference == mBatteryBarThickness) {
                int val = Integer.valueOf((String) newValue);
                int index = mBatteryBarThickness.findIndexOfValue((String) newValue);
                Settings.System.putInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);
                mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntries()[index]);
                return true;
            } else if (preference == mBatteryBarUseGradient) {
                Settings.System.putInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, (Boolean) newValue ? 1 : 0);
                updateBatteryBarOptions();
                return true;
            }
            return false;
        }

        @Override
    	public boolean onPreferenceTreeClick(Preference preference) {
            ContentResolver resolver = getActivity().getContentResolver();
            boolean value;
            if (preference == mBatteryBarChargingAnimation) {
                value = mBatteryBarChargingAnimation.isChecked();
                Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, value ? 1 : 0);
                return true;
            } else if (preference == mBatteryBarUseGradient) {
                Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, ((SwitchPreference) preference).isChecked() ? 1 : 0);
                return true;
            }
        return super.onPreferenceTreeClick(preference);
        }

        private void updateBatteryBarOptions() {
            if (Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR, 0) == 0) {
                mBatteryBarStyle.setEnabled(false);
                mBatteryBarThickness.setEnabled(false);
                mBatteryBarChargingAnimation.setEnabled(false);
                mBatteryBarColor.setEnabled(false);
                mBatteryBarChargingColor.setEnabled(false);
                mBatteryBarUseGradient.setEnabled(false);
                mBatteryBarBatteryHighColor.setEnabled(false);
                mBatteryBarBatteryLowColor.setEnabled(false);
                mBatteryBarBatteryLowColorWarn.setEnabled(false);
            } else if (Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_USE_GRADIENT_COLOR, 0) == 1) {
                mBatteryBarStyle.setEnabled(true);
                mBatteryBarThickness.setEnabled(true);
                mBatteryBarChargingAnimation.setEnabled(true);
                mBatteryBarColor.setEnabled(false);
                mBatteryBarChargingColor.setEnabled(false);
                mBatteryBarUseGradient.setEnabled(true);
                mBatteryBarBatteryHighColor.setEnabled(true);
                mBatteryBarBatteryLowColor.setEnabled(true);
                mBatteryBarBatteryLowColorWarn.setEnabled(false);
            } else {
                mBatteryBarStyle.setEnabled(true);
                mBatteryBarThickness.setEnabled(true);
                mBatteryBarChargingAnimation.setEnabled(true);
                mBatteryBarColor.setEnabled(true);
                mBatteryBarChargingColor.setEnabled(true);
                mBatteryBarUseGradient.setEnabled(true);
                mBatteryBarBatteryHighColor.setEnabled(true);
                mBatteryBarBatteryLowColor.setEnabled(true);
                mBatteryBarBatteryLowColorWarn.setEnabled(true);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
        }
}
