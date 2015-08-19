package in.lockerapplication.views;

import in.lockerapplication.utility.CustomTypefaceSpan;

import java.util.Calendar;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * this is custom view class use to display custom digital clock on lock screen
 */
public class CustomDigitalClock extends TextView {
	Calendar mCalendar;
	private final String m12 = "hh:mm aaa \n EEE, MMMM dd"; // "hh:mm aaa \n    EEE, MMMM dd";
	private final String m24 = "k:mm \n EEE, MMMM dd"; // "k:mm \n    EEE, MMMM dd"
	private Typeface typeface1 = null;
	private Typeface typeface2 = null;
	private FormatChangeObserver mFormatChangeObserver;

	private Runnable mTicker;
	private Handler mHandler;

	private boolean mTickerStopped = false;

	private String mFormat;

	public CustomDigitalClock(Context context) {
		super(context);
		initClock(context);
	}

	public CustomDigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
		typeface1 = Typeface.createFromAsset(context.getAssets(),
				"fonts/EX1127_0.TTF");
		typeface2 = Typeface.createFromAsset(context.getAssets(),
				"fonts/arial_0.ttf");
	}

	private void initClock(Context context) {
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}

		mFormatChangeObserver = new FormatChangeObserver();
		getContext().getContentResolver().registerContentObserver(
				Settings.System.CONTENT_URI, true, mFormatChangeObserver);

		setFormat();
	}

	@Override
	protected void onAttachedToWindow() {
		mTickerStopped = false;
		super.onAttachedToWindow();
		mHandler = new Handler();

		/**
		 * requests a tick on the next hard-second boundary
		 */
		mTicker = new Runnable() {
			public void run() {
				if (mTickerStopped)
					return;
				mCalendar.setTimeInMillis(System.currentTimeMillis());
				setCustomText();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;
	}

	/**
	 * Pulls 12/24 mode from system settings
	 * 
	 * @param none
	 * @return boolean
	 */
	private boolean get24HourMode() {
		return android.text.format.DateFormat.is24HourFormat(getContext());
	}

	/**
	 * use to set format for the textview for custom digital clock
	 * 
	 * @param none
	 * @return void
	 */
	private void setFormat() {
		if (get24HourMode()) {
			mFormat = m24;
		} else {
			mFormat = m12;
		}
	}

	/**
	 * This is format change observer change the text view data accordingly
	 */
	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			setFormat();
		}
	}

	/**
	 * This method is use to change text style through spannable string for 24
	 * and 12 hr fromat
	 * 
	 * @param none
	 * @return void
	 */
	private void setCustomText() {
		SpannableStringBuilder builder = new SpannableStringBuilder(
				DateFormat.format(mFormat, mCalendar));
		if (!get24HourMode()) {
			builder.setSpan(new RelativeSizeSpan(3.5f), 0, 5,
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new RelativeSizeSpan(1.3f), 6, 8,
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new StyleSpan(Typeface.BOLD), 6, 8,
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new RelativeSizeSpan(1.3f), 9, builder.length(),
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new CustomTypefaceSpan("", typeface1), 0, 8,
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new CustomTypefaceSpan("", typeface2), 9,
					builder.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.replace(6, 8, builder.toString().substring(6, 8)
					.toUpperCase());
		} else {
			builder.setSpan(new RelativeSizeSpan(3.5f), 0, 5,
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new RelativeSizeSpan(1.3f), 6, builder.length(),
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new CustomTypefaceSpan("", typeface1), 0, 7,
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(new CustomTypefaceSpan("", typeface2), 8,
					builder.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
		}
		setText(builder);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setLayoutParams(params);
		invalidate();

	}
}