package com.km.noh.wanted;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class WantedMainActivity extends Activity implements OnClickListener {
	// keep track of camera capture intent
	final int GALLERY_CAPTURE = 0;
	final int CAMERA_CAPTURE = 1;
	// keep track of cropping intent
	final int PIC_CROP = 2;
	// captured picture uri
	private Uri _mImageCaptureUri;
	private Uri _mImageCropUri;
	private EditText _txtName;
	private EditText _txtMoney;
	private AdView adView;
	private static final String MY_AD_UNIT_ID = "a1516ce50805d77";
	private MediaPlayer _mp;

	private static final String PATH_IMG_TEMP = Environment.getExternalStorageDirectory() + "/wanted/preview";
	private static final String PATH_IMG_SAVE = Environment.getExternalStorageDirectory() + "/wanted/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wanted_main);
		_txtName = (EditText) findViewById(R.id.label_name);
		_txtMoney = (EditText) findViewById(R.id.label_money);
		Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/bernhc.ttf");
		_txtName.setTypeface(typeFace);
		_txtMoney.setTypeface(typeFace);
		findViewById(R.id.img_main).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);
		AdSize adSize = new AdSize(AdSize.FULL_WIDTH, AdSize.PORTRAIT_AD_HEIGHT);
		// adView 만들기
		adView = new AdView(this, adSize, MY_AD_UNIT_ID);

		// android:id="@+id/mainLayout" 속성이
		// 지정된 것으로 가정하여 LinearLayout 찾기
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_ad);

		// 찾은 LinearLayout에 adView를 추가
		layout.addView(adView);

		// 기본 요청을 시작하여 광고와 함께 요청을 로드
		adView.loadAd(new AdRequest());
		_mp = MediaPlayer.create(this, R.raw.wanted_bgm);
	}

	/**
	 * Click method to handle user pressing button to launch camera
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_main:
			new AlertDialog.Builder(this).setItems(new String[] { "앨범", "사진찍기" }, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						viewGallery();
						break;
					case 1:
						viewCamera();
						break;
					}
				}
			}).show();
			break;
		case R.id.layout_name:
			break;
		case R.id.layout_money:
			break;
		case R.id.btn_save:
			saveFile();
			break;
		case R.id.btn_share:

			sendImage(makePreviewImage());
			break;

		}
	}

	private Bitmap makePreviewImage() {
		View vMain = findViewById(R.id.layout_main);
		vMain.destroyDrawingCache();
		_txtName.setFocusable(false);
		_txtMoney.setFocusable(false);
		NumberFormat formatter = NumberFormat.getInstance(Locale.US);
		String numberFormat = "";
		String moneyVal = _txtMoney.getText().toString();
		if ("".equalsIgnoreCase(moneyVal) == false) {
			numberFormat = formatter.format(Long.parseLong(moneyVal.replace(",", "")));
		}
		_txtMoney.setText(numberFormat);
		Log.d("", "_txtMoney====>>" + numberFormat);
		vMain.setDrawingCacheEnabled(true);
		vMain.buildDrawingCache(true);
		Bitmap bmp = Bitmap.createBitmap(vMain.getDrawingCache());
		_txtName.setFocusable(true);
		_txtName.setFocusableInTouchMode(true);
		_txtMoney.setFocusable(true);
		_txtMoney.setFocusableInTouchMode(true);
		return bmp;
	}

	/**
	 * Handle user returning from both capturing and cropping the image
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// Log.e("", "dataUri>>>"+data.getData());
			// Log.e("", "data>>>"+data.getParcelableExtra("data"));

			switch (requestCode) {
			case GALLERY_CAPTURE:
				_mImageCaptureUri = data.getData();
			case CAMERA_CAPTURE:
				performCrop();
				break;
			case PIC_CROP:
				try {
					Log.d("", "_mImageCropUri====>>" + _mImageCropUri);
					ImageView photoView = (ImageView) findViewById(R.id.img_main);
					photoView.setImageURI(_mImageCropUri);

				} catch (Exception e) {
					Log.d("", "PIC_CROP======>>ERROR");
					e.printStackTrace();
				}

				break;
			}
		}
	}

	protected void makeTemporaryDir() {
		File tmpDir = new File(PATH_IMG_TEMP);
		tmpDir.mkdirs();
		File nomedia = new File(PATH_IMG_TEMP + "/.nomedia");
		try {
			if (nomedia.exists() == false)
				nomedia.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected File saveFile() {
		makeTemporaryDir();
		File resultFile = null;
		try {
			File f = SaveBitmapToFileCache(makePreviewImage(), PATH_IMG_SAVE + "/img_" + System.currentTimeMillis() + ".png");
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
			Toast.makeText(WantedMainActivity.this, "Save Complete", Toast.LENGTH_SHORT).show();
			// Log.d("", "makeTemporaryFile======>>" + resultFile.exists());
		} catch (Exception e) {
			Toast.makeText(WantedMainActivity.this, "Save Failed", Toast.LENGTH_SHORT).show();
			// Log.d("", "makeTemporaryFile======>>ERROR");
		}
		return resultFile;
	}

	protected File makeTemporaryFile() {
		makeTemporaryDir();
		File resultFile = null;
		try {
			resultFile = new File(PATH_IMG_TEMP + "/crop_img_" + System.currentTimeMillis());
			Log.d("", "makeTemporaryFile======>>" + resultFile.exists());
		} catch (Exception e) {
			Log.d("", "makeTemporaryFile======>>ERROR");
		}
		return resultFile;
	}

	@Override
	protected void onResume() {
		if (_mp == null) {
			_mp = MediaPlayer.create(this, R.raw.wanted_bgm);
		}
		_mp.setLooping(true);
		_mp.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (_mp != null) {
			_mp.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (_mp != null) {
			_mp.stop();
			_mp.release();
		}
		deleteFile();
		super.onDestroy();
	}

	private void deleteFile() {
		File tmpDir = new File(PATH_IMG_TEMP);
		try {
			if (tmpDir.exists()) {
				File[] fList = tmpDir.listFiles();
				for (int i = 0; i < fList.length; i++) {
					fList[i].delete();
				}
				tmpDir.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to carry out crop operation
	 */
	private void performCrop() {
		// take care of exceptions
		try {
			File tempFile = makeTemporaryFile();
			_mImageCropUri = Uri.fromFile(tempFile);
			// call the standard crop action intent (the user device may not
			// support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(_mImageCaptureUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 17);
			cropIntent.putExtra("aspectY", 13);
			// indicate output X and Y
			// cropIntent.putExtra("outputX", 340);
			// cropIntent.putExtra("outputY", 260);
			// // retrieve data on return
			// cropIntent.putExtra("return-data", true);
			cropIntent.putExtra("output", _mImageCropUri);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void viewCamera() {
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String url = "tmp_image.png";
			_mImageCaptureUri = Uri.fromFile(new File(PATH_IMG_TEMP, url));

			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, _mImageCaptureUri);
			// intent.putExtra("return-data", true);
			startActivityForResult(intent, CAMERA_CAPTURE);
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support capturing images!";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void viewGallery() {
		try {
			Intent intent = new Intent();
			// intent.setType("image/*");
			intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_CAPTURE);
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support capturing images!";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	void sendImage(Bitmap bmp) {
		makeTemporaryDir();
		File fPreview = SaveBitmapToFileCache(bmp, PATH_IMG_TEMP + "/img_preview.png");
		Uri dataUri = Uri.fromFile(fPreview);

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_STREAM, dataUri);
		// intent.setPackage("com.kakao.talk");
		startActivity(intent);
	}

	private File SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
		File fileCacheItem = new File(strFilePath);
		OutputStream out = null;
		try {
			fileCacheItem.createNewFile();
			out = new FileOutputStream(fileCacheItem);
			bitmap.compress(CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileCacheItem;
	}
}
