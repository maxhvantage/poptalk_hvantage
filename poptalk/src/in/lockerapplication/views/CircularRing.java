package in.lockerapplication.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.poptalk.app.R;

public class CircularRing extends View {

	/**
	 * Customizable display parameters (in percents)
	 */

	private RectF mDragRect;
	private Bitmap[] mArcBitmapArray;
	private Bitmap[] mArcBitmapSelectorArray;
	private Bitmap mDragBitmap;
	private int[] mDrawableIds = { R.drawable.tag, R.drawable.call_locker,
			R.drawable.message_locker, R.drawable.unlock };
	private int[] mDrawableSelectorIds = { R.drawable.tag_selected,
			R.drawable.call_selected_locker, R.drawable.message_selected_locker,
			R.drawable.unlock_selected };
	private RectF[] mArcBitmapRect;
	private int mAngleDifference;
	private int mSectorAngle;
	private RectF mOriginalDragRect;
	/**
	 * Currently selected color
	 */
	private Paint mBitmapPaint;
	private float mRadius;
	private float mOuterRadius;
	private float mCentreX;
	private float mCentreY;
	private int mCurrentDragIndex = -1;
	private RectF[] mArcBitmapSelectorRect;
	private int width;
	private int height;
	private boolean mDrawLock = true;
	private boolean mIsDragActive;
	private Bitmap mDragSelectedBitmap;
	private Paint mPaint;
	private IIntersectionListner mIntersectListner;
	private int mSpaceFromBottom;

	public IIntersectionListner getIntersectListner() {
		return mIntersectListner;
	}

	public void setIntersectListner(IIntersectionListner intersectListner) {
		this.mIntersectListner = intersectListner;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		if (mIsDragActive) {
			canvas.drawColor(Color.parseColor("#44000000"));
			for (int i = 0; i < mDrawableIds.length; i++) {
				if (mCurrentDragIndex == i) {
					canvas.drawBitmap(mArcBitmapSelectorArray[i],
							mArcBitmapSelectorRect[i].left,
							mArcBitmapSelectorRect[i].top, mBitmapPaint);
				} else {
					canvas.drawBitmap(mArcBitmapArray[i],
							mArcBitmapRect[i].left, mArcBitmapRect[i].top,
							mBitmapPaint);
				}
			}
			if (mDrawLock) {
				canvas.drawBitmap(mDragSelectedBitmap, mDragRect.left,
						mDragRect.top, mBitmapPaint);
			}

		} else if (mDrawLock) {
			canvas.drawBitmap(mDragBitmap, mDragRect.left, mDragRect.top,
					mBitmapPaint);
		}

	}

	public CircularRing(Context context) {
		super(context);
		init();
	}

	public CircularRing(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircularRing(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		Resources resources = getContext().getResources();
		mArcBitmapArray = new Bitmap[mDrawableIds.length];
		mArcBitmapSelectorArray = new Bitmap[mDrawableSelectorIds.length];
		mArcBitmapRect = new RectF[mDrawableIds.length];
		mArcBitmapSelectorRect = new RectF[mDrawableIds.length];
		for (int i = 0; i < mDrawableIds.length; i++) {
			mArcBitmapRect[i] = new RectF();
			mArcBitmapSelectorRect[i] = new RectF();
			mArcBitmapArray[i] = BitmapFactory.decodeResource(resources,
					mDrawableIds[i]);
			mArcBitmapSelectorArray[i] = BitmapFactory.decodeResource(
					resources, mDrawableSelectorIds[i]);
		}

		mDragBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.drag_circle);
		mDragSelectedBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.circle_selected);
		mAngleDifference = 180 / (mDrawableIds.length - 1);
		mSectorAngle = 180 / mDrawableIds.length;
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		mRadius = resources.getDimension(R.dimen.d_arc_radius);
		mSpaceFromBottom = resources
				.getDimensionPixelSize(R.dimen.d_space_from_bottom);
		mOuterRadius = mRadius
				+ mArcBitmapArray[mDrawableIds.length - 1].getHeight() / 2.0f;
		mDragRect = new RectF();
		mOriginalDragRect = new RectF();
		mPaint = new Paint();
	}

	public void destroyView() {
		for (int i = 0; i < mDrawableIds.length; i++) {
			mArcBitmapArray[i].recycle();
			mArcBitmapSelectorArray[i].recycle();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Log.e("onMeasure", "onMeasure");
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		mCentreX = width / 2;
		mCentreY = height - mSpaceFromBottom / 2;
		mDragRect.set((mCentreX - mDragBitmap.getWidth() / 2.0f),
				(mCentreY - mDragBitmap.getHeight() / 2),
				(mCentreX + mDragBitmap.getWidth() / 2.0f), mCentreY
						+ mDragBitmap.getHeight() / 2);
		mOriginalDragRect.set(mDragRect);
		int angle = 180;
		float left, top;
		double angleInRadians;
		for (int i = 0; i < mDrawableIds.length; i++) {
			angleInRadians = angle * (Math.PI / 180);
			left = (float) (mCentreX + mRadius * Math.cos(angleInRadians));
			top = (float) (mCentreY - mRadius * Math.sin(angleInRadians));
			if (mDrawableIds[i] != 0)
				mArcBitmapRect[i].set(left - mArcBitmapArray[i].getWidth()
						/ 2.0f, top - mArcBitmapArray[i].getHeight(), left
						+ mArcBitmapArray[i].getWidth() / 2.0f, top);
			if (mDrawableSelectorIds[i] != 0)
				mArcBitmapSelectorRect[i].set(
						left - mArcBitmapSelectorArray[i].getWidth() / 2.0f,
						top - mArcBitmapSelectorArray[i].getHeight(), left
								+ mArcBitmapSelectorArray[i].getWidth() / 2.0f,
						top);
			angle -= mAngleDifference;
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			super.onRestoreInstanceState(bundle.getParcelable("super"));
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle state = new Bundle();
		state.putParcelable("super", super.onSaveInstanceState());
		return state;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x, y;
		x = event.getX();
		y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.e(event.hashCode() + "action=>" + x, "ACTION_DOWN=>" + y);
			x = event.getX();
			y = event.getY();
			if (mDragRect.contains(x, y)) {
				mIsDragActive = true;
				invalidate();
				return true;
			}else{
				return false;
			}
		case MotionEvent.ACTION_MOVE:
			if (mIsDragActive) {
				mCurrentDragIndex = -1;
				x = event.getX();
				y = event.getY();
				if (inCircle(x, y)) {
					mDrawLock = true;
					for (int i = 0; i < mDrawableIds.length; i++) {
						if (mDragRect.intersect(mArcBitmapRect[i])) {
							mCurrentDragIndex = i;
							break;
						}
					}

				} else {
					mDrawLock = false;
					if (y < mCentreY) {

						double degreeAngle = Math.toDegrees(Math
								.atan((y - mCentreY) / (x - mCentreX)));
						if (degreeAngle > 0) {
							mCurrentDragIndex = (int) degreeAngle
									/ mSectorAngle;
						} else {
							mCurrentDragIndex = (int) (180 + degreeAngle)
									/ mSectorAngle;
						}
						Log.e("----" + ((180 + degreeAngle)), "---degreee->"
								+ degreeAngle);
					}
				}
				mDragRect.set((x - mDragBitmap.getWidth() / 2.0f),
						(y - mDragBitmap.getHeight() / 2),
						(x + mDragBitmap.getWidth() / 2.0f),
						(y + mDragBitmap.getHeight() / 2));
				invalidate();
			}
			return true;
		case MotionEvent.ACTION_UP:
			Log.e("action", "ACTION_UP" + event.hashCode());
			if (mIsDragActive) {

				if (inCircle(x, y)) {
					for (int i = 0; i < mDrawableIds.length; i++) {
						if (mDragRect.intersect(mArcBitmapRect[i])) {
							mCurrentDragIndex = i;
							// TODO : call particular click option
							if (mIntersectListner != null) {
								Log.e("mIntersectListner--->",
										"  -----  mIntersectListnerACTION_UP-&&&>"
												+ i);
								mIntersectListner.intersectionBitmapNumber(i);
							}
							break;
						}
					}
				} else {
					mDrawLock = false;
					if (y < mCentreY) {

						double degreeAngle = Math.toDegrees(Math
								.atan((y - mCentreY) / (x - mCentreX)));
						if (degreeAngle > 0) {
							mCurrentDragIndex = (int) degreeAngle
									/ mSectorAngle;
						} else {
							mCurrentDragIndex = (int) (180 + degreeAngle)
									/ mSectorAngle;
						}
						if (mIntersectListner != null) {
							Log.e("mIntersectListner--->",
									"  -----  mIntersectListnerACTION_UP-&&&>"
											+ mCurrentDragIndex);
							mIntersectListner
									.intersectionBitmapNumber(mCurrentDragIndex);
						}
						Log.e("----" + ((180 + degreeAngle)), "---degreee->"
								+ degreeAngle);
					}
				}
			}
			mDragRect.set(mOriginalDragRect);
			mIsDragActive = false;
			mCurrentDragIndex = -1;
			mDrawLock = true;
			invalidate();
//			break;
			return false;
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
			mDragRect.set(mOriginalDragRect);
			mCurrentDragIndex = -1;
			mDrawLock = true;
			mIsDragActive = false;
			invalidate();
			return true;
		}

		return true;
	}

	private boolean inCircle(float x, float y){
		float mRadius = mOuterRadius;
		float dx = Math.abs(x - mCentreX);
		if (dx > mRadius)
			return false;
		float dy = Math.abs(y - mCentreY);
		if (dy > mRadius)
			return false;
		if (dx + dy <= mRadius)
			return true;
		return (dx * dx + dy * dy <= mRadius * mRadius);
	}

}
