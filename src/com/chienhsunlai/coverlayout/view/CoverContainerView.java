package com.chienhsunlai.coverlayout.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CoverContainerView extends LinearLayout {

	private Bitmap backgroundBitmap;
	private int topOffset;

	private Matrix matrix = new Matrix();
	private TextPaint paint;
	private boolean isFake;
	private int currentHeight;
	private int currentWidth;

	public CoverContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

		setWillNotDraw(false);
	}

	public CoverContainerView(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		currentWidth = r - l;
		currentHeight = b - t;

		t = isFake ? 0 : t + topOffset;
		b = isFake ? getMeasuredHeight() : b;
		getChildAt(0).layout(l, t, r, b);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getVisibility() == VISIBLE && backgroundBitmap != null
				&& !backgroundBitmap.isRecycled()) {

			canvas.save();
			matrix.reset();
			matrix.isIdentity();

			int bw = backgroundBitmap.getWidth();
			int bh = backgroundBitmap.getHeight();

			float sx = (float) currentWidth / bw;
			float sy = (float) currentHeight / bh;
			float scale = Math.min(sx, sy);

			int tx = (currentWidth - bw) / 2;
			int ty = (currentHeight - bh) / 2;

			matrix.postTranslate(tx, ty);

			float hh = currentHeight / 2;
			float hw = currentWidth / 2;

			matrix.postTranslate(-hw, -hh);
			matrix.postScale(scale, scale);
			matrix.postTranslate(hw, hh);

			canvas.drawBitmap(backgroundBitmap, matrix, paint);
			canvas.restore();
		}

		super.onDraw(canvas);
	}

	public void setTopOffset(int topOffset) {
		if (!isFake) {
			this.topOffset = topOffset;
		}
	}

	public void setBackgroundBitmap(Bitmap backgroundBitmap) {
		this.backgroundBitmap = backgroundBitmap;

	}

	public void setFake(boolean isFake) {
		this.isFake = isFake;
	}

}
