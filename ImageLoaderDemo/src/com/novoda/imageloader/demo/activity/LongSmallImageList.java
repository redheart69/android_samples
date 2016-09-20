package com.novoda.imageloader.demo.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.novoda.imageloader.core.model.ImageTagFactory;
import com.novoda.imageloader.demo.DemoApplication;
import com.novoda.imageloader.demo.R;
import com.novoda.imageloader.demo.activity.base.ImageLoaderBaseActivity;

import java.util.Locale;

/**
 * Very similar to imageLongList example.
 */
public class LongSmallImageList extends ImageLoaderBaseActivity {

	private static final int SIZE = 80;

	@Override
	protected String getTableName() {
		return LongSmallImageList.class.getSimpleName().toLowerCase(Locale.UK);
	}

	@Override
	protected int getImageItemLayout() {
		return R.layout.small_image_item;
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
        imageTagFactory = createImageTagFactory();
        setAnimationFromIntent(imageTagFactory);
    }

    private ImageTagFactory createImageTagFactory() {
		ImageTagFactory imageTagFactory = ImageTagFactory.newInstance();
		imageTagFactory.setHeight(SIZE);
		imageTagFactory.setWidth(SIZE);
		imageTagFactory.setDefaultImageResId(R.drawable.bg_img_loading);
		imageTagFactory.setErrorImageId(R.drawable.bg_img_notfound);
		imageTagFactory.setSaveThumbnail(true);
		return imageTagFactory;
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