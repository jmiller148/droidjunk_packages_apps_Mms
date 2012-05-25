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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.provider.ContactsContract.Profile;
import android.provider.Telephony.Sms;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.android.mms.MmsApp;
import com.android.mms.R;
import com.android.mms.data.Contact;
import com.android.mms.data.WorkingMessage;
import com.android.mms.transaction.Transaction;
import com.android.mms.transaction.TransactionBundle;
import com.android.mms.transaction.TransactionService;
import com.android.mms.util.DownloadManager;
import com.android.mms.util.SmileyParser;
import com.google.android.mms.ContentType;
import com.google.android.mms.pdu.PduHeaders;

import droidjunk.colorfitermaker.ColorFilterMaker;


/**
 * This class provides view of a message in the messages list.
 */
public class MessageListItem extends LinearLayout implements
        SlideViewInterface, OnClickListener {
    public static final String EXTRA_URLS = "com.android.mms.ExtraUrls";

    private static final String TAG = "MessageListItem";
    private static final StyleSpan STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    static final int MSG_LIST_EDIT_MMS   = 1;
    static final int MSG_LIST_EDIT_SMS   = 2;

    private View mMmsView;
    private ImageView mImageView;
    private ImageView mLockedIndicator;
    private ImageView mDeliveredIndicator;
    private ImageView mDetailsIndicator;
    private ImageButton mSlideShowButton;
    private TextView mBodyTextView;
    private Button mDownloadButton;
    private TextView mDownloadingLabel;
    private Handler mHandler;
    private MessageItem mMessageItem;
    private String mDefaultCountryIso;
    private TextView mDateView;
    public View mMessageBlock;
    public View mMessageLayout;
    private QuickContactBadge mAvatar;
    static private Drawable sDefaultContactImage;
    
    
    // Junk
    SharedPreferences sp;
    private boolean mMsgFillParent, mUseContact = false;
    private int mMsgInBgColor, mMsgOutBgColor;

    
    
    
    
    public MessageListItem(Context context) {
        super(context);
        mDefaultCountryIso = MmsApp.getApplication().getCurrentCountryIso();

        if (sDefaultContactImage == null) {
            sDefaultContactImage = context.getResources().getDrawable(R.drawable.ic_contact_picture);
        }
    }

    public MessageListItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        int color = mContext.getResources().getColor(R.color.timestamp_color);
        mColorSpan = new ForegroundColorSpan(color);
        mDefaultCountryIso = MmsApp.getApplication().getCurrentCountryIso();

        if (sDefaultContactImage == null) {
            sDefaultContactImage = context.getResources().getDrawable(R.drawable.ic_contact_picture);
        }

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBodyTextView = (TextView) findViewById(R.id.text_view);
        mDateView = (TextView) findViewById(R.id.date_view);
        mLockedIndicator = (ImageView) findViewById(R.id.locked_indicator);
        mDeliveredIndicator = (ImageView) findViewById(R.id.delivered_indicator);
        mDetailsIndicator = (ImageView) findViewById(R.id.details_indicator);
        mAvatar = (QuickContactBadge) findViewById(R.id.avatar);
        mMessageBlock = findViewById(R.id.message_block);
        mMessageLayout = findViewById(R.id.message_layout);
        
    }
    
 
    // Junk - changed name (drawLeftStatusIndicator) made no sense
    private void getBubbleType() {

    	String bType = sp.getString(MessagingPreferenceActivity.MSG_BUBBLE_TYPE, "BubbleGlass");
    	mMsgFillParent = sp.getBoolean(MessagingPreferenceActivity.MSG_FILL_PARENT, false);
    	if (mMsgFillParent) {
    		mMessageBlock.getLayoutParams().width = LayoutParams.MATCH_PARENT; // Stretch Bubble
    	} else {
    		mMessageBlock.getLayoutParams().width = LayoutParams.WRAP_CONTENT; // Do Not Stretch Bubble
    	}
    	
    	if (mMessageItem.getBoxId() == 1) {
    		mBodyTextView.setLinkTextColor(sp.getInt(MessagingPreferenceActivity.MSG_IN_LINK_COLOR, 0xffffffff));
    		mMessageBlock.setLayoutDirection(LAYOUT_DIRECTION_LTR);
    		mMessageLayout.setLayoutDirection(LAYOUT_DIRECTION_LTR);
    		if (bType.equals("BubbleCall")) {
    		  mMessageBlock.setBackgroundResource(R.drawable.msg_in_bubble_1);
    		  mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgInBgColor, .32f));
    	  
    	  		} else if (bType.equals("Bubble")) {
    	  			mMessageBlock.setBackgroundResource(R.drawable.msg_in_bubble_2);
    	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgInBgColor, .32f));
        
    	  		} else if (bType.equals("Framed")) {
    	  			mMessageBlock.setBackgroundResource(R.drawable.msg_in_frame_1);
    	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgInBgColor, .32f));

    	  		} else if (bType.equals("FramedArrow")) {
       	  			mMessageBlock.setBackgroundResource(R.drawable.msg_in_frame_2);
       	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgInBgColor, .32f));
    	  		
    	  		} else if (bType.equals("Plain")) {
       	  			mMessageBlock.setBackgroundResource(R.drawable.msg_in_frame_3);
       	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgInBgColor, .32f));
    	  		}

    		
    	  	} else {
    	    
    	  		mBodyTextView.setLinkTextColor(sp.getInt(MessagingPreferenceActivity.MSG_OUT_LINK_COLOR, 0xffffffff));
    	  		mMessageBlock.setLayoutDirection(LAYOUT_DIRECTION_RTL);
    	  		mMessageLayout.setLayoutDirection(LAYOUT_DIRECTION_LTR);
    	  		if (bType.equals("BubbleCall")) {
    	  			mMessageBlock.setBackgroundResource(R.drawable.msg_out_bubble_1);
    	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgOutBgColor, .32f));
    		
    	  		} else if (bType.equals("Bubble")) {
    	  			mMessageBlock.setBackgroundResource(R.drawable.msg_out_bubble_2);
    	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgOutBgColor, .32f));
    		 
    	  		} else if (bType.equals("Framed")) {
    	  			mMessageBlock.setBackgroundResource(R.drawable.msg_out_frame_1);
    	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgOutBgColor, .32f));

    	  		} else if (bType.equals("FramedArrow")) {
       	  			mMessageBlock.setBackgroundResource(R.drawable.msg_out_frame_2);
       	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgOutBgColor, .32f));
   	  			
    	  		} else if (bType.equals("Plain")) {
       	  			mMessageBlock.setBackgroundResource(R.drawable.msg_out_frame_3);
       	  			mMessageBlock.getBackground().setColorFilter(ColorFilterMaker.changeColor(mMsgOutBgColor, .32f));
   	  			}
    	  }
    };    
    
    
    
    public void bind(MessageItem msgItem, boolean isLastItem) {
        mMessageItem = msgItem;
      
        // Junk
        mMsgInBgColor = sp.getInt(MessagingPreferenceActivity.MSG_IN_BG_COLOR, 0xff008ec2);
        mMsgOutBgColor = sp.getInt(MessagingPreferenceActivity.MSG_OUT_BG_COLOR, 0xff33b5e5);
        getBubbleType();
        mUseContact = sp.getBoolean(MessagingPreferenceActivity.MSG_USE_CONTACT, false);
        mMsgFillParent = sp.getBoolean(MessagingPreferenceActivity.MSG_FILL_PARENT, false);
        //
        
        setLongClickable(false);
        setClickable(false);    // let the list view handle clicks on the item normally. When
                                // clickable is true, clicks bypass the listview and go straight
                                // to this listitem. We always want the listview to handle the
                                // clicks first.

        switch (msgItem.mMessageType) {
            case PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND:
                bindNotifInd(msgItem);
                break;
            default:
                bindCommonMessage(msgItem);
                break;
        }
    }

    public void unbind() {
        // Clear all references to the message item, which can contain attachments and other
        // memory-intensive objects
        mMessageItem = null;
        if (mImageView != null) {
            // Because #setOnClickListener may have set the listener to an object that has the
            // message item in its closure.
            mImageView.setOnClickListener(null);
        }
        if (mSlideShowButton != null) {
            // Because #drawPlaybackButton sets the tag to mMessageItem
            mSlideShowButton.setTag(null);
        }
    }

    public MessageItem getMessageItem() {
        return mMessageItem;
    }

    public void setMsgListItemHandler(Handler handler) {
        mHandler = handler;
    }

    private void bindNotifInd(final MessageItem msgItem) {
        hideMmsViewIfNeeded();

        String msgSizeText = mContext.getString(R.string.message_size_label)
                                + String.valueOf((msgItem.mMessageSize + 1023) / 1024)
                                + mContext.getString(R.string.kilobyte);
        
        mBodyTextView.setText(formatMessage(msgItem, msgItem.mContact, null, msgItem.mSubject,
                                            msgItem.mHighlight, msgItem.mTextContentType));

        mDateView.setText(msgSizeText + " " + msgItem.mTimestamp);

        // Junk
        // Set the data color and background colors
        int mColor = 0;
        if (mMessageItem.getBoxId() == 1) {
        	mColor = sp.getInt(MessagingPreferenceActivity.MSG_IN_DATE_COLOR, 0xcdcfcfcf);
         } else {
        	 mColor = sp.getInt(MessagingPreferenceActivity.MSG_OUT_DATE_COLOR, 0xcdffffff);
        }
        mDateView.setBackgroundColor(0x00000000);
        mDateView.setTextColor(mColor);
        setBackgroundColor(sp.getInt(MessagingPreferenceActivity.MSG_LIST_BG_COLOR, 0xff000000)); // The Listview
        //
        
        int state = DownloadManager.getInstance().getState(msgItem.mMessageUri);
        switch (state) {
            case DownloadManager.STATE_DOWNLOADING:
                inflateDownloadControls();
                mDownloadingLabel.setVisibility(View.VISIBLE);
                mDownloadButton.setVisibility(View.GONE);
                break;
            case DownloadManager.STATE_UNSTARTED:
            case DownloadManager.STATE_TRANSIENT_FAILURE:
            case DownloadManager.STATE_PERMANENT_FAILURE:
            default:
                setLongClickable(true);
                inflateDownloadControls();
                mDownloadingLabel.setVisibility(View.GONE);
                mDownloadButton.setVisibility(View.VISIBLE);
                mDownloadButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDownloadingLabel.setVisibility(View.VISIBLE);
                        mDownloadButton.setVisibility(View.GONE);
                        Intent intent = new Intent(mContext, TransactionService.class);
                        intent.putExtra(TransactionBundle.URI, msgItem.mMessageUri.toString());
                        intent.putExtra(TransactionBundle.TRANSACTION_TYPE,
                                Transaction.RETRIEVE_TRANSACTION);
                        mContext.startService(intent);
                    }
                });
                break;
        }

        // Hide the indicators.
        mLockedIndicator.setVisibility(View.GONE);
        mDeliveredIndicator.setVisibility(View.GONE);
        mDetailsIndicator.setVisibility(View.GONE);
        updateAvatarView(msgItem.mAddress, false);
    }

    private void updateAvatarView(String addr, boolean isSelf) {
        Drawable avatarDrawable;
        if (isSelf || !TextUtils.isEmpty(addr)) {
            Contact contact = isSelf ? Contact.getMe(false) : Contact.get(addr, false);
            avatarDrawable = contact.getAvatar(mContext, sDefaultContactImage);

            if (isSelf) {
                mAvatar.assignContactUri(Profile.CONTENT_URI);
            } else {
                if (contact.existsInDatabase()) {
                    mAvatar.assignContactUri(contact.getUri());
                } else {
                    mAvatar.assignContactFromPhone(contact.getNumber(), true);
                }
            }
        } else {
            avatarDrawable = sDefaultContactImage;
        }
        mAvatar.setImageDrawable(avatarDrawable);
        
        // Junk
        // Show/Hide the avatar
        if (sp.getBoolean(MessagingPreferenceActivity.MSG_SHOW_AVATAR, false)) {
        	mAvatar.setVisibility(View.VISIBLE);
        } else {
        	mAvatar.setVisibility(View.GONE);
    	}
    }

    private void bindCommonMessage(final MessageItem msgItem) {
        if (mDownloadButton != null) {
            mDownloadButton.setVisibility(View.GONE);
            mDownloadingLabel.setVisibility(View.GONE);
        }
        // Since the message text should be concatenated with the sender's
        // address(or name), I have to display it here instead of
        // displaying it by the Presenter.
        mBodyTextView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        boolean isSelf = Sms.isOutgoingFolder(msgItem.mBoxId);
        String addr = isSelf ? null : msgItem.mAddress;
        updateAvatarView(addr, isSelf);

        // Get and/or lazily set the formatted message from/on the
        // MessageItem.  Because the MessageItem instances come from a
        // cache (currently of size ~50), the hit rate on avoiding the
        // expensive formatMessage() call is very high.
        CharSequence formattedMessage = msgItem.getCachedFormattedMessage();
        if (formattedMessage == null) {
            formattedMessage = formatMessage(msgItem, msgItem.mContact, msgItem.mBody,
                                             msgItem.mSubject,
                                             msgItem.mHighlight, msgItem.mTextContentType);
        }
        mBodyTextView.setText(formattedMessage);
        mBodyTextView.setTextSize(sp.getInt(MessagingPreferenceActivity.MSG_TEXT_SIZE, 14));

        // If we're in the process of sending a message (i.e. pending), then we show a "SENDING..."
        // string in place of the timestamp.
        mDateView.setText(msgItem.isSending() ?
                mContext.getResources().getString(R.string.sending_message) :
                    msgItem.mTimestamp);

        // Junk
        // Set date and background colors
        int mColor = 0;
        if (mMessageItem.getBoxId() == 1) {
        	mColor = sp.getInt(MessagingPreferenceActivity.MSG_IN_DATE_COLOR, 0xcdcfcfcf);
         } else {
        	 mColor = sp.getInt(MessagingPreferenceActivity.MSG_OUT_DATE_COLOR, 0xcdffffff);
        }
        mDateView.setBackgroundColor(0x00000000);
        mDateView.setTextColor(mColor);
        
        setBackgroundColor(sp.getInt(MessagingPreferenceActivity.MSG_LIST_BG_COLOR, 0xff000000)); // The Listview
        //
        
       
        if (msgItem.isSms()) {
            hideMmsViewIfNeeded();
        } else {
            Presenter presenter = PresenterFactory.getPresenter(
                    "MmsThumbnailPresenter", mContext,
                    this, msgItem.mSlideshow);
            presenter.present();

            if (msgItem.mAttachmentType != WorkingMessage.TEXT) {
                inflateMmsView();
                mMmsView.setVisibility(View.VISIBLE);
                setOnClickListener(msgItem);
                drawPlaybackButton(msgItem);
            } else {
                hideMmsViewIfNeeded();
            }
        }
        drawRightStatusIndicator(msgItem);

        requestLayout();
    }

    private void hideMmsViewIfNeeded() {
        if (mMmsView != null) {
            mMmsView.setVisibility(View.GONE);
        }
    }

    @Override
    public void startAudio() {
        // TODO Auto-generated method stub
    }

    @Override
    public void startVideo() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setAudio(Uri audio, String name, Map<String, ?> extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setImage(String name, Bitmap bitmap) {
        inflateMmsView();

        try {
            if (null == bitmap) {
                bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_missing_thumbnail_picture);
            }
            mImageView.setImageBitmap(bitmap);
            mImageView.setVisibility(VISIBLE);
        } catch (java.lang.OutOfMemoryError e) {
            Log.e(TAG, "setImage: out of memory: ", e);
        }
    }

    private void inflateMmsView() {
        if (mMmsView == null) {
            //inflate the surrounding view_stub
            findViewById(R.id.mms_layout_view_stub).setVisibility(VISIBLE);

            mMmsView = findViewById(R.id.mms_view);
            mImageView = (ImageView) findViewById(R.id.image_view);
            mSlideShowButton = (ImageButton) findViewById(R.id.play_slideshow_button);
        }
    }

    private void inflateDownloadControls() {
        if (mDownloadButton == null) {
            //inflate the download controls
            findViewById(R.id.mms_downloading_view_stub).setVisibility(VISIBLE);
            mDownloadButton = (Button) findViewById(R.id.btn_download_msg);
            mDownloadingLabel = (TextView) findViewById(R.id.label_downloading);
        }
    }


    private LineHeightSpan mSpan = new LineHeightSpan() {
        @Override
        public void chooseHeight(CharSequence text, int start,
                int end, int spanstartv, int v, FontMetricsInt fm) {
            fm.ascent -= 10;
        }
    };

    TextAppearanceSpan mTextSmallSpan =
        new TextAppearanceSpan(mContext, android.R.style.TextAppearance_Small);

    ForegroundColorSpan mColorSpan = null;  // set in ctor

    private CharSequence formatMessage(MessageItem msgItem, String contact, String body,
                                       String subject, Pattern highlight,
                                       String contentType) {
        SpannableStringBuilder buf = new SpannableStringBuilder();


        // Junk 
        // Show the contact if set and apply colors

        int mColor = 0;
        int contactLength = 0;
        if (mUseContact) {
        	contactLength = msgItem.mContact.length() + 1;
        	if (mMessageItem.getBoxId() == 1) {
        		mColor = sp.getInt(MessagingPreferenceActivity.MSG_IN_CONTACT_COLOR, 0xffffffff);
        	} else {
        		mColor = sp.getInt(MessagingPreferenceActivity.MSG_OUT_CONTACT_COLOR, 0xffffffff);
        	}
        
        	buf.append(msgItem.mContact + ": ");
        	buf.setSpan(new StyleSpan(Typeface.BOLD), 0, contactLength, 0);
        	buf.setSpan(new ForegroundColorSpan(mColor), 0, contactLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        }
        //
        
        
        boolean hasSubject = !TextUtils.isEmpty(subject);
        SmileyParser parser = SmileyParser.getInstance();
        if (hasSubject) {
            CharSequence smilizedSubject = parser.addSmileySpans(subject, 0xffacacac);
            
            // Can't use the normal getString() with extra arguments for string replacement
            // because it doesn't preserve the SpannableText returned by addSmileySpans.
            // We have to manually replace the %s with our text.
            buf.append(TextUtils.replace(mContext.getResources().getString(R.string.inline_subject),
                    new String[] { "%s" }, new CharSequence[] { smilizedSubject }));
        }

        
        
        if (!TextUtils.isEmpty(body)) {
            // Converts html to spannable if ContentType is "text/html".
            if (contentType != null && ContentType.TEXT_HTML.equals(contentType)) {
                buf.append("\n");
                buf.append(Html.fromHtml(body));
            } else {
                if (hasSubject) {
                    buf.append(" - ");
                }
                if (sp.getBoolean(MessagingPreferenceActivity.MSG_USE_SMILEY, true)) {
                	if (mMessageItem.getBoxId() == 1) {
                		buf.append(parser.addSmileySpans(body, sp.getInt(MessagingPreferenceActivity.MSG_IN_SMILEY_COLOR, 0xffffffff)));
                	} else {
                		buf.append(parser.addSmileySpans(body, sp.getInt(MessagingPreferenceActivity.MSG_OUT_SMILEY_COLOR, 0xffffffff)));
                	}
                } else {
                	buf.append(body);
                }
            }
        }

        
        // Junk
        // Set the color of the text for messages
        mColor = 0;
         if (mMessageItem.getBoxId() == 1) {
         mColor = sp.getInt(MessagingPreferenceActivity.MSG_IN_TEXT_COLOR, 0xffcecece);
          } else {
          mColor = sp.getInt(MessagingPreferenceActivity.MSG_OUT_TEXT_COLOR, 0xffd6d6d6);
         }
    
        buf.setSpan(new ForegroundColorSpan(mColor), contactLength, buf.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);         
 
        
        // Junk  Search Highlight color
        if (highlight != null) {
            if (mMessageItem.getBoxId() == 1) {
                mColor = sp.getInt(MessagingPreferenceActivity.MSG_IN_SEARCH_COLOR, 0xffffffff);
                 } else {
                 mColor = sp.getInt(MessagingPreferenceActivity.MSG_OUT_SEARCH_COLOR, 0xffffffff);
                }
            Matcher m = highlight.matcher(buf.toString());
            while (m.find()) {
                buf.setSpan(new StyleSpan(Typeface.BOLD), m.start(), m.end(), 0);
                buf.setSpan(new ForegroundColorSpan(mColor), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
            }
        }
        return buf;
    }

    private void drawPlaybackButton(MessageItem msgItem) {
        switch (msgItem.mAttachmentType) {
            case WorkingMessage.SLIDESHOW:
            case WorkingMessage.AUDIO:
            case WorkingMessage.VIDEO:
                // Show the 'Play' button and bind message info on it.
                mSlideShowButton.setTag(msgItem);
                // Set call-back for the 'Play' button.
                mSlideShowButton.setOnClickListener(this);
                mSlideShowButton.setVisibility(View.VISIBLE);
                setLongClickable(true);

                // When we show the mSlideShowButton, this list item's onItemClickListener doesn't
                // get called. (It gets set in ComposeMessageActivity:
                // mMsgListView.setOnItemClickListener) Here we explicitly set the item's
                // onClickListener. It allows the item to respond to embedded html links and at the
                // same time, allows the slide show play button to work.
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMessageListItemClick();
                    }
                });
                break;
            default:
                mSlideShowButton.setVisibility(View.GONE);
                break;
        }
    }

    // OnClick Listener for the playback button
    @Override
    public void onClick(View v) {
        MessageItem mi = (MessageItem) v.getTag();
        switch (mi.mAttachmentType) {
            case WorkingMessage.VIDEO:
            case WorkingMessage.AUDIO:
            case WorkingMessage.SLIDESHOW:
                MessageUtils.viewMmsMessageAttachment(mContext, mi.mMessageUri, mi.mSlideshow);
                break;
        }
    }

    public void onMessageListItemClick() {
        // If the message is a failed one, clicking it should reload it in the compose view,
        // regardless of whether it has links in it
        if (mMessageItem != null &&
                mMessageItem.isOutgoingMessage() &&
                mMessageItem.isFailedMessage() ) {
            recomposeFailedMessage();
            return;
        }

        // Check for links. If none, do nothing; if 1, open it; if >1, ask user to pick one
        URLSpan[] spans = mBodyTextView.getUrls();

        if (spans.length == 0) {
            // Do nothing.
        } else if (spans.length == 1) {
            Uri uri = Uri.parse(spans[0].getURL());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, mContext.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            mContext.startActivity(intent);
        } else {
            final java.util.ArrayList<String> urls = MessageUtils.extractUris(spans);

            ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, urls) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    try {
                        String url = getItem(position).toString();
                        TextView tv = (TextView) v;
                        Drawable d = mContext.getPackageManager().getActivityIcon(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        if (d != null) {
                            d.setBounds(0, 0, d.getIntrinsicHeight(), d.getIntrinsicHeight());
                            tv.setCompoundDrawablePadding(10);
                            tv.setCompoundDrawables(d, null, null, null);
                        }
                        final String telPrefix = "tel:";
                        if (url.startsWith(telPrefix)) {
                            url = PhoneNumberUtils.formatNumber(
                                            url.substring(telPrefix.length()), mDefaultCountryIso);
                        }
                        tv.setText(url);
                    } catch (android.content.pm.PackageManager.NameNotFoundException ex) {
                        // it's ok if we're unable to set the drawable for this view - the user
                        // can still use it
                    }
                    return v;
                }
            };

            AlertDialog.Builder b = new AlertDialog.Builder(mContext);

            DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        Uri uri = Uri.parse(urls.get(which));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, mContext.getPackageName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        mContext.startActivity(intent);
                    }
                    dialog.dismiss();
                }
            };

            b.setTitle(R.string.select_link_title);
            b.setCancelable(true);
            b.setAdapter(adapter, click);

            b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            b.show();
        }
    }

    private void setOnClickListener(final MessageItem msgItem) {
        switch(msgItem.mAttachmentType) {
        case WorkingMessage.IMAGE:
        case WorkingMessage.VIDEO:
            mImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageUtils.viewMmsMessageAttachment(mContext, null, msgItem.mSlideshow);
                }
            });
            mImageView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return v.showContextMenu();
                }
            });
            break;

        default:
            mImageView.setOnClickListener(null);
            break;
        }
    }

    /**
     * Assuming the current message is a failed one, reload it into the compose view so that the
     * user can resend it.
     */
    private void recomposeFailedMessage() {
        String type = mMessageItem.mType;
        final int what;
        if (type.equals("sms")) {
            what = MSG_LIST_EDIT_SMS;
        } else {
            what = MSG_LIST_EDIT_MMS;
        }
        if (null != mHandler) {
            Message msg = Message.obtain(mHandler, what);
            msg.obj = new Long(mMessageItem.mMsgId);
            msg.sendToTarget();
        }
    }

    private void drawRightStatusIndicator(MessageItem msgItem) {
        // Locked icon
        if (msgItem.mLocked) {
            mLockedIndicator.setImageResource(R.drawable.ic_lock_message_sms);
            mLockedIndicator.setVisibility(View.VISIBLE);
        } else {
            mLockedIndicator.setVisibility(View.GONE);
        }

        // Delivery icon - we can show a failed icon for both sms and mms, but for an actual
        // delivery, we only show the icon for sms. We don't have the information here in mms to
        // know whether the message has been delivered. For mms, msgItem.mDeliveryStatus set
        // to MessageItem.DeliveryStatus.RECEIVED simply means the setting requesting a
        // delivery report was turned on when the message was sent. Yes, it's confusing!
        if ((msgItem.isOutgoingMessage() && msgItem.isFailedMessage()) ||
                msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.FAILED) {
            mDeliveredIndicator.setImageResource(R.drawable.ic_list_alert_sms_failed);
            mDeliveredIndicator.setVisibility(View.VISIBLE);
        } else if (msgItem.isSms() &&
                msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.RECEIVED) {
            mDeliveredIndicator.setImageResource(R.drawable.ic_sms_mms_delivered);
            mDeliveredIndicator.setVisibility(View.VISIBLE);
        } else {
            mDeliveredIndicator.setVisibility(View.GONE);
        }

        // Message details icon - this icon is shown both for sms and mms messages. For mms,
        // we show the icon if the read report or delivery report setting was set when the
        // message was sent. Showing the icon tells the user there's more information
        // by selecting the "View report" menu.
        if (msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.INFO || msgItem.mReadReport
                || (msgItem.isMms() &&
                        msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.RECEIVED)) {
            mDetailsIndicator.setImageResource(R.drawable.ic_sms_mms_details);
            mDetailsIndicator.setVisibility(View.VISIBLE);
        } else {
            mDetailsIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void setImageRegionFit(String fit) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setImageVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setText(String name, String text) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setTextVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setVideo(String name, Uri video) {
        inflateMmsView();

        try {
            Bitmap bitmap = VideoAttachmentView.createVideoThumbnail(mContext, video);
            if (null == bitmap) {
                bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_missing_thumbnail_video);
            }
            mImageView.setImageBitmap(bitmap);
            mImageView.setVisibility(VISIBLE);
        } catch (java.lang.OutOfMemoryError e) {
            Log.e(TAG, "setVideo: out of memory: ", e);
        }
    }

    @Override
    public void setVideoVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    @Override
    public void stopAudio() {
        // TODO Auto-generated method stub
    }

    @Override
    public void stopVideo() {
        // TODO Auto-generated method stub
    }

    @Override
    public void reset() {
        if (mImageView != null) {
            mImageView.setVisibility(GONE);
        }
    }

    @Override
    public void setVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    @Override
    public void pauseAudio() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pauseVideo() {
        // TODO Auto-generated method stub

    }

    @Override
    public void seekAudio(int seekTo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seekVideo(int seekTo) {
        // TODO Auto-generated method stub

    }


   
}
