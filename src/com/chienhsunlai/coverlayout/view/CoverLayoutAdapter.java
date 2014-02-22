package com.chienhsunlai.coverlayout.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.chienhsunlai.coverlayout.R;

public class CoverLayoutAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;

	private CoverContainerView coverView;
	private ViewGroup banner;
	private BaseAdapter adapter;

	CoverLayoutAdapter(Context context, BaseAdapter adapter) {
		this.context = context;
		this.adapter = adapter;

		layoutInflater = LayoutInflater.from(this.context);

		int min = countCoverSize(context);

		coverView = (CoverContainerView) layoutInflater.inflate(R.layout.cover,
				null);
		coverView.setLayoutParams(new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, min));
		coverView.setFake(true);

		banner = (ViewGroup) layoutInflater.inflate(R.layout.banner, null);
	}

	static int countCoverSize(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int min = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) / 3f);
		return min;
	}

	@Override
	public int getCount() {
		return adapter.getCount() + 2;
	}

	@Override
	public Object getItem(int position) {
		return adapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return adapter.getItemId(position);
	}

	/**
	 * @param position
	 * @param convertView
	 *            It must call setTag()
	 * @param parent
	 * @return
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		switch (position) {
		case 0:
			convertView = coverView;
			convertView.requestLayout();
			break;
		case 1:
			convertView = banner;
			break;
		default:
			boolean isNull = convertView != null
					&& convertView.getTag() == null;

			convertView = adapter.getView(position - 2, isNull ? null
					: convertView, parent);
			break;
		}

		return convertView;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	void setCoverBackground(Bitmap bitmap) {
		coverView.setBackgroundBitmap(bitmap);
	}

}
