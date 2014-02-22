package com.chienhsunlai.coverlayout;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;

import com.chienhsunlai.coverlayout.view.CoverViewGroup;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CoverViewGroup coverViewGroup = (CoverViewGroup) findViewById(R.id.coverViewGroup1);
		coverViewGroup.setAdapter(new TextAdapter(this));

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.imgres);
		coverViewGroup.setCoverBackground(bitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
