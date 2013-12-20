/*
 * Copyright (C) 2011 The Android Open Source Project
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

/*
 * This preference will add a min value above and to the
 * left of the seekbar, a max value to the right and above
 * the seekbar and a current value above and centered over
 * the seekbar.  The current value will update as the seekbar
 * is moved.
 * 
 * Use preference_widget_dj_seekbar.xml
 */


package com.android.mms.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.mms.R;

/**
 * @hide
 */
public class DJSeekBarPreference extends Preference
        implements OnSeekBarChangeListener {

    private int mProgress = 0;
    private int mMax = 0;
    private int mMin = 0;
    private boolean mTrackingTouch;	
    private TextView mCurrentValueView;
    private SeekBar seekbar;
    

    
    public DJSeekBarPreference(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                com.android.internal.R.styleable.ProgressBar, defStyle, 0);
        setMax(a.getInt(com.android.internal.R.styleable.ProgressBar_max, mMax - mMin));
        setMin(a.getInt(com.android.internal.R.styleable.ProgressBar_max, mMin));
        a.recycle();
        setLayoutResource(R.layout.preference_widget_dj_seekbar);

    }


    public DJSeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DJSeekBarPreference(Context context) {
        this(context, null);
    }    
    
    
    @Override
    protected View onCreateView(ViewGroup parent) {

   	
    final LinearLayout layout = (LinearLayout) LayoutInflater.from(
    		getContext()).inflate(R.layout.preference_widget_dj_seekbar, null);

    
    TextView mMinValueView = (TextView) layout.findViewById(R.id.min_value);
    mCurrentValueView = (TextView) layout.findViewById(R.id.current_value);
    TextView mMaxValueView = (TextView) layout.findViewById(R.id.max_value);
    mMinValueView.setText(String.valueOf(mMin));
    mCurrentValueView.setText(String.valueOf(mProgress + mMin));
    mMaxValueView.setText(String.valueOf(mMax));
    
    
    mMaxValueView.setClickable(true);
	mMaxValueView.setOnClickListener(new OnClickListener() { 
		public void onClick (View v){
			if (mProgress < (mMax - mMin)) {
				mProgress = mProgress + 1;
				callChangeListener(mProgress);
				seekbar.setProgress(mProgress);
				//setProgress(mProgress + mMin);
				persistInt(mProgress + mMin);
				mCurrentValueView.setText(String.valueOf(mProgress + mMin));
				
			}
		}
	}
	);
    
    mMinValueView.setClickable(true);
	mMinValueView.setOnClickListener(new OnClickListener() { 
		public void onClick (View v){
			if (mProgress > 0) {
				mProgress = mProgress - 1;
				callChangeListener(mProgress);
				seekbar.setProgress(mProgress);
				//setProgress(mProgress);
				persistInt(mProgress + mMin);
				mCurrentValueView.setText(String.valueOf(mProgress + mMin));
				
			}
		}
	}
	);
    return layout;
    }

    
    @Override
    protected void onBindView(View view) {
    	super.onBindView(view);
    	seekbar = (SeekBar) view.findViewById(R.id.djseekbar);
    	seekbar.setOnSeekBarChangeListener(this);
    	seekbar.setMax(mMax - mMin);
    	seekbar.setProgress(mProgress);
    	seekbar.setEnabled(isEnabled());
    }    
    

   
       @Override
       public CharSequence getSummary() {
           return null;
       }

       @Override
       protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
           setProgress(restoreValue ? getPersistedInt(mProgress - mMin)
                   : (Integer) defaultValue);
       }

       @Override
       protected Object onGetDefaultValue(TypedArray a, int index) {
           return a.getInt(index, 0);
       }

       @Override
       public boolean onKey(View v, int keyCode, KeyEvent event) {
           if (event.getAction() != KeyEvent.ACTION_UP) {
               if (keyCode == KeyEvent.KEYCODE_PLUS
                       || keyCode == KeyEvent.KEYCODE_EQUALS) {
                   setProgress(getProgress() + 1);
                   return true;
               }
               if (keyCode == KeyEvent.KEYCODE_MINUS) {
                   setProgress(getProgress() - 1);
                   return true;
               }
           }
           return false;
       }

       
       public void setMax(int max) {
           if (max != mMax) {
               mMax = max;
               notifyChanged();
           }
       }

       public void setMin(int min) {
           if (min != mMin) {
               mMin = min;
               notifyChanged();
           }
       }
       
       public void setProgress(int progress) {
     		   setProgress(progress - mMin, true);
        }

       private void setProgress(int progress, boolean notifyChanged) {

    	   if (progress > mMax - mMin) {
               progress = mMax - mMin;
           }
           if (progress < 0) {
               progress = 0;
           }
           if (progress != mProgress) {
               mProgress = progress;
               persistInt(progress + mMin);
               if (notifyChanged) {
                   notifyChanged();
               }
           }
           
       }

       public int getProgress() {
    	   return mProgress + mMin;
       }

       /**
        * Persist the seekBar's progress value if callChangeListener
        * returns true, otherwise set the seekBar's progress to the stored value
        */
       void syncProgress(SeekBar seekBar) {
           int progress = seekBar.getProgress();
           if (progress != mProgress) {
               if (callChangeListener(progress)) {
                   setProgress(progress, false);
               } else {
                   seekBar.setProgress(mProgress);
               }
           }
           mCurrentValueView.setText(String.valueOf(mProgress + mMin));
       }

       @Override
       public void onProgressChanged(
               SeekBar seekBar, int progress, boolean fromUser) {
           if (fromUser && !mTrackingTouch) {
               syncProgress(seekBar);
           }
           mCurrentValueView.setText(String.valueOf(mProgress + mMin));
       }

       @Override
       public void onStartTrackingTouch(SeekBar seekBar) {
           mTrackingTouch = true;
       }

       
       @Override
       public void onStopTrackingTouch(SeekBar seekBar) {
           mTrackingTouch = false;
           if (seekBar.getProgress() != mProgress) {
               syncProgress(seekBar);
           }
       }

       @Override
       protected Parcelable onSaveInstanceState() {
           /*
            * Suppose a client uses this preference type without persisting. We
            * must save the instance state so it is able to, for example, survive
            * orientation changes.
            */

           final Parcelable superState = super.onSaveInstanceState();
           if (isPersistent()) {
               // No need to save instance state since it's persistent
               return superState;
           }

           // Save the instance state
           final SavedState myState = new SavedState(superState);
           myState.progress = mProgress;
           myState.max = mMax;
           return myState;
       }

       @Override
       protected void onRestoreInstanceState(Parcelable state) {
           if (!state.getClass().equals(SavedState.class)) {
               // Didn't save state for us in onSaveInstanceState
               super.onRestoreInstanceState(state);
               return;
           }

           // Restore the instance state
           SavedState myState = (SavedState) state;
           super.onRestoreInstanceState(myState.getSuperState());
           mProgress = myState.progress;
           mMax = myState.max;
           setProgress(mProgress);
           mCurrentValueView.setText(String.valueOf(mProgress + mMin));
           notifyChanged();
       }       
       
       
       /**
        * SavedState, a subclass of {@link BaseSavedState}, will store the state
        * of MyPreference, a subclass of Preference.
        * <p>
        * It is important to always call through to super methods.
        */
       private static class SavedState extends BaseSavedState {
           int progress;
           int max;

           public SavedState(Parcel source) {
               super(source);

               // Restore the click counter
               progress = source.readInt();
               max = source.readInt();
           }

           @Override
           public void writeToParcel(Parcel dest, int flags) {
               super.writeToParcel(dest, flags);

               // Save the click counter
               dest.writeInt(progress);
               dest.writeInt(max);
           }

           public SavedState(Parcelable superState) {
               super(superState);
           }

           @SuppressWarnings("unused")
           public static final Parcelable.Creator<SavedState> CREATOR =
                   new Parcelable.Creator<SavedState>() {
               public SavedState createFromParcel(Parcel in) {
                   return new SavedState(in);
               }

               public SavedState[] newArray(int size) {
                   return new SavedState[size];
               }
           };
       }       
       
}

