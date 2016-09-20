package com.novoda.imageloader.demo.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.novoda.imageloader.core.util.DirectLoader;
import com.novoda.imageloader.demo.R;

/**
 * Direct loading example.
 */
public class DirectLoading extends Activity {

	private ImageView imageView;

	private Animation fadeInAnimation;
	private Boolean isAnimated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direct_loading);

		if (getIntent().hasExtra("animated")) {
			isAnimated = true;
			fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		}

		imageView = (ImageView) findViewById(R.id.direct_image);
		fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * TODO Don't use the direct loader like this. It's useful for downloading a Bitmap to display in a notification. You need to handle the threading
		 * yourself.
		 */
		new Thread() {
			@Override
			public void run() {
				DirectLoader dl = new DirectLoader();
				Bitmap b = dl.download("http://www.asianweek.com/wp-content/uploads/2012/03/microsoft_logo11.jpg");
				setImageView(b);
			};
		}.start();
	}

	public void setImageView(final Bitmap bitmap) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				imageView.setImageBitmap(bitmap);

				// Start animating the image
				if (isAnimated) {
					imageView.startAnimation(fadeInAnimation);
				}

			}
		});
	}

}
