package com.novoda.imageloader.demo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.novoda.imageloader.core.model.ImageTagFactory;
import com.novoda.imageloader.demo.DemoApplication;
import com.novoda.imageloader.demo.R;
import com.novoda.imageloader.demo.activity.base.ImageLoaderBaseActivity;

import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Very similar to imageLongList example.
 */
@SuppressLint("NewApi")
public class CustomImageList extends ImageLoaderBaseActivity {

	private static final int SIZE = 150;
	LayoutInflater inflater;
	private BaseAdapter adapter;
	private Cursor cursor;
	@Override
	protected String getTableName() {
		return ImageLongList.class.getSimpleName().toLowerCase(Locale.UK);
	}

	@Override
	protected int getImageItemLayout() {
		return R.layout.image_item;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		inflater = (LayoutInflater)getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		super.onCreate(savedInstanceState);
		
		/**
		 * TODO Need to prepare imageLoader and imageTagFactory, generally we
		 * keep and instance of ImageManager and ImageTagFactory
		 */
		initImageLoader();
	}

	private void initImageLoader() {
		imageManager = DemoApplication.getImageLoader();
		imageTagFactory = createImageTagFactory();
		setAnimationFromIntent(imageTagFactory);
	}

	private ImageTagFactory createImageTagFactory() {
//		ImageTagFactory imageTagFactory = ImageTagFactory.newInstance();
//		imageTagFactory.setHeight(SIZE);
//		imageTagFactory.setWidth(SIZE);
        imageTagFactory = ImageTagFactory.newInstance(this, R.drawable.bg_img_loading);
//		imageTagFactory.setDefaultImageResId(R.drawable.bg_img_loading);
		imageTagFactory.setErrorImageId(R.drawable.bg_img_notfound);
		imageTagFactory.setSaveThumbnail(true);
		return imageTagFactory;
	}

	/**
	 * TODO Generally you will have a binder where you have to set the tag and
	 * load the image.
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

	@Override
	public void setAdapter() {
		cursor = getCursor();
        adapter = new UserAdapter();
        ViewBinder binder = getViewBinder();
//        if (binder != null) {
//        	adapter.setViewBinder(binder);
//        	adapter.
//        }
        view.setAdapter(adapter);
//		super.setAdapter();
	}
	
	class UserAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			Log.d("", "cursor.getCount()===>>"+cursor.getCount());
			return cursor.getCount();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("", "getView===>>"+position);
			if(convertView == null){
				convertView = inflater.inflate(R.layout.image_item, parent , false);		
			}
			cursor.moveToPosition(position);
			int columnIndex =cursor.getColumnIndex("url");
			String url = cursor.getString(columnIndex);
			setImageTag((ImageView) convertView.findViewById(R.id.list_item_image), url);
			loadImage((ImageView) convertView.findViewById(R.id.list_item_image));
			
			
			return convertView;
		}
		
	}
}