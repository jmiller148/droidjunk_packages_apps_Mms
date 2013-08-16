/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
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


import com.android.mms.LogTag;
import com.android.mms.R;
import com.android.mms.data.Contact;
import com.android.mms.data.ContactList;
import com.android.mms.data.Conversation;
import com.android.mms.util.SmileyParser;

import droidjunk.colorfitermaker.ColorFilterMaker;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.mms.LogTag;
import com.android.mms.R;
import com.android.mms.data.Contact;
import com.android.mms.data.ContactList;
import com.android.mms.data.Conversation;
import com.android.mms.util.SmileyParser;

/**
 * This class manages the view for given conversation.
 */
public class ConversationListItem extends RelativeLayout implements Contact.UpdateListener,
            Checkable {
    private static final String TAG = "ConversationListItem";
    private static final boolean DEBUG = false;

    private TextView mSubjectView;
    private TextView mFromView;
    private TextView mDateView;
    private ImageView mAttachmentView;
    private ImageView mErrorIndicator;
    private QuickContactBadge mAvatarView;

    static private Drawable sDefaultContactImage;
    
    // Junk
    private SharedPreferences sp;
    // End Junk

    // For posting UI update Runnables from other threads:
    private Handler mHandler = new Handler();

    private Conversation mConversation;

    public static final StyleSpan STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    public ConversationListItem(Context context) {
        super(context);
    }

    public ConversationListItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (sDefaultContactImage == null) {
            sDefaultContactImage = context.getResources().getDrawable(R.drawable.ic_contact_picture);
        }
        // Junk
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        // End Junk
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFromView = (TextView) findViewById(R.id.from);
        mSubjectView = (TextView) findViewById(R.id.subject);

        mDateView = (TextView) findViewById(R.id.date);
        mAttachmentView = (ImageView) findViewById(R.id.attachment);
        mErrorIndicator = (ImageView) findViewById(R.id.error);
        mAvatarView = (QuickContactBadge) findViewById(R.id.avatar);
    }

    public Conversation getConversation() {
        return mConversation;
    }

    /**
     * Only used for header binding.
     */
    public void bind(String title, String explain) {
        mFromView.setText(title);
        mSubjectView.setText(explain);
    }

    private CharSequence formatMessage() {
        final int color = android.R.styleable.Theme_textColorSecondary;
        String from = mConversation.getRecipients().formatNames(", ");

        SpannableStringBuilder buf = new SpannableStringBuilder(from);

        if (mConversation.getMessageCount() > 1) {
            int before = buf.length();
            buf.append(mContext.getResources().getString(R.string.message_count_format,
                    mConversation.getMessageCount()));
			// Junk
            if (mConversation.isChecked()) {            
            buf.setSpan(new ForegroundColorSpan(
            		sp.getInt(MessagingPreferenceActivity.SELECTED_CONV_COUNT_COLOR, 0xff000000)),
                    before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (mConversation.hasUnreadMessages()) {
                buf.setSpan(new ForegroundColorSpan(
                		sp.getInt(MessagingPreferenceActivity.UNREAD_CONV_COUNT_COLOR, 0xff33b5e5)),
                        before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                buf.setSpan(new ForegroundColorSpan(
                		sp.getInt(MessagingPreferenceActivity.READ_CONV_COUNT_COLOR, 0xffe2e2e2)),
                        before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }			// End Junk                    
        }
        if (mConversation.hasDraft()) {
            buf.append(mContext.getResources().getString(R.string.draft_separator));
            int before = buf.length();
            int size;
            buf.append(mContext.getResources().getString(R.string.has_draft));
            size = android.R.style.TextAppearance_Small;
            buf.setSpan(new TextAppearanceSpan(mContext, size, color), before,
                    buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            buf.setSpan(new ForegroundColorSpan(
                    mContext.getResources().getColor(R.drawable.text_color_red)),
                    before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // Unread messages are shown in bold
        if (mConversation.hasUnreadMessages()) {
            buf.setSpan(STYLE_BOLD, 0, buf.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return buf;
    }

    private void updateAvatarView() {
        Drawable avatarDrawable;
        if (mConversation.getRecipients().size() == 1) {
            Contact contact = mConversation.getRecipients().get(0);
            avatarDrawable = contact.getAvatar(mContext, sDefaultContactImage);

            if (contact.existsInDatabase()) {
                mAvatarView.assignContactUri(contact.getUri());
            } else {
                mAvatarView.assignContactFromPhone(contact.getNumber(), true);
            }
        } else {
            // TODO get a multiple recipients asset (or do something else)
            avatarDrawable = sDefaultContactImage;
            mAvatarView.assignContactUri(null);
        }
        mAvatarView.setImageDrawable(avatarDrawable);
        mAvatarView.setVisibility(View.VISIBLE);
    }

    private void updateFromView() {
        mFromView.setText(formatMessage());
        updateAvatarView();
    }

    public void onUpdate(Contact updated) {
        if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
            Log.v(TAG, "onUpdate: " + this + " contact: " + updated);
        }
        mHandler.post(new Runnable() {
            public void run() {
                updateFromView();
            }
        });
    }

    public final void bind(Context context, final Conversation conversation) {
        //if (DEBUG) Log.v(TAG, "bind()");

        mConversation = conversation;

        updateBackground();

        LayoutParams attachmentLayout = (LayoutParams)mAttachmentView.getLayoutParams();
        boolean hasError = conversation.hasError();
        // When there's an error icon, the attachment icon is left of the error icon.
        // When there is not an error icon, the attachment icon is left of the date text.
        // As far as I know, there's no way to specify that relationship in xml.
        if (hasError) {
            attachmentLayout.addRule(RelativeLayout.LEFT_OF, R.id.error);
        } else {
            attachmentLayout.addRule(RelativeLayout.LEFT_OF, R.id.date);
        }

        boolean hasAttachment = conversation.hasAttachment();
        mAttachmentView.setVisibility(hasAttachment ? VISIBLE : GONE);

        // Date
        mDateView.setText(MessageUtils.formatTimeStampString(context, conversation.getDate(), false));

        // From.
        mFromView.setText(formatMessage());

        // Register for updates in changes of any of the contacts in this conversation.
        ContactList contacts = conversation.getRecipients();

        if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
            Log.v(TAG, "bind: contacts.addListeners " + this);
        }
        Contact.addListener(this);

        // Subject
        SmileyParser parser = SmileyParser.getInstance();
        mSubjectView.setText(parser.addSmileySpans(conversation.getSnippet(), 0xffacacac));
        LayoutParams subjectLayout = (LayoutParams)mSubjectView.getLayoutParams();
        // We have to make the subject left of whatever optional items are shown on the right.
        subjectLayout.addRule(RelativeLayout.LEFT_OF, hasAttachment ? R.id.attachment :
            (hasError ? R.id.error : R.id.date));

        // Transmission error indicator.
        mErrorIndicator.setVisibility(hasError ? VISIBLE : GONE);

        updateAvatarView();
    }

    private void updateBackground() {
		// Junk
        if (mConversation.isChecked()) {
        	setBackgroundColor(sp.getInt(MessagingPreferenceActivity.SELECTED_CONV_BG_COLOR, 0xff33b5e5));
            mFromView.setTextColor(sp.getInt(MessagingPreferenceActivity.SELECTED_CONV_CONTACT_COLOR, 0xffffffff));
            mSubjectView.setTextColor(sp.getInt(MessagingPreferenceActivity.SELECTED_CONV_SUBJECT_COLOR, 0xff3c3c3c));
            mDateView.setTextColor(sp.getInt(MessagingPreferenceActivity.SELECTED_CONV_DATE_COLOR, 0xff4b4b4b));
            mErrorIndicator.setColorFilter(ColorFilterMaker.changeColor(
            		sp.getInt(MessagingPreferenceActivity.SELECTED_CONV_ERROR_COLOR, 0xff4f4f4f), .6f));
            mAttachmentView.setColorFilter(ColorFilterMaker.changeColor(
            		sp.getInt(MessagingPreferenceActivity.SELECTED_CONV_ATTACH_COLOR, 0xff4f4f4f), .6f));

        } else if (mConversation.hasUnreadMessages()) {
            setBackgroundColor(sp.getInt(MessagingPreferenceActivity.UNREAD_CONV_BG_COLOR, 0xffd8d8d8));
            mFromView.setTextColor(sp.getInt(MessagingPreferenceActivity.UNREAD_CONV_CONTACT_COLOR, 0xff000000));
            mSubjectView.setTextColor(sp.getInt(MessagingPreferenceActivity.UNREAD_CONV_SUBJECT_COLOR, 0xff424242));
            mDateView.setTextColor(sp.getInt(MessagingPreferenceActivity.UNREAD_CONV_DATE_COLOR, 0xff363636));
            mErrorIndicator.setColorFilter(ColorFilterMaker.changeColor(
            		sp.getInt(MessagingPreferenceActivity.UNREAD_CONV_ERROR_COLOR, 0xffffffff), .6f));
            mAttachmentView.setColorFilter(ColorFilterMaker.changeColor(
            		sp.getInt(MessagingPreferenceActivity.UNREAD_CONV_ATTACH_COLOR, 0xffffffff), .6f));

        } else {
            setBackgroundColor(sp.getInt(MessagingPreferenceActivity.READ_CONV_BG_COLOR, 0xff4e4e4e));
            mFromView.setTextColor(sp.getInt(MessagingPreferenceActivity.READ_CONV_CONTACT_COLOR, 0xff33b5e5));
            mSubjectView.setTextColor(sp.getInt(MessagingPreferenceActivity.READ_CONV_SUBJECT_COLOR, 0xffb2b2b2));
            mDateView.setTextColor(sp.getInt(MessagingPreferenceActivity.READ_CONV_DATE_COLOR, 0xff4b4b4b));
            mErrorIndicator.setColorFilter(ColorFilterMaker.changeColor(
            		sp.getInt(MessagingPreferenceActivity.READ_CONV_ERROR_COLOR, 0xffdbdbdb), .6f));
            mAttachmentView.setColorFilter(ColorFilterMaker.changeColor(
            		sp.getInt(MessagingPreferenceActivity.READ_CONV_ATTACH_COLOR, 0xffdbdbdb), .6f));

        }
		// End Junk
    }

    public final void unbind() {
        if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
            Log.v(TAG, "unbind: contacts.removeListeners " + this);
        }
        // Unregister contact update callbacks.
        Contact.removeListener(this);
    }

    public void setChecked(boolean checked) {
        mConversation.setIsChecked(checked);
        updateBackground();
    }

    public boolean isChecked() {
        return mConversation.isChecked();
    }

    public void toggle() {
        mConversation.setIsChecked(!mConversation.isChecked());
    }
}
