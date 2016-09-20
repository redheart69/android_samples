package com.novoda.imageloader.demo.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.novoda.imageloader.core.model.ImageTagFactory;
import com.novoda.imageloader.demo.DemoApplication;
import com.novoda.imageloader.demo.R;
import com.novoda.imageloader.demo.activity.base.ImageLoaderBaseActivity;

import java.util.Locale;

/**
 * Example of setting a specific image size. Not that you can ask the imageLoader to store the small image as files. In this way you don't need to scale images
 * every time
 */
public class ImageLongList extends ImageLoaderBaseActivity {

	private static final int SIZE = 400;

	@Override
	protected String getTableName() {
		return ImageLongList.class.getSimpleName().toLowerCase(Locale.UK);
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
        imageTagFactory = ImageTagFactory.newInstance(SIZE, SIZE, R.drawable.bg_img_loading);
        imageTagFactory.setErrorImageId(R.drawable.bg_img_notfound);
        imageTagFactory.setSaveThumbnail(true);
        setAnimationFromIntent(imageTagFactory);
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
                Log.d("", ">>"+url);
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