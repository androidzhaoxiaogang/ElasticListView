package xg.elasticlistview;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AbsListView;

public class ElasticListView extends ListView implements
		AbsListView.OnScrollListener {
	private static final int INVALID_VALUE = -1;
	private static final String TAG = "ElasticListView";
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float paramAnonymousFloat) {
			float f = paramAnonymousFloat - 1.0F;
			return 1.0F + f * (f * (f * (f * f)));
		}
	};
	int mActivePointerId = -1;
	private FrameLayout mHeaderContainer;
	private int mHeaderHeight;
	private ImageView mHeaderImage;
	float mLastMotionY = -1.0F;
	float mLastScale = -1.0F;
	float mMaxScale = -1.0F;
	private AbsListView.OnScrollListener mOnScrollListener;
	private ScalingRunnalable mScalingRunnalable;
	private int mScreenHeight;
	private ImageView mShadow;

	public ElasticListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ElasticListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ElasticListView(Context context) {
		super(context);
		init(context);
	}

	private void endScraling() {
		if (mHeaderContainer.getBottom() >= this.mHeaderHeight)
			mScalingRunnalable.startAnimation(200L);
	}

	private void init(Context paramContext) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((Activity) paramContext).getWindowManager().getDefaultDisplay()
				.getMetrics(localDisplayMetrics);
		this.mScreenHeight = localDisplayMetrics.heightPixels;
		this.mHeaderContainer = new FrameLayout(paramContext);
		this.mHeaderImage = new ImageView(paramContext);
		int i = localDisplayMetrics.widthPixels;
		setHeaderViewSize(i, (int) (9.0F * (i / 16.0F)));
		this.mShadow = new ImageView(paramContext);
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		localLayoutParams.gravity = 80;
		this.mShadow.setLayoutParams(localLayoutParams);
		this.mHeaderContainer.addView(this.mHeaderImage);
		this.mHeaderContainer.addView(this.mShadow);
		addHeaderView(this.mHeaderContainer);
		mScalingRunnalable = new ScalingRunnalable();
		super.setOnScrollListener(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	public void setHeaderViewSize(int paramInt1, int paramInt2) {
		Object localObject = this.mHeaderContainer.getLayoutParams();
		if (localObject == null)
			localObject = new AbsListView.LayoutParams(paramInt1, paramInt2);
		((ViewGroup.LayoutParams) localObject).width = paramInt1;
		((ViewGroup.LayoutParams) localObject).height = paramInt2;
		this.mHeaderContainer
				.setLayoutParams((ViewGroup.LayoutParams) localObject);
		this.mHeaderHeight = paramInt2;
	}

	public void setOnScrollListener(
			AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOnScrollListener = paramOnScrollListener;
	}

	public void setShadow(int paramInt) {
		this.mShadow.setBackgroundResource(paramInt);
	}

	private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
		int i = (0xFF00 & paramMotionEvent.getAction()) >> 8;
		if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
			if (i != 0)
				break label48;
		label48: for (int j = 1;; j = 0) {
			this.mLastMotionY = paramMotionEvent.getY(j);
			this.mActivePointerId = paramMotionEvent.getPointerId(j);
			return;
		}
	}

	private void reset() {
		this.mActivePointerId = -1;
		this.mLastMotionY = -1.0F;
		this.mMaxScale = -1.0F;
		this.mLastScale = -1.0F;
	}

	public ImageView getHeaderView() {
		return mHeaderImage;
	}

	class ScalingRunnalable implements Runnable {
		long mDuration;
		boolean mIsFinished = true;
		float mScale;
		long mStartTime;

		ScalingRunnalable() {
		}

		public void abortAnimation() {
			mIsFinished = true;
		}

		public boolean isFinished() {
			return mIsFinished;
		}

		public void run() {
			if ((!mIsFinished) && (mScale > 1.0D)) {
				float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime)
						/ (float) mDuration;
				float f2 = mScale - (mScale - 1.0F)
						* ElasticListView.sInterpolator.getInterpolation(f1);
				ViewGroup.LayoutParams localLayoutParams = mHeaderContainer
						.getLayoutParams();
				if (f2 > 1.0F) {
					mIsFinished = true;
				} else {
					for (localLayoutParams.height = mHeaderHeight;; localLayoutParams.height = ((int) (f2 * mHeaderHeight))) {
						mHeaderContainer.setLayoutParams(localLayoutParams);
						post(this);
					}
				}
			}
		}

		public void startAnimation(long paramLong) {
			mStartTime = SystemClock.currentThreadTimeMillis();
			mDuration = paramLong;
			mScale = (mHeaderContainer.getBottom() / mHeaderHeight);
			mIsFinished = false;
			post(this);
		}
	}

}
