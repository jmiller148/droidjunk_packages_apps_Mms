/*
 * Copyright (C) 2007-2008 Esmertec AG.
 * Copyright (C) 2007-2008 The Android Open Source Project
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

package com.android.mms.ui;

import java.text.DecimalFormat;

import com.android.mms.MmsApp;
import com.android.mms.MmsConfig;
import com.android.mms.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;

import com.android.mms.util.Recycler;

/**
 * With this activity, users can set preferences for MMS and SMS and
 * can access and manipulate SMS messages stored on the SIM.
 */
public class MessagingPreferenceActivity extends PreferenceActivity
            implements OnPreferenceChangeListener {
    // Symbolic names for the keys used for preference lookup
    public static final String MMS_DELIVERY_REPORT_MODE = "pref_key_mms_delivery_reports";
    public static final String EXPIRY_TIME              = "pref_key_mms_expiry";
    public static final String PRIORITY                 = "pref_key_mms_priority";
    public static final String READ_REPORT_MODE         = "pref_key_mms_read_reports";
    public static final String SMS_DELIVERY_REPORT_MODE = "pref_key_sms_delivery_reports";
    public static final String NOTIFICATION_ENABLED     = "pref_key_enable_notifications";
    public static final String NOTIFICATION_VIBRATE     = "pref_key_vibrate";
    public static final String NOTIFICATION_VIBRATE_WHEN= "pref_key_vibrateWhen";
    public static final String NOTIFICATION_RINGTONE    = "pref_key_ringtone";
    public static final String AUTO_RETRIEVAL           = "pref_key_mms_auto_retrieval";
    public static final String RETRIEVAL_DURING_ROAMING = "pref_key_mms_retrieval_during_roaming";
    public static final String AUTO_DELETE              = "pref_key_auto_delete";

    // Junk
    public static final String MMS_LED_COLOR            = "mms_led_color";
    public static final String MMS_LED_ON_MS            = "mms_led_on_ms";
    public static final String MMS_LED_OFF_MS           = "mms_led_off_ms";
    public static final String MSG_BUBBLE_TYPE		    = "msg_bubble_type";
    public static final String MSG_FILL_PARENT		    = "msg_fill_parent";
    public static final String MSG_USE_CONTACT		    = "msg_use_contact";
    public static final String MSG_LIST_BG_COLOR	    = "msg_list_bg_color";
    public static final String MSG_SHOW_AVATAR		    = "msg_show_avatar";
    public static final String MSG_FULL_DATE		    = "msg_full_date";
    public static final String MSG_IN_BG_COLOR		    = "msg_in_bg_color";
    public static final String MSG_OUT_BG_COLOR		    = "msg_out_bg_color";
    public static final String MSG_IN_CONTACT_COLOR	    = "msg_in_contact_color";
    public static final String MSG_OUT_CONTACT_COLOR	= "msg_out_contact_color";
    public static final String MSG_IN_TEXT_COLOR	    = "msg_in_text_color";
    public static final String MSG_OUT_TEXT_COLOR	    = "msg_out_text_color";
    public static final String MSG_IN_DATE_COLOR	    = "msg_in_date_color";
    public static final String MSG_OUT_DATE_COLOR	    = "msg_out_date_color";
    public static final String MSG_IN_LINK_COLOR	    = "msg_in_link_color";
    public static final String MSG_OUT_LINK_COLOR	    = "msg_out_link_color";
    public static final String MSG_IN_SEARCH_COLOR	    = "msg_in_search_color";
    public static final String MSG_OUT_SEARCH_COLOR	    = "msg_out_search_color";
    
    public static final String CONV_LIST_BG_COLOR	    		= "conv_list_bg_color";
    public static final String READ_CONV_BG_COLOR	    		= "read_conv_bg_color";
    public static final String READ_CONV_CONTACT_COLOR	 	    = "read_conv_contact_color";
    public static final String READ_CONV_COUNT_COLOR	 	    = "read_conv_count_color";
    public static final String READ_CONV_SUBJECT_COLOR	 	    = "read_conv_subject_color";
    public static final String READ_CONV_DATE_COLOR	    		= "read_conv_date_color";
    public static final String READ_CONV_ATTACH_COLOR		    = "read_conv_attach_color";
    public static final String READ_CONV_ERROR_COLOR	    	= "read_conv_error_color";
    public static final String UNREAD_CONV_BG_COLOR			   	= "unread_conv_bg_color";
    public static final String UNREAD_CONV_CONTACT_COLOR	    = "unread_conv_contact_color";
    public static final String UNREAD_CONV_COUNT_COLOR	 	    = "unread_conv_count_color";
    public static final String UNREAD_CONV_SUBJECT_COLOR	    = "unread_conv_subject_color";
    public static final String UNREAD_CONV_DATE_COLOR	    	= "unread_conv_date_color";
    public static final String UNREAD_CONV_ATTACH_COLOR	  		= "unread_conv_attach_color";
    public static final String UNREAD_CONV_ERROR_COLOR	    	= "unread_conv_error_color";
    public static final String SELECTED_CONV_BG_COLOR	    	= "selected_conv_bg_color";
    public static final String SELECTED_CONV_CONTACT_COLOR	    = "selected_conv_contact_color";
    public static final String SELECTED_CONV_COUNT_COLOR	 	    = "selected_conv_count_color";
    public static final String SELECTED_CONV_SUBJECT_COLOR	    = "selected_conv_subject_color";
    public static final String SELECTED_CONV_DATE_COLOR	    	= "selected_conv_date_color";
    public static final String SELECTED_CONV_ATTACH_COLOR	    = "selected_conv_attach_color";
    public static final String SELECTED_CONV_ERROR_COLOR		= "selected_conv_error_color";

    
    
    // Menu entries
    private static final int MENU_RESTORE_DEFAULTS    = 1;

    private Preference mSmsLimitPref;
    private Preference mSmsDeliveryReportPref;
    private Preference mMmsLimitPref;
    private Preference mMmsDeliveryReportPref;
    private Preference mMmsReadReportPref;
    private Preference mManageSimPref;
    private Preference mClearHistoryPref;
    private ListPreference mVibrateWhenPref;
    private CheckBoxPreference mEnableNotificationsPref;
    private Recycler mSmsRecycler;
    private Recycler mMmsRecycler;
    private static final int CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG = 3;
    private CharSequence[] mVibrateEntries;
    private CharSequence[] mVibrateValues;

    
    // Junk
    private Preference mMmsLedColor;
    private Preference mMmsLedOnMs;
    private Preference mMmsLedOffMs;
    private static int        MmsLedColor;
    private static int        MmsLedOnMs;
    private static int        MmsLedOffMs;
    

    
    
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.preferences);

        mManageSimPref = findPreference("pref_key_manage_sim_messages");
        mSmsLimitPref = findPreference("pref_key_sms_delete_limit");
        mSmsDeliveryReportPref = findPreference("pref_key_sms_delivery_reports");
        mMmsDeliveryReportPref = findPreference("pref_key_mms_delivery_reports");
        mMmsReadReportPref = findPreference("pref_key_mms_read_reports");
        mMmsLimitPref = findPreference("pref_key_mms_delete_limit");
        mClearHistoryPref = findPreference("pref_key_mms_clear_history");
        mEnableNotificationsPref = (CheckBoxPreference) findPreference(NOTIFICATION_ENABLED);
        mVibrateWhenPref = (ListPreference) findPreference(NOTIFICATION_VIBRATE_WHEN);
        mVibrateEntries = getResources().getTextArray(R.array.prefEntries_vibrateWhen);
        mVibrateValues = getResources().getTextArray(R.array.prefValues_vibrateWhen);

        
        // Junk
        mMmsLedColor = (Preference) findPreference(MMS_LED_COLOR);
        mMmsLedOnMs = (Preference) findPreference(MMS_LED_ON_MS);
        mMmsLedOffMs = (Preference) findPreference(MMS_LED_OFF_MS);
        
        
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setMessagePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Since the enabled notifications pref can be changed outside of this activity,
        // we have to reload it whenever we resume.
        setEnabledNotificationsPref();
        registerListeners();
    }

    private void setMessagePreferences() {
        if (!MmsApp.getApplication().getTelephonyManager().hasIccCard()) {
            // No SIM card, remove the SIM-related prefs
            PreferenceCategory smsCategory =
                (PreferenceCategory)findPreference("pref_key_sms_settings");
            smsCategory.removePreference(mManageSimPref);
        }

        if (!MmsConfig.getSMSDeliveryReportsEnabled()) {
            PreferenceCategory smsCategory =
                (PreferenceCategory)findPreference("pref_key_sms_settings");
            smsCategory.removePreference(mSmsDeliveryReportPref);
            if (!MmsApp.getApplication().getTelephonyManager().hasIccCard()) {
                getPreferenceScreen().removePreference(smsCategory);
            }
        }

        if (!MmsConfig.getMmsEnabled()) {
            // No Mms, remove all the mms-related preferences
            PreferenceCategory mmsOptions =
                (PreferenceCategory)findPreference("pref_key_mms_settings");
            getPreferenceScreen().removePreference(mmsOptions);

            PreferenceCategory storageOptions =
                (PreferenceCategory)findPreference("pref_key_storage_settings");
            storageOptions.removePreference(findPreference("pref_key_mms_delete_limit"));
        } else {
            if (!MmsConfig.getMMSDeliveryReportsEnabled()) {
                PreferenceCategory mmsOptions =
                    (PreferenceCategory)findPreference("pref_key_mms_settings");
                mmsOptions.removePreference(mMmsDeliveryReportPref);
            }
            if (!MmsConfig.getMMSReadReportsEnabled()) {
                PreferenceCategory mmsOptions =
                    (PreferenceCategory)findPreference("pref_key_mms_settings");
                mmsOptions.removePreference(mMmsReadReportPref);
            }
        }

        setEnabledNotificationsPref();

        // If needed, migrate vibration setting from a previous version
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(NOTIFICATION_VIBRATE_WHEN) &&
                sharedPreferences.contains(NOTIFICATION_VIBRATE)) {
            int stringId = sharedPreferences.getBoolean(NOTIFICATION_VIBRATE, false) ?
                    R.string.prefDefault_vibrate_true :
                    R.string.prefDefault_vibrate_false;
            mVibrateWhenPref.setValue(getString(stringId));
        }

        mSmsRecycler = Recycler.getSmsRecycler();
        mMmsRecycler = Recycler.getMmsRecycler();

        // Fix up the recycler's summary with the correct values
        setSmsDisplayLimit();
        setMmsDisplayLimit();
        adjustVibrateSummary(mVibrateWhenPref.getValue());
        
        MmsLedColor = sharedPreferences.getInt(MMS_LED_COLOR, 0xff00ff00);
        MmsLedOnMs = sharedPreferences.getInt(MMS_LED_ON_MS, 10);
        MmsLedOffMs = sharedPreferences.getInt(MMS_LED_OFF_MS, 10);

    }
    

    private void setEnabledNotificationsPref() {
        // The "enable notifications" setting is really stored in our own prefs. Read the
        // current value and set the checkbox to match.
        mEnableNotificationsPref.setChecked(getNotificationEnabled(this));
    }

    private void setSmsDisplayLimit() {
        mSmsLimitPref.setSummary(
                getString(R.string.pref_summary_delete_limit,
                        mSmsRecycler.getMessageLimit(this)));
    }

    private void setMmsDisplayLimit() {
        mMmsLimitPref.setSummary(
                getString(R.string.pref_summary_delete_limit,
                        mMmsRecycler.getMessageLimit(this)));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        menu.add(0, MENU_RESTORE_DEFAULTS, 0, R.string.restore_default);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESTORE_DEFAULTS:
                restoreDefaultPreferences();
                return true;

            case android.R.id.home:
                // The user clicked on the Messaging icon in the action bar. Take them back from
                // wherever they came from
                finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mSmsLimitPref) {
            new NumberPickerDialog(this,
                    mSmsLimitListener,
                    mSmsRecycler.getMessageLimit(this),
                    mSmsRecycler.getMessageMinLimit(),
                    mSmsRecycler.getMessageMaxLimit(),
                    R.string.pref_title_sms_delete).show();
        } else if (preference == mMmsLimitPref) {
            new NumberPickerDialog(this,
                    mMmsLimitListener,
                    mMmsRecycler.getMessageLimit(this),
                    mMmsRecycler.getMessageMinLimit(),
                    mMmsRecycler.getMessageMaxLimit(),
                    R.string.pref_title_mms_delete).show();
        } else if (preference == mMmsLedOnMs) {
            new NumberPickerDialog(this,
                    mMmsLedOnListener,
                    MmsLedOnMs,
                    1,
                    50,
                    R.string.mms_led_on_ms).show();
        } else if (preference == mMmsLedOffMs) {
            new NumberPickerDialog(this,
                    mMmsLedOffListener,
                    MmsLedOffMs,
                    1,
                    50,
                    R.string.mms_led_off_ms).show();
        
            
        } else if (preference == mManageSimPref) {
            startActivity(new Intent(this, ManageSimMessages.class));
        } else if (preference == mClearHistoryPref) {
            showDialog(CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG);
            return true;
        } else if (preference == mEnableNotificationsPref) {
            // Update the actual "enable notifications" value that is stored in secure settings.
            enableNotifications(mEnableNotificationsPref.isChecked(), this);
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    private void restoreDefaultPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().clear().apply();
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
        setMessagePreferences();
    }

    NumberPickerDialog.OnNumberSetListener mSmsLimitListener =
        new NumberPickerDialog.OnNumberSetListener() {
            public void onNumberSet(int limit) {
                mSmsRecycler.setMessageLimit(MessagingPreferenceActivity.this, limit);
                setSmsDisplayLimit();
            }
    };

    NumberPickerDialog.OnNumberSetListener mMmsLimitListener =
        new NumberPickerDialog.OnNumberSetListener() {
            public void onNumberSet(int limit) {
                mMmsRecycler.setMessageLimit(MessagingPreferenceActivity.this, limit);
                setMmsDisplayLimit();
            }
    };

    NumberPickerDialog.OnNumberSetListener mMmsLedOnListener =
            new NumberPickerDialog.OnNumberSetListener() {
                public void onNumberSet(int limit) {
                    SharedPreferences.Editor editor =
                            PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    editor.putInt(MessagingPreferenceActivity.MMS_LED_ON_MS, limit);
                    editor.apply();
                    DecimalFormat numf = new DecimalFormat("#.##");
                    double mTime = ((double) (limit) / (double)(10));
                    mMmsLedOnMs.setSummary(numf.format(mTime)+ " seconds");
                    MmsLedOnMs = limit;
                }
        };
    
        NumberPickerDialog.OnNumberSetListener mMmsLedOffListener =
                new NumberPickerDialog.OnNumberSetListener() {
                    public void onNumberSet(int limit) {
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();

                        editor.putInt(MessagingPreferenceActivity.MMS_LED_OFF_MS, limit);
                        editor.apply();
                        DecimalFormat numf = new DecimalFormat("#.##");
                        double mTime = ((double) (limit) / (double)(10));
                        mMmsLedOffMs.setSummary(numf.format(mTime) + " seconds");
                        MmsLedOffMs = limit;
                    }
            };
    
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG:
                return new AlertDialog.Builder(MessagingPreferenceActivity.this)
                    .setTitle(R.string.confirm_clear_search_title)
                    .setMessage(R.string.confirm_clear_search_text)
                    .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SearchRecentSuggestions recent =
                                ((MmsApp)getApplication()).getRecentSuggestions();
                            if (recent != null) {
                                recent.clearHistory();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create();
        }
        return super.onCreateDialog(id);
    }

    public static boolean getNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationsEnabled =
            prefs.getBoolean(MessagingPreferenceActivity.NOTIFICATION_ENABLED, true);
        return notificationsEnabled;
    }

    public static void enableNotifications(boolean enabled, Context context) {
        // Store the value of notifications in SharedPreferences
        SharedPreferences.Editor editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean(MessagingPreferenceActivity.NOTIFICATION_ENABLED, enabled);

        editor.apply();
    }


    private void registerListeners() {
        mVibrateWhenPref.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;
        if (preference == mVibrateWhenPref) {
            adjustVibrateSummary((String)newValue);
            result = true;
        }
        return result;
    }

    private void adjustVibrateSummary(String value) {
        int len = mVibrateValues.length;
        for (int i = 0; i < len; i++) {
            if (mVibrateValues[i].equals(value)) {
                mVibrateWhenPref.setSummary(mVibrateEntries[i]);
                return;
            }
        }
        mVibrateWhenPref.setSummary(null);
    }

    

}
