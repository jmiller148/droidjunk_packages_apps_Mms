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

import java.io.File;
import java.io.IOException;
import com.android.mms.MmsApp;
import com.android.mms.MmsConfig;
import com.android.mms.util.BackupUtils;
import com.android.mms.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ColorPickerPreference;
import android.preference.DJSeekBarPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.mms.util.AssetUtils;
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
	private final String BACKUP_MMS = "backup_mms";
	private final String RESTORE_MMS = "restore_mms";

    public static final String MMS_ASSETS_COPIED        = "mms_assets_copied";
    public static final String MMS_LED_COLOR            = "mms_led_color";
    public static final String MMS_LED_ON_MS            = "mms_led_on_ms";
    public static final String MMS_LED_OFF_MS           = "mms_led_off_ms";
    public static final String MSG_TEXT_SIZE		    = "msg_text_size";
    public static final String MSG_PRESET_COLORS	    = "msg_preset_colors";
    public static final String MSG_BUBBLE_TYPE		    = "msg_bubble_type";
    public static final String MSG_FILL_PARENT		    = "msg_fill_parent";
    public static final String MSG_USE_CONTACT		    = "msg_use_contact";
    public static final String MSG_LIST_BG_COLOR	    = "msg_list_bg_color";
	public static final String MSG_DIVIDER_HEIGHT	    = "msg_divider_height";
    public static final String MSG_SHOW_AVATAR		    = "msg_show_avatar";
    public static final String MSG_FULL_DATE		    = "msg_full_date";
    public static final String MSG_IN_BG_COLOR		    = "msg_in_bg_color";
    public static final String MSG_OUT_BG_COLOR		    = "msg_out_bg_color";
    public static final String MSG_USE_SMILEY		    = "msg_use_smiley";
    public static final String MSG_IN_SMILEY_COLOR	    = "msg_in_smiley_color";
    public static final String MSG_OUT_SMILEY_COLOR	    = "msg_out_smiley_color";
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
    public static final String SELECTED_CONV_COUNT_COLOR	    = "selected_conv_count_color";
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
    private DJSeekBarPreference mMmsLedOnMs;
    private DJSeekBarPreference mMmsLedOffMs;
    private ListPreference mPresetColors;
    private DJSeekBarPreference mMsgTextSize;
    private DJSeekBarPreference mDividerHeight;
	Preference mBackupMmsSettings;
	Preference mRestoreMmsSettings;
    private SharedPreferences sp;

    private String mMsgBType;
    private boolean mMsgStretch;
    private boolean mMsgSmiley;
    private boolean mMsgAvatar;
    private boolean mMsgContact;
    private boolean mMsgDate;
    private int mMsgLedColor;
    private int mMsgLedOn;
    private int mMsgLedOff;
    private int mMsgTxtSize;
    private int mMsgDivHeight;
    private int mMsgListBgColor;
    private int mMsgInBgColor;
    private int mMsgOutBgColor;
    private int mMsgInSmileyColor;
    private int mMsgOutSmileyColor;
    private int mMsgInContactColor;
    private int mMsgOutContactColor;
    private int mMsgInTextColor;
    private int mMsgOutTextColor;
    private int mMsgInDateColor;
    private int mMsgOutDateColor;
    private int mMsgInLinkColor;
    private int mMsgOutLinkColor;
    private int mMsgInSearchColor;
    private int mMsgOutSearchColor;
    private int mConvListBgColor;
    private int mConvReadBgColor;
    private int mConvReadContactColor;
    private int mConvReadCountColor;
    private int mConvReadSubjectColor;
    private int mConvReadDateColor;
    private int mConvReadAttachColor;
    private int mConvReadErrorColor;
    private int mConvUnReadBgColor;
    private int mConvUnReadContactColor;
    private int mConvUnReadCountColor;
    private int mConvUnReadSubjectColor;
    private int mConvUnReadDateColor;
    private int mConvUnReadAttachColor;
    private int mConvUnReadErrorColor;
    private int mConvSelectedBgColor;
    private int mConvSelectedContactColor;
    private int mConvSelectedCountColor;
    private int mConvSelectedSubjectColor;
    private int mConvSelectedDateColor;
    private int mConvSelectedAttachColor;
    private int mConvSelectedErrorColor;
    
    
    
    
    
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
        File junkBackupDir = new File("/sdcard/.junk/backup/");
        junkBackupDir.mkdirs(); 

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        mPresetColors = (ListPreference) findPreference(MSG_PRESET_COLORS);
        mPresetColors.setOnPreferenceChangeListener(this);

        mMmsLedOnMs = (DJSeekBarPreference) findPreference(MMS_LED_ON_MS);
        mMmsLedOnMs.setMax(50);
        mMmsLedOnMs.setProgress(sp.getInt(MMS_LED_ON_MS, 0));

        mMmsLedOffMs = (DJSeekBarPreference) findPreference(MMS_LED_OFF_MS);
        mMmsLedOffMs.setMax(50);
        mMmsLedOffMs.setProgress(sp.getInt(MMS_LED_OFF_MS, 0));

        mMsgTextSize = (DJSeekBarPreference) findPreference(MSG_TEXT_SIZE);
        mMsgTextSize.setMax(24);
        mMsgTextSize.setMin(12);
        mMsgTextSize.setProgress(sp.getInt(MSG_TEXT_SIZE, 14 ));

        mDividerHeight = (DJSeekBarPreference) findPreference(MSG_DIVIDER_HEIGHT);
        mDividerHeight.setMax(100);
        mDividerHeight.setProgress(sp.getInt(MSG_DIVIDER_HEIGHT, 0));

        mBackupMmsSettings = (Preference) findPreference(BACKUP_MMS);
		mBackupMmsSettings.setOnPreferenceChangeListener(this);	

		mRestoreMmsSettings = (Preference) findPreference(RESTORE_MMS);
		mRestoreMmsSettings.setOnPreferenceChangeListener(this);
		
       
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setMessagePreferences();
        
        	boolean copied = false;
        	try {
 				copied = AssetUtils.copyAsset(getBaseContext(), "Junk_StockLike.xml",
 						"data/data/com.android.mms/shared_prefs/Junk_StockLike.xml");
 				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean(MMS_ASSETS_COPIED, copied);
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}

        	try {
 				copied = AssetUtils.copyAsset(getBaseContext(), "Junk_Blue.xml",
 						"data/data/com.android.mms/shared_prefs/Junk_Blue.xml");
 				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean(MMS_ASSETS_COPIED, copied);
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}

        	try {
 				copied = AssetUtils.copyAsset(getBaseContext(), "Junk_Grey.xml",
 						"data/data/com.android.mms/shared_prefs/Junk_Grey.xml");
 				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean(MMS_ASSETS_COPIED, copied);
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
 
        	try {
 				copied = AssetUtils.copyAsset(getBaseContext(), "Junk_Red.xml",
 						"data/data/com.android.mms/shared_prefs/Junk_Red.xml");
 				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean(MMS_ASSETS_COPIED, copied);
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}

        	try {
 				copied = AssetUtils.copyAsset(getBaseContext(), "Junk_DroidJunk.xml",
 						"data/data/com.android.mms/shared_prefs/Junk_DroidJunk.xml");
 				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean(MMS_ASSETS_COPIED, copied);
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}

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

        if (!sp.contains(NOTIFICATION_VIBRATE_WHEN) &&
                sp.contains(NOTIFICATION_VIBRATE)) {
            int stringId = sp.getBoolean(NOTIFICATION_VIBRATE, false) ?
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

        } else if (preference == mBackupMmsSettings) {
    		BackupMmsDialog();
    	} else if (preference == mRestoreMmsSettings) {
    		RestoreMmsDialog();
 
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


    private void BackupMmsDialog()	{
		
    	Builder alertDialog = new AlertDialog.Builder(getPreferenceScreen().getContext());
    	alertDialog.setTitle("Backup Mms Settings");
    	alertDialog.setMessage("Backup Mms settings?");
    	alertDialog.setNegativeButton("Cancel", null);
    	alertDialog.setPositiveButton("Backup", backupMmsDialogPositiveListener);
    	alertDialog.show();
    }

    private void RestoreMmsDialog()	{
		
    	Builder alertDialog = new AlertDialog.Builder(getPreferenceScreen().getContext());
    	alertDialog.setTitle("Restore Mms Settings");
    	alertDialog.setMessage("Restore Mms settings?");
    	alertDialog.setNegativeButton("Cancel", null);
    	alertDialog.setPositiveButton("Restore", restoreMmsDialogPositiveListener);
    	alertDialog.show();
    }    
    
    
    DialogInterface.OnClickListener backupMmsDialogPositiveListener =
            new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
			        try {
			        	if (BackupUtils.settingsExist()) {
			        		Log.e("JUNK: ","EXISTS");
			        		BackupUtils.copyBackup("/data/data/com.android.mms/shared_prefs/com.android.mms_preferences.xml",
			        				"/sdcard/.junk/backup/com.android.mms_preferences.xml");
			        		Toast.makeText(getBaseContext(), "Backup Successful", Toast.LENGTH_SHORT).show();
			        	} else {
			        		Log.e("JUNK: ","DOES NOT EXISTS");
			        	}
			        	
			        } catch (IOException e) {
			        	Toast.makeText(getBaseContext(), "ERROR Backing up Mms settings", Toast.LENGTH_SHORT).show();
			        	Log.e("JUNK: ","ERROR BACKING UP MMS SETTINGS");
			        };	
				}
			};
    
    DialogInterface.OnClickListener restoreMmsDialogPositiveListener =
            new DialogInterface.OnClickListener() {
						
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
			        try {
			        	if (BackupUtils.backupExist(
			        			"sdcard/.junk/backup/com.android.mms_preferences.xml")) {
			        		BackupUtils.copyBackup("sdcard/.junk/backup/com.android.mms_preferences.xml",
									"data/data/com.android.mms/shared_prefs/com.android.mms_preferences_temp.xml");
			        		onPreferenceChange(mRestoreMmsSettings,null);
			        		Toast.makeText(getBaseContext(), "Restore Successful", Toast.LENGTH_SHORT).show();
			        		
			        		
			        	} else {
			        		Toast.makeText(getBaseContext(), "No backup exists!", Toast.LENGTH_SHORT).show();	
			        	}
			        } catch (IOException e) {
			        	Log.e("JUNK: ","ERROR RESTORING MMS SETTINGS");
			        };
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
        } else if (preference == mPresetColors) {
            sp = getSharedPreferences("Junk_" + (String) newValue, Context.MODE_WORLD_READABLE);
            getValues();
            writeValues();
            
        } else if (preference == mRestoreMmsSettings) {
            sp = getSharedPreferences("com.android.mms_preferences_temp", Context.MODE_WORLD_READABLE);
            getValues();
            writeValues();
        	
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

    private void getValues() {
    	mMsgBType = sp.getString(MSG_BUBBLE_TYPE, "BubbleCall");
    	mMsgStretch = sp.getBoolean(MSG_FILL_PARENT, false);
    	mMsgSmiley = sp.getBoolean(MSG_USE_SMILEY, true);
    	mMsgAvatar = sp.getBoolean(MSG_SHOW_AVATAR, false);
    	mMsgContact = sp.getBoolean(MSG_USE_CONTACT, false);
    	mMsgDate = sp.getBoolean(MSG_FULL_DATE, false);
    	mMsgLedColor = sp.getInt(MMS_LED_COLOR, 0xff00ff00);
    	mMsgLedOn = sp.getInt(MMS_LED_ON_MS, 2);
    	mMsgLedOff = sp.getInt(MMS_LED_OFF_MS, 2);
    	mMsgTxtSize = sp.getInt(MSG_TEXT_SIZE, 14);
    	mMsgListBgColor = sp.getInt(MSG_LIST_BG_COLOR, 0xffffffff);
    	mMsgDivHeight = sp.getInt(MSG_DIVIDER_HEIGHT, 0);
        mMsgInBgColor = sp.getInt(MSG_IN_BG_COLOR, 0xff008ec2);
        mMsgOutBgColor = sp.getInt(MSG_OUT_BG_COLOR, 0xff33b5e5);
        mMsgInSmileyColor = sp.getInt(MSG_IN_SMILEY_COLOR, 0xffffffff);
        mMsgOutSmileyColor = sp.getInt(MSG_OUT_SMILEY_COLOR, 0xffffffff);
        mMsgInContactColor = sp.getInt(MSG_IN_CONTACT_COLOR, 0xffffff);
        mMsgOutContactColor = sp.getInt(MSG_OUT_CONTACT_COLOR, 0xffffffff);
        mMsgInTextColor = sp.getInt(MSG_IN_TEXT_COLOR, 0xffcecece);
        mMsgOutTextColor = sp.getInt(MSG_OUT_TEXT_COLOR, 0xffd6d6d6);
        mMsgInDateColor = sp.getInt(MSG_IN_DATE_COLOR, 0xcdcfcfcf);
        mMsgOutDateColor = sp.getInt(MSG_OUT_DATE_COLOR, 0xcdffffff);
        mMsgInLinkColor = sp.getInt(MSG_IN_LINK_COLOR, 0xffffffff);
        mMsgOutLinkColor = sp.getInt(MSG_OUT_LINK_COLOR, 0xffffffff);
        mMsgInSearchColor = sp.getInt(MSG_IN_SEARCH_COLOR, 0xffffffff);
        mMsgOutSearchColor = sp.getInt(MSG_OUT_SEARCH_COLOR, 0xffffffff);
        mConvListBgColor = sp.getInt(CONV_LIST_BG_COLOR, 0xff000000);
        mConvReadBgColor = sp.getInt(READ_CONV_BG_COLOR, 0xff4e4e4e);
        mConvReadContactColor = sp.getInt(READ_CONV_CONTACT_COLOR, 0xffc4c4c4);
        mConvReadCountColor = sp.getInt(READ_CONV_COUNT_COLOR, 0xffe2e2e2);
        mConvReadSubjectColor = sp.getInt(READ_CONV_SUBJECT_COLOR, 0xffb2b2b2);
        mConvReadDateColor = sp.getInt(READ_CONV_DATE_COLOR, 0xff4b4b4b);
        mConvReadAttachColor = sp.getInt(READ_CONV_ATTACH_COLOR, 0xffdbdbdb);
        mConvReadErrorColor = sp.getInt(READ_CONV_ERROR_COLOR, 0xffdbdbdb);
        mConvUnReadBgColor = sp.getInt(UNREAD_CONV_BG_COLOR, 0xffd8d8d8);
        mConvUnReadContactColor = sp.getInt(UNREAD_CONV_CONTACT_COLOR, 0xff000000);
        mConvUnReadCountColor = sp.getInt(UNREAD_CONV_COUNT_COLOR, 0xff33b5e5);
        mConvUnReadSubjectColor = sp.getInt(UNREAD_CONV_SUBJECT_COLOR, 0xff424242);
        mConvUnReadDateColor = sp.getInt(UNREAD_CONV_DATE_COLOR, 0xff363636);
        mConvUnReadAttachColor = sp.getInt(UNREAD_CONV_ATTACH_COLOR, 0xffffffff);
        mConvUnReadErrorColor = sp.getInt(UNREAD_CONV_ERROR_COLOR, 0xffdbdbdb);
        mConvSelectedBgColor = sp.getInt(SELECTED_CONV_BG_COLOR, 0xff33b5e5);
        mConvSelectedContactColor = sp.getInt(SELECTED_CONV_CONTACT_COLOR, 0xffffffff);
        mConvSelectedCountColor = sp.getInt(SELECTED_CONV_COUNT_COLOR, 0xff000000);
        mConvSelectedSubjectColor = sp.getInt(SELECTED_CONV_SUBJECT_COLOR, 0xff3c3c3c);
        mConvSelectedDateColor = sp.getInt(SELECTED_CONV_DATE_COLOR, 0xff4b4b4b);
        mConvSelectedAttachColor = sp.getInt(SELECTED_CONV_ATTACH_COLOR, 0xff4f4f4f);
        mConvSelectedErrorColor = sp.getInt(SELECTED_CONV_ERROR_COLOR, 0xffdbdbdb);
    }
    
    private void writeValues() {
    	
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences.Editor editor = sp.edit();
    	editor.putBoolean(MSG_FILL_PARENT, mMsgStretch);
    	editor.putBoolean(MSG_USE_SMILEY, mMsgSmiley);
    	editor.putBoolean(MSG_SHOW_AVATAR, mMsgAvatar);
    	editor.putBoolean(MSG_USE_CONTACT, mMsgContact);
    	editor.putBoolean(MSG_FULL_DATE, mMsgDate);
    	editor.putInt(MMS_LED_COLOR, mMsgLedColor);
    	editor.putInt(MMS_LED_COLOR, mMsgLedColor);
    	editor.putInt(MMS_LED_ON_MS, mMsgLedOn);
    	editor.putInt(MMS_LED_OFF_MS, mMsgLedOff);
    	editor.putInt(MSG_TEXT_SIZE, mMsgTxtSize);
    	editor.putInt(MSG_DIVIDER_HEIGHT, mMsgDivHeight);
    	editor.putInt(MSG_LIST_BG_COLOR, mMsgListBgColor);
    	editor.putInt(MSG_IN_BG_COLOR, mMsgInBgColor);
    	editor.putInt(MSG_OUT_BG_COLOR, mMsgOutBgColor);
    	editor.putInt(MSG_IN_SMILEY_COLOR, mMsgInSmileyColor);
    	editor.putInt(MSG_OUT_SMILEY_COLOR, mMsgOutSmileyColor);
    	editor.putInt(MSG_IN_CONTACT_COLOR, mMsgInContactColor);
    	editor.putInt(MSG_OUT_CONTACT_COLOR, mMsgOutContactColor);
    	editor.putInt(MSG_IN_TEXT_COLOR, mMsgInTextColor);
    	editor.putInt(MSG_OUT_TEXT_COLOR, mMsgOutTextColor);
    	editor.putInt(MSG_IN_DATE_COLOR, mMsgInDateColor);
    	editor.putInt(MSG_OUT_DATE_COLOR, mMsgOutDateColor);
    	editor.putInt(MSG_IN_LINK_COLOR, mMsgInLinkColor);
    	editor.putInt(MSG_OUT_LINK_COLOR, mMsgOutLinkColor);
    	editor.putInt(MSG_IN_SEARCH_COLOR, mMsgInSearchColor);
    	editor.putInt(MSG_OUT_SEARCH_COLOR, mMsgOutSearchColor);
    	editor.putInt(CONV_LIST_BG_COLOR, mConvListBgColor);
    	editor.putInt(READ_CONV_BG_COLOR, mConvReadBgColor);
    	editor.putInt(READ_CONV_CONTACT_COLOR, mConvReadContactColor);
    	editor.putInt(READ_CONV_COUNT_COLOR, mConvReadCountColor);
    	editor.putInt(READ_CONV_SUBJECT_COLOR, mConvReadSubjectColor);
    	editor.putInt(READ_CONV_DATE_COLOR, mConvReadDateColor);
    	editor.putInt(READ_CONV_ATTACH_COLOR, mConvReadAttachColor);
    	editor.putInt(READ_CONV_ERROR_COLOR, mConvReadErrorColor);
    	editor.putInt(UNREAD_CONV_BG_COLOR, mConvUnReadBgColor);
    	editor.putInt(UNREAD_CONV_CONTACT_COLOR, mConvUnReadContactColor);
    	editor.putInt(UNREAD_CONV_COUNT_COLOR, mConvUnReadCountColor);
    	editor.putInt(UNREAD_CONV_SUBJECT_COLOR, mConvUnReadSubjectColor);
    	editor.putInt(UNREAD_CONV_DATE_COLOR, mConvUnReadDateColor);
    	editor.putInt(UNREAD_CONV_ATTACH_COLOR, mConvUnReadAttachColor);
    	editor.putInt(UNREAD_CONV_ERROR_COLOR, mConvUnReadErrorColor);
    	editor.putInt(SELECTED_CONV_BG_COLOR, mConvSelectedBgColor);
    	editor.putInt(SELECTED_CONV_CONTACT_COLOR, mConvSelectedContactColor);
    	editor.putInt(SELECTED_CONV_COUNT_COLOR, mConvSelectedCountColor);
    	editor.putInt(SELECTED_CONV_SUBJECT_COLOR, mConvSelectedSubjectColor);
    	editor.putInt(SELECTED_CONV_DATE_COLOR, mConvSelectedDateColor);
    	editor.putInt(SELECTED_CONV_ATTACH_COLOR, mConvSelectedAttachColor);
    	editor.putInt(SELECTED_CONV_ERROR_COLOR, mConvSelectedErrorColor);
    	editor.commit();
    }

}
