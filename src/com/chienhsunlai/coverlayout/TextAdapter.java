package com.chienhsunlai.coverlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TextAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;

	public TextAdapter(Context context) {
		this.context = context;

		layoutInflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return 200;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.text, null);
			convertView.setTag(holder);

			holder.text = (TextView) convertView;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(String.valueOf(position));
		return convertView;
	}

	class ViewHolder {
		TextView text;
	}
}
