package com.chienhsunlai.coverlayout.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.chienhsunlai.coverlayout.R;

public class CoverViewGroup extends ViewGroup implements OnScrollListener {
	private enum State {
		IDLE, ANIMATED,
	}

	private State state = State.IDLE;

	private final static long ANIMATION_RESTORE_DURATION = 300;
	private CoverContainerView coverView;
	private int coverViewHeight;

	private ViewGroup bannerView;
	private ViewGroup contentView;

	private ListView listView;
	private boolean isListViewTop;

	private OnCoverGestureListener onCoverPullGestureListener;
	private GestureDetector detector;
	private boolean isDisplayBanner;

	private float lastY;
	private float currentY;
	private float animationY;
	private long startTime;
	private long duration = ANIMATION_RESTORE_DURATION;

	private AnticipateInterpolator anticipateInterpolator;

	private CoverLayoutAdapter coverLayoutAdapter;

	private Bitmap coverBackground;

	private Runnable restore = new Runnable() {

		@Override
		public void run() {

			float normalize = normalize();

			currentY = animationY
					* anticipateInterpolator.getInterpolation(1 - normalize);

			layoutTopView(0, 0, getMeasuredWidth(), (int) currentY);

			if (normalize < 1) {
				post(restore);
			} else {
				startTime = 0;
				state = State.IDLE;
				removeCallbacks(restore);
			}
		}
	};

	public CoverViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public CoverViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CoverViewGroup(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {

		coverView = (CoverContainerView) inflate(getContext(), R.layout.cover,
				null);
		contentView = (ViewGroup) inflate(getContext(),
				R.layout.cover_view_group_content_view, null);

		bannerView = (ViewGroup) contentView.findViewById(R.id.banner_ref);
		listView = (ListView) contentView.findViewById(R.id.listView1);
		listView.setOnScrollListener(this);
		listView.setVerticalScrollBarEnabled(false);

		contentView.removeView(bannerView);
		contentView.removeView(listView);

		addView(listView);
		addView(bannerView);
		addView(coverView);

		onCoverPullGestureListener = new OnCoverGestureListener(context);
		detector = new GestureDetector(context, onCoverPullGestureListener);

		anticipateInterpolator = new AnticipateInterpolator(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutListView(l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		bannerView.measure(widthMeasureSpec, heightMeasureSpec);

		int height = bannerView.getChildAt(0).getMeasuredHeight();
		int makeMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);
		bannerView.measure(widthMeasureSpec, makeMeasureSpec);

		listView.measure(widthMeasureSpec, heightMeasureSpec);

		height = CoverLayoutAdapter.countCoverSize(getContext());
		makeMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);
		coverView.measure(widthMeasureSpec, makeMeasureSpec);

		coverViewHeight = coverView.getMeasuredHeight();
	}

	public void setAdapter(BaseAdapter adapter) {
		coverLayoutAdapter = adapter == null ? null : new CoverLayoutAdapter(
				getContext(), adapter);
		listView.setAdapter(coverLayoutAdapter);
	}

	private void layoutTopView(int l, int t, int r, int b) {
		int left = l;
		int top = t;
		int right = r;
		int bottom = b;

		bottom += bottom > 0 ? coverViewHeight : 0;
		coverView.setTopOffset(bottom - top - coverViewHeight);
		coverView.layout(left, top, right, bottom);

		top = b;
		bottom = getMeasuredHeight();
		layoutListView(left, top, right, bottom);
	}

	private void layoutListView(int l, int t, int r, int b) {
		listView.layout(l, t, r, b);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		isListViewTop = firstVisibleItem == 0;

		if (firstVisibleItem >= 1 && !isDisplayBanner) {
			int top = 0;
			int bottom = bannerView.getMeasuredHeight();
			int visible = bannerView.getVisibility();

			if (visible == INVISIBLE) {
				bannerView.setVisibility(VISIBLE);
			}

			bannerView.layout(0, top, getMeasuredWidth(), bottom);

			listView.setVerticalScrollBarEnabled(true);
			listView.setVerticalFadingEdgeEnabled(true);
			isDisplayBanner = true;
		} else if (isListViewTop && isDisplayBanner) {
			bannerView.setVisibility(INVISIBLE);
			listView.setVerticalFadingEdgeEnabled(false);
			listView.setVerticalScrollBarEnabled(false);
			isDisplayBanner = false;
		}

		View childAt = view.getChildAt(0);
		isListViewTop = isListViewTop && childAt != null
				&& childAt.getTop() == 0;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean isIntercepted = true;
		switch (state) {
		case IDLE:
			isIntercepted = detector.onTouchEvent(ev) && isListViewTop
					&& onCoverPullGestureListener.getAccumulatedY() < 0;
			break;
		default:
		}

		return isIntercepted;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (state) {
		case IDLE:
			int id = event.getPointerId(0);
			if (id == 0) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					runRestoreAnimation();
					break;
				case MotionEvent.ACTION_MOVE:
					float y = event.getY(id);
					currentY += (Math.abs(lastY) == 0 ? 0 : y - lastY);
					currentY = Math.max(-coverViewHeight, currentY);

					layoutTopView(0, 0, getMeasuredWidth(), (int) currentY);

					lastY = y;
					break;
				}

			} else {
				runRestoreAnimation();
			}

			break;
		default:
		}

		return true;
	}

	private void runRestoreAnimation() {
		lastY = 0;

		animationY = currentY;

		startTime = System.currentTimeMillis();

		state = State.ANIMATED;

		post(restore);
	}

	private float normalize() {
		return normalize(duration);
	}

	private float normalize(long duration) {
		float normalize = (float) (System.currentTimeMillis() - startTime)
				/ duration;
		return normalize > 1 ? 1 : normalize;
	}

	public void setCoverBackground(Bitmap coverBackground) {
		this.coverBackground = coverBackground;

		coverView.setBackgroundBitmap(this.coverBackground);

		if (coverLayoutAdapter != null) {
			coverLayoutAdapter.setCoverBackground(this.coverBackground);
		}
	}

}
