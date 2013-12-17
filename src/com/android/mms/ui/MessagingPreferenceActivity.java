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
import com.android.mms.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SeekBarPreference;
import android.provider.SearchRecentSuggestions;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.mms.transaction.TransactionService;
import com.android.mms.util.AssetUtils;
import com.android.mms.util.BackupUtils;
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
    public static final String GROUP_MMS_MODE           = "pref_key_mms_group_mms";

    // Junk
	private final String BACKUP_MMS = "backup_mms";
	private final String RESTORE_MMS = "restore_mms";
	public static final String MSG_SIGNATURE	        = "msg_signature";
    public static final String MMS_ASSETS_COPIED        = "mms_assets_copied";
    public static final String MSG_TEXT_SIZE		    = "msg_text_size";
    public static final String MSG_PRESET_COLORS	    = "msg_preset_colors";
    public static final String MSG_BUBBLE_TYPE		    = "msg_bubble_type";
    public static final String MSG_SHOW_FRAME		    = "msg_show_frame";
    public static final String MSG_IN_FRAME_COLOR		= "msg_in_frame_color";
    public static final String MSG_OUT_FRAME_COLOR		= "msg_out_frame_color";
    public static final String MSG_FILL_PARENT		    = "msg_fill_parent";
    public static final String MSG_USE_CONTACT		    = "msg_use_contact";
    public static final String MSG_LIST_BG_COLOR	    = "msg_list_bg_color";
	public static final String MSG_DIVIDER_HEIGHT	    = "msg_divider_height";
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
    public static final String SELECTED_CONV_COUNT_COLOR	    = "selected_conv_count_color";
    public static final String SELECTED_CONV_SUBJECT_COLOR	    = "selected_conv_subject_color";
    public static final String SELECTED_CONV_DATE_COLOR	    	= "selected_conv_date_color";
    public static final String SELECTED_CONV_ATTACH_COLOR	    = "selected_conv_attach_color";
    public static final String SELECTED_CONV_ERROR_COLOR		= "selected_conv_error_color";
	// End Junk
    
    
    // Menu entries
    private static final int MENU_RESTORE_DEFAULTS    = 1;

    // Preferences for enabling and disabling SMS
    private Preference mSmsDisabledPref;
    private Preference mSmsEnabledPref;

    private PreferenceCategory mStoragePrefCategory;
    private PreferenceCategory mSmsPrefCategory;
    private PreferenceCategory mMmsPrefCategory;
    private PreferenceCategory mNotificationPrefCategory;

    private Preference mSmsLimitPref;
    private Preference mSmsDeliveryReportPref;
    private Preference mMmsLimitPref;
    private Preference mMmsDeliveryReportPref;
    private Preference mMmsGroupMmsPref;
    private Preference mMmsReadReportPref;
    private Preference mManageSimPref;
    private Preference mClearHistoryPref;
    private CheckBoxPreference mVibratePref;
    private CheckBoxPreference mEnableNotificationsPref;
    private CheckBoxPreference mMmsAutoRetrievialPref;
    private RingtonePreference mRingtonePref;
    private Recycler mSmsRecycler;
    private Recycler mMmsRecycler;
    private static final int CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG = 3;

    // Whether or not we are currently enabled for SMS. This field is updated in onResume to make
    // sure we notice if the user has changed the default SMS app.
    private boolean mIsSmsEnabled;

    
    // Junk
    private ListPreference mPresetColors;
    private EditTextPreference mSignature;
    private SeekBarPreference mMsgTextSize;
    private SeekBarPreference mDividerHeight;
	Preference mBackupMmsSettings;
	Preference mRestoreMmsSettings;
    private SharedPreferences sp;

    private String mSignatureText;
    private String mMsgBType;
    private boolean mMsgShowFrame;
    private int mMsgInFrameColor;
    private int mMsgOutFrameColor;
    private boolean mMsgStretch;
    private boolean mMsgAvatar;
    private boolean mMsgContact;
    private boolean mMsgDate;
    private int mMsgTxtSize;
    private int mMsgDivHeight;
    private int mMsgListBgColor;
    private int mMsgInBgColor;
    private int mMsgOutBgColor;
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
    // End Junk
    
    
    
    

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        loadPrefs();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isSmsEnabled = MmsConfig.isSmsEnabled(this);
        if (isSmsEnabled != mIsSmsEnabled) {
            mIsSmsEnabled = isSmsEnabled;
            invalidateOptionsMenu();
        }

        // Since the enabled notifications pref can be changed outside of this activity,
        // we have to reload it whenever we resume.
        setEnabledNotificationsPref();
        registerListeners();
        updateSmsEnabledState();
    }

    private void updateSmsEnabledState() {
        // Show the right pref (SMS Disabled or SMS Enabled)
        PreferenceScreen prefRoot = (PreferenceScreen)findPreference("pref_key_root");
        if (!mIsSmsEnabled) {
            prefRoot.addPreference(mSmsDisabledPref);
            prefRoot.removePreference(mSmsEnabledPref);
        } else {
            prefRoot.removePreference(mSmsDisabledPref);
            prefRoot.addPreference(mSmsEnabledPref);
        }

        // Enable or Disable the settings as appropriate
        mStoragePrefCategory.setEnabled(mIsSmsEnabled);
        mSmsPrefCategory.setEnabled(mIsSmsEnabled);
        mMmsPrefCategory.setEnabled(mIsSmsEnabled);
        mNotificationPrefCategory.setEnabled(mIsSmsEnabled);
    }

    private void loadPrefs() {
        addPreferencesFromResource(R.xml.preferences);

        mSmsDisabledPref = findPreference("pref_key_sms_disabled");
        mSmsEnabledPref = findPreference("pref_key_sms_enabled");

        mStoragePrefCategory = (PreferenceCategory)findPreference("pref_key_storage_settings");
        mSmsPrefCategory = (PreferenceCategory)findPreference("pref_key_sms_settings");
        mMmsPrefCategory = (PreferenceCategory)findPreference("pref_key_mms_settings");
        mNotificationPrefCategory =
                (PreferenceCategory)findPreference("pref_key_notification_settings");

        mManageSimPref = findPreference("pref_key_manage_sim_messages");
        mSmsLimitPref = findPreference("pref_key_sms_delete_limit");
        mSmsDeliveryReportPref = findPreference("pref_key_sms_delivery_reports");
        mMmsDeliveryReportPref = findPreference("pref_key_mms_delivery_reports");
        mMmsGroupMmsPref = findPreference("pref_key_mms_group_mms");
        mMmsReadReportPref = findPreference("pref_key_mms_read_reports");
        mMmsLimitPref = findPreference("pref_key_mms_delete_limit");
        mClearHistoryPref = findPreference("pref_key_mms_clear_history");
        mEnableNotificationsPref = (CheckBoxPreference) findPreference(NOTIFICATION_ENABLED);

        mMmsAutoRetrievialPref = (CheckBoxPreference) findPreference(AUTO_RETRIEVAL);
        mVibratePref = (CheckBoxPreference) findPreference(NOTIFICATION_VIBRATE);
        mRingtonePref = (RingtonePreference) findPreference(NOTIFICATION_RINGTONE);


        
        // Junk
        File junkBackupDir = new File("/sdcard/.junk/backup/");
        junkBackupDir.mkdirs(); 

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        
        mSignature = (EditTextPreference) findPreference(MSG_SIGNATURE);
        mSignature.setOnPreferenceChangeListener(this);
        mSignature.setText(sp.getString(MSG_SIGNATURE,""));
        
        
        mPresetColors = (ListPreference) findPreference(MSG_PRESET_COLORS);
        mPresetColors.setOnPreferenceChangeListener(this);


        mMsgTextSize = (SeekBarPreference) findPreference(MSG_TEXT_SIZE);
        mMsgTextSize.setMax(24);
        mMsgTextSize.setProgress(sp.getInt(MSG_TEXT_SIZE, 14 ));

        mDividerHeight = (SeekBarPreference) findPreference(MSG_DIVIDER_HEIGHT);
        mDividerHeight.setMax(100);
        mDividerHeight.setProgress(sp.getInt(MSG_DIVIDER_HEIGHT, 0));

        mBackupMmsSettings = (Preference) findPreference(BACKUP_MMS);
		mBackupMmsSettings.setOnPreferenceChangeListener(this);	

		mRestoreMmsSettings = (Preference) findPreference(RESTORE_MMS);
		mRestoreMmsSettings.setOnPreferenceChangeListener(this);
		// End Junk
       
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        setMessagePreferences();
        
		// Junk        
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
		// End Junk

    }

    private void restoreDefaultPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        setPreferenceScreen(null);
        loadPrefs();
        updateSmsEnabledState();

        // NOTE: After restoring preferences, the auto delete function (i.e. message recycler)
        // will be turned off by default. However, we really want the default to be turned on.
        // Because all the prefs are cleared, that'll cause:
        // ConversationList.runOneTimeStorageLimitCheckForLegacyMessages to get executed the
        // next time the user runs the Messaging app and it will either turn on the setting
        // by default, or if the user is over the limits, encourage them to turn on the setting
        // manually.
    }

    private void setMessagePreferences() {
        if (!MmsApp.getApplication().getTelephonyManager().hasIccCard()) {
            // No SIM card, remove the SIM-related prefs
            mSmsPrefCategory.removePreference(mManageSimPref);
        }

        if (!MmsConfig.getSMSDeliveryReportsEnabled()) {
            mSmsPrefCategory.removePreference(mSmsDeliveryReportPref);
            if (!MmsApp.getApplication().getTelephonyManager().hasIccCard()) {
                getPreferenceScreen().removePreference(mSmsPrefCategory);
            }
        }

        if (!MmsConfig.getMmsEnabled()) {
            // No Mms, remove all the mms-related preferences
            getPreferenceScreen().removePreference(mMmsPrefCategory);

            mStoragePrefCategory.removePreference(findPreference("pref_key_mms_delete_limit"));
        } else {
            if (!MmsConfig.getMMSDeliveryReportsEnabled()) {
                mMmsPrefCategory.removePreference(mMmsDeliveryReportPref);
            }
            if (!MmsConfig.getMMSReadReportsEnabled()) {
                mMmsPrefCategory.removePreference(mMmsReadReportPref);
            }
            // If the phone's SIM doesn't know it's own number, disable group mms.
            if (!MmsConfig.getGroupMmsEnabled() ||
                    TextUtils.isEmpty(MessageUtils.getLocalNumber())) {
                mMmsPrefCategory.removePreference(mMmsGroupMmsPref);
            }
        }

        setEnabledNotificationsPref();

        // If needed, migrate vibration setting from the previous tri-state setting stored in
        // NOTIFICATION_VIBRATE_WHEN to the boolean setting stored in NOTIFICATION_VIBRATE.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains(NOTIFICATION_VIBRATE_WHEN)) {
            String vibrateWhen = sharedPreferences.
                    getString(MessagingPreferenceActivity.NOTIFICATION_VIBRATE_WHEN, null);
            boolean vibrate = "always".equals(vibrateWhen);
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.putBoolean(NOTIFICATION_VIBRATE, vibrate);
            prefsEditor.remove(NOTIFICATION_VIBRATE_WHEN);  // remove obsolete setting
            prefsEditor.apply();
            mVibratePref.setChecked(vibrate);
        }

        mSmsRecycler = Recycler.getSmsRecycler();
        mMmsRecycler = Recycler.getMmsRecycler();

        // Fix up the recycler's summary with the correct values
        setSmsDisplayLimit();
        setMmsDisplayLimit();

        String soundValue = sharedPreferences.getString(NOTIFICATION_RINGTONE, null);
        setRingtoneSummary(soundValue);
    }

    private void setRingtoneSummary(String soundValue) {
        Uri soundUri = TextUtils.isEmpty(soundValue) ? null : Uri.parse(soundValue);
        Ringtone tone = soundUri != null ? RingtoneManager.getRingtone(this, soundUri) : null;
        mRingtonePref.setSummary(tone != null ? tone.getTitle(this)
                : getResources().getString(R.string.silent_ringtone));
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
        if (mIsSmsEnabled) {
            menu.add(0, MENU_RESTORE_DEFAULTS, 0, R.string.restore_default);
        }
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
        } else if (preference == mManageSimPref) {
            startActivity(new Intent(this, ManageSimMessages.class));
		// Junk
        } else if (preference == mBackupMmsSettings) {
    		BackupMmsDialog();
    	} else if (preference == mRestoreMmsSettings) {
    		RestoreMmsDialog();
		// End Junk
        } else if (preference == mClearHistoryPref) {
            showDialog(CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG);
            return true;
        } else if (preference == mEnableNotificationsPref) {
            // Update the actual "enable notifications" value that is stored in secure settings.
            enableNotifications(mEnableNotificationsPref.isChecked(), this);
        } else if (preference == mMmsAutoRetrievialPref) {
            if (mMmsAutoRetrievialPref.isChecked()) {
                startMmsDownload();
            }
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    /**
     * Trigger the TransactionService to download any outstanding messages.
     */
    private void startMmsDownload() {
        startService(new Intent(TransactionService.ACTION_ENABLE_AUTO_RETRIEVE, null, this,
                TransactionService.class));
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

	// Junk
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
    // End JUnk

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
                    .setIconAttribute(android.R.attr.alertDialogIcon)
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
        mRingtonePref.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;
        if (preference == mRingtonePref) {
            setRingtoneSummary((String)newValue);
            result = true;
		// Junk
        } else if (preference == mPresetColors) {
            sp = getSharedPreferences("Junk_" + (String) newValue, Context.MODE_WORLD_READABLE);
            getValues();
            writeValues();
            
        } else if (preference == mRestoreMmsSettings) {
            sp = getSharedPreferences("com.android.mms_preferences_temp", Context.MODE_WORLD_READABLE);
            getValues();
            writeValues();

    	} else if (preference == mSignature) {
    		SharedPreferences.Editor editor = sp.edit();
    	    editor.putString(MSG_SIGNATURE, (String) newValue);
    	    editor.commit();
	    }
		// End Junk
        return result;
    }

    // For the group mms feature to be enabled, the following must be true:
    //  1. the feature is enabled in mms_config.xml (currently on by default)
    //  2. the feature is enabled in the mms settings page
    //  3. the SIM knows its own phone number
    public static boolean getIsGroupMmsEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean groupMmsPrefOn = prefs.getBoolean(
                MessagingPreferenceActivity.GROUP_MMS_MODE, true);
        return MmsConfig.getGroupMmsEnabled() &&
                groupMmsPrefOn &&
                !TextUtils.isEmpty(MessageUtils.getLocalNumber());
    }
	
	// Junk
    private void getValues() {
    	mSignatureText = sp.getString(MSG_SIGNATURE, "");
    	mMsgBType = sp.getString(MSG_BUBBLE_TYPE, "Bubble1");
    	mMsgShowFrame = sp.getBoolean(MSG_SHOW_FRAME, false);
    	mMsgInFrameColor = sp.getInt(MSG_IN_FRAME_COLOR, 0xff008ec2);
    	mMsgOutFrameColor = sp.getInt(MSG_OUT_FRAME_COLOR, 0xff33b5e5);
    	mMsgStretch = sp.getBoolean(MSG_FILL_PARENT, false);
    	mMsgAvatar = sp.getBoolean(MSG_SHOW_AVATAR, false);
    	mMsgContact = sp.getBoolean(MSG_USE_CONTACT, false);
    	mMsgDate = sp.getBoolean(MSG_FULL_DATE, false);
    	mMsgTxtSize = sp.getInt(MSG_TEXT_SIZE, 14);
    	mMsgListBgColor = sp.getInt(MSG_LIST_BG_COLOR, 0xffffffff);
    	mMsgDivHeight = sp.getInt(MSG_DIVIDER_HEIGHT, 0);
        mMsgInBgColor = sp.getInt(MSG_IN_BG_COLOR, 0xff008ec2);
        mMsgOutBgColor = sp.getInt(MSG_OUT_BG_COLOR, 0xff33b5e5);
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
    	editor.putString(MSG_SIGNATURE, mSignatureText);
    	mSignature.setText(mSignatureText);
    	editor.putString(MSG_BUBBLE_TYPE, mMsgBType);
    	editor.putBoolean(MSG_SHOW_FRAME, mMsgShowFrame);
    	editor.putInt(MSG_IN_FRAME_COLOR, mMsgInFrameColor);
    	editor.putInt(MSG_OUT_FRAME_COLOR, mMsgOutFrameColor);
    	editor.putBoolean(MSG_FILL_PARENT, mMsgStretch);
    	editor.putBoolean(MSG_SHOW_AVATAR, mMsgAvatar);
    	editor.putBoolean(MSG_USE_CONTACT, mMsgContact);
    	editor.putBoolean(MSG_FULL_DATE, mMsgDate);
    	editor.putInt(MSG_TEXT_SIZE, mMsgTxtSize);
    	editor.putInt(MSG_DIVIDER_HEIGHT, mMsgDivHeight);
    	editor.putInt(MSG_LIST_BG_COLOR, mMsgListBgColor);
    	editor.putInt(MSG_IN_BG_COLOR, mMsgInBgColor);
    	editor.putInt(MSG_OUT_BG_COLOR, mMsgOutBgColor);
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
	// End Junk

}
