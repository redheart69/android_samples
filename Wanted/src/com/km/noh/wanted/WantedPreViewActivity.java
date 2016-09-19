package com.km.noh.wanted;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


public class WantedPreViewActivity extends Activity {
	// keep track of camera capture intent
	final int GALLERY_CAPTURE = 0;
	final int CAMERA_CAPTURE = 1;
	// keep track of cropping intent
	final int PIC_CROP = 2;
	// captured picture uri
	private Uri _mImageCaptureUri;
	private Uri _mImageCropUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wanted_main);
		Log.d("", "onContextItemSelected3333");
		ImageView vMain = (ImageView)findViewById(R.id.img_main);
//		Object bm =getIntent().getExtras().get("bmp");
//		Log.d("", "onContextItemSelected=====>>"+bm);
//		Bitmap bm =(Bitmap)getIntent().getExtras().get("bmp");
//		vMain.setImageBitmap(WantedMainActivity._bmp);
	}

}
