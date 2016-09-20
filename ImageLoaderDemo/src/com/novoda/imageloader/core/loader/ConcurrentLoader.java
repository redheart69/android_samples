/**
 * Copyright 2012 Novoda Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novoda.imageloader.core.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.OnImageLoadedListener;
import com.novoda.imageloader.core.exception.ImageNotFoundException;
import com.novoda.imageloader.core.loader.util.LoaderTask;
import com.novoda.imageloader.core.model.ImageWrapper;

import java.lang.ref.WeakReference;

public class ConcurrentLoader implements Loader {

    private final LoaderSettings loaderSettings;

    private WeakReference<OnImageLoadedListener> onImageLoadedListener;

    public ConcurrentLoader(LoaderSettings loaderSettings) {
        this.loaderSettings = loaderSettings;
    }

    @Override
    public void load(ImageView imageView) {
        if (!isValidImageView(imageView)) {
            Log.w("ImageLoader", "You should never call load if you don't set a ImageTag on the view");
            return;
        }
        loadBitmap(new ImageWrapper(imageView));
    }

    private boolean isValidImageView(ImageView imageView) {
        return imageView.getTag() != null;
    }

    private void loadBitmap(ImageWrapper w) {
        if (!isTaskAlreadyRunning(w)) {
            if (isBitmapAlreadyInCache(getCachedBitmap(w))) {
                Bitmap cachedBitmap = getCachedBitmap(w);
                w.setBitmap(cachedBitmap);
                return;
            }
            setDefaultImage(w);
            if (!w.isUseCacheOnly()) {
                startTask(w);
            }
        }
    }

    private boolean isBitmapAlreadyInCache(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }

    private Bitmap getCachedBitmap(ImageWrapper w) {
        return loaderSettings.getCacheManager().get(w.getUrl(), w.getHeight(), w.getWidth());
    }

    private void setDefaultImage(ImageWrapper w) {
        if (hasPreviewUrl(w.getPreviewUrl())) {
            if (isBitmapAlreadyInCache(getPreviewCachedBitmap(w))) {
                w.setBitmap(getPreviewCachedBitmap(w));
            } else {
                w.setResourceBitmap(getResourceAsBitmap(w, w.getLoadingResourceId()));
            }
        } else {
            w.setResourceBitmap(getResourceAsBitmap(w, w.getLoadingResourceId()));
        }
    }

    private Bitmap getPreviewCachedBitmap(ImageWrapper w) {
        return loaderSettings.getCacheManager().get(w.getPreviewUrl(), w.getPreviewHeight(), w.getPreviewWidth());
    }

    private void startTask(ImageWrapper w) {
        try {
            LoaderTask task = createTask(w);
            w.setLoaderTask(task);
            task.execute();
        } catch (ImageNotFoundException inf) {
            w.setResourceBitmap(getResourceAsBitmap(w, w.getNotFoundResourceId()));
        } catch (Throwable t) {
            w.setResourceBitmap(getResourceAsBitmap(w, w.getNotFoundResourceId()));
        }
    }

    private Bitmap getResourceAsBitmap(ImageWrapper w, int resId) {
        Bitmap b = loaderSettings.getResCacheManager().get("" + resId, w.getWidth(), w.getHeight());
        if (b != null) {
            return b;
        }
        b = loaderSettings.getBitmapUtil().decodeResourceBitmapAndScale(w, resId, loaderSettings.isAllowUpsampling());
        loaderSettings.getResCacheManager().put(String.valueOf(resId), b);
        return b;
    }

    private boolean hasPreviewUrl(String previewUrl) {
        return previewUrl != null;
    }

    private LoaderTask createTask(ImageWrapper imageWrapper) {
        return onImageLoadedListener == null ? new LoaderTask(imageWrapper, loaderSettings) :
                new LoaderTask(imageWrapper, loaderSettings, onImageLoadedListener);
    }

    @Override
    public void setLoadListener(WeakReference<OnImageLoadedListener> onImageLoadedListener) {
        this.onImageLoadedListener = onImageLoadedListener;
    }

    /**
     * checks whether a previous task is loading the same url
     *
     * @param imageWrapper url of the image to be fetched
     *                     task that might already fetching an image, might be null
     * @return false if there is no other concurrent task running
     */

    private static boolean isTaskAlreadyRunning(ImageWrapper imageWrapper) {
        LoaderTask oldTask = imageWrapper.getLoaderTask();
        if (oldTask == null) {
            return false;
        }

        if ((!imageWrapper.getUrl().equals(oldTask.getUrl()))) {
            return true;
        }
        oldTask.cancel(true);
        return false;
    }

}
