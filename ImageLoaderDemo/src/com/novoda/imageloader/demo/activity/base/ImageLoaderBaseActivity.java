package com.novoda.imageloader.demo.activity.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.novoda.imageloader.core.util.AnimationHelper;
import com.novoda.imageloader.demo.R;

@SuppressLint("NewApi")
public abstract class ImageLoaderBaseActivity extends Activity implements View.OnClickListener {

    private static final String[] CURSOR_FROM = new String[]{"url"};
    private static final int[] CURSOR_TO = new int[]{R.id.list_item_image};

    protected ImageTagFactory imageTagFactory;
    protected ImageManager imageManager;

    private boolean useCacheFlag;
    private Button cacheModeButton;

    protected AbsListView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_list_activity_layout);
        initView();
        initButtons();
        setAdapter();
    }

    private void initView() {
        view = getListTypeFromIntent();
        view.setVisibility(View.VISIBLE);

    }

    private AbsListView getListTypeFromIntent() {
        if (intentIsGrid()) {
            return (GridView) findViewById(R.id.grid_view);
        } else {
            return (ListView) findViewById(R.id.list_view);
        }
    }

    private boolean intentIsGrid() {
        return getIntent().hasExtra("grid") && getIntent().getBooleanExtra("grid", false);
    }

    private void initButtons() {
        Button button = (Button) this.findViewById(R.id.refresh_button);
        button.setOnClickListener(this);
        cacheModeButton = (Button) this.findViewById(R.id.cache_mode_button);
        cacheModeButton.setOnClickListener(this);
    }

    public void setAdapter() {
        SimpleCursorAdapter adapter = initAdapter();
        ViewBinder binder = getViewBinder();
        if (binder != null) {
            adapter.setViewBinder(binder);
        }
        view.setAdapter(adapter);
    }

    protected abstract ViewBinder getViewBinder();

    protected SimpleCursorAdapter initAdapter() {
        return new SimpleCursorAdapter(this, getImageItemLayout(), getCursor(), CURSOR_FROM, CURSOR_TO);
    }

    protected int getImageItemLayout() {
        return R.layout.image_item;
    }

    protected Cursor getCursor() {
        return managedQuery(Uri.parse("content://com.novoda.imageloader.demo/" + getTableName()), null, null, null, null);
    }

    protected abstract String getTableName();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_button:
                refreshData();
                break;
            case R.id.cache_mode_button:
                useCacheFlag = !useCacheFlag;
                setCacheModeText();
                imageTagFactory.setUseOnlyCache(useCacheFlag);
                break;
        }
    }

    private void refreshData() {
        ((SimpleCursorAdapter) view.getAdapter()).notifyDataSetChanged();
    }

    private void setCacheModeText() {
        if (useCacheFlag) {
            cacheModeButton.setText(getString(R.string.turn_off_cache_only));
        } else {
            cacheModeButton.setText(getString(R.string.cache_only));
        }
    }

    protected void setAnimationFromIntent(ImageTagFactory imageTagFactory) {
        if (intentHasAnimation()) {
            imageTagFactory.setAnimation(getIntent().getIntExtra("animated", AnimationHelper.ANIMATION_DISABLED));
        }
    }

    private boolean intentHasAnimation() {
        return getIntent().hasExtra("animated") &&
                getIntent().getIntExtra("animated", AnimationHelper.ANIMATION_DISABLED) != AnimationHelper.ANIMATION_DISABLED;
    }

}
