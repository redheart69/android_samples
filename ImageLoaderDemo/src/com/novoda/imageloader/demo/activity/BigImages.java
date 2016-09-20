package com.novoda.imageloader.demo.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.novoda.imageloader.core.OnImageLoadedListener;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.novoda.imageloader.demo.DemoApplication;
import com.novoda.imageloader.demo.R;
import com.novoda.imageloader.demo.activity.base.ImageLoaderBaseActivity;

import java.util.Locale;

/**
 * This is an example using really big images and see how the image loader can keep up with the memory limitations of android.
 */
public class BigImages extends ImageLoaderBaseActivity implements OnImageLoadedListener {

	private static final String TAG = DemoApplication.class.getSimpleName().toLowerCase(Locale.UK);

	@Override
	protected String getTableName() {
		return BigImages.class.getSimpleName().toLowerCase(Locale.UK);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * TODO Need to prepare imageLoader and imageTagFactory, generally we keep and instance of ImageManager and ImageTagFactory
         */
        initImageLoader();
	}

    private void initImageLoader() {
        imageManager = DemoApplication.getImageLoader();
        imageTagFactory = ImageTagFactory.newInstance(this, R.drawable.bg_img_loading);
        imageTagFactory.setErrorImageId(R.drawable.bg_img_notfound);
        setAnimationFromIntent(imageTagFactory);
    }

    @Override
	protected void onResume() {
		super.onResume();
		imageManager.setOnImageLoadedListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		imageManager.unRegisterOnImageLoadedListener(this);
	}

    @Override
    public void onImageLoaded(ImageView imageView) {
        Log.v(TAG, "onImageLoaded");
        Log.i(TAG, "ImageView URL : " + ((ImageTag) imageView.getTag()).getUrl());
    }

    /**
     * TODO Generally you will have a binder where you have to set the tag and load the image.
     */
    @Override
    protected ViewBinder getViewBinder() {
        return new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                String url = cursor.getString(columnIndex);
                setImageTag((ImageView) view, url);
                loadImage((ImageView) view);
                return true;
            }

        };
    }

    private void setImageTag(ImageView view, String url) {
        view.setTag(imageTagFactory.build(url, this));
    }

    private void loadImage(ImageView view) {
        imageManager.getLoader().load(view);
    }

}
