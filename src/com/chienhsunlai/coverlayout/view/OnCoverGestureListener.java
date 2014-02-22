package com.chienhsunlai.coverlayout.view;

import android.content.Context;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class OnCoverGestureListener implements OnGestureListener {

	private float accumulatedY;
	private int touchSlop;
	private float accumulated;
	private boolean isMoving;

	public OnCoverGestureListener(Context context) {
		ViewConfiguration configuration = ViewConfiguration.get(context);
		touchSlop = configuration.getScaledTouchSlop();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		accumulated = accumulatedY = 0;
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		accumulatedY += distanceY;

		float dy = e2.getY() - e1.getY();
		accumulated += dy;
		return isMoving = accumulated > touchSlop;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public float getAccumulatedY() {
		return accumulatedY;
	}

	public boolean isMoving() {
		return isMoving;
	}

}
