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
package com.novoda.imageloader.core.loader.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.OnImageLoadedListener;
import com.novoda.imageloader.core.exception.ImageNotFoundException;
import com.novoda.imageloader.core.model.ImageWrapper;

import java.io.File;
import java.lang.ref.WeakReference;

public class LoaderTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageWrapper imageWrapper;
    private final LoaderSettings loaderSettings;
    private final WeakReference<OnImageLoadedListener> onImageLoadedListener;

    private String url;
    private boolean saveScaledImage;
    private boolean useCacheOnly;
    private int width;
    private int height;
    private int notFoundResourceId;

    public LoaderTask(ImageWrapper imageWrapper, LoaderSettings loaderSettings) {
        this(imageWrapper, loaderSettings, null);
    }

    public LoaderTask(ImageWrapper imageWrapper, LoaderSettings loaderSettings, WeakReference<OnImageLoadedListener> onImageLoadedListener) {
        this.imageWrapper = imageWrapper;
        this.loaderSettings = loaderSettings;
        this.onImageLoadedListener = onImageLoadedListener;
    }

    @Override
    protected Bitmap doInBackground(String... args) {
        if (imageWrapper == null) {
            return null;
        }

        setTagInformation(imageWrapper);

        if (url == null || url.length() <= 0 || url.equals("_url_error")) {
            return getNotFoundImage(imageWrapper.getContext());
        }

        if (hasImageViewUrlChanged(imageWrapper)) {
            return null;
        }

        File imageFile = getImageFile(imageWrapper);
        if (!imageFile.exists()) {
            if (useCacheOnly) {
                return null;
            }
            try {
                loaderSettings.getNetworkManager().retrieveImage(url, imageFile);
            } catch (ImageNotFoundException inf) {
                return getNotFoundImage(imageWrapper.getContext());
            }
        }
        if (hasImageViewUrlChanged(imageWrapper)) {
            return null;
        }
        return getImageFromFile(imageFile);
    }

    private Bitmap getImageFromFile(File imageFile) {
        Bitmap b;
        if (loaderSettings.isAlwaysUseOriginalSize()) {
            b = loaderSettings.getBitmapUtil().decodeFile(imageFile, width, height);
        } else {
            b = loaderSettings.getBitmapUtil().decodeFileAndScale(imageFile, width, height, loaderSettings.isAllowUpsampling());
        }

        if (b == null) {
            // decoding failed
            return b;
        }

        if (saveScaledImage) {
            saveScaledImage(imageFile, b);
        }
        loaderSettings.getCacheManager().put(url, b);
        return b;
    }

    private void setTagInformation(ImageWrapper imageWrapper) {
        url = imageWrapper.getUrl();
        width = imageWrapper.getWidth();
        height = imageWrapper.getHeight();
        notFoundResourceId = imageWrapper.getNotFoundResourceId();
        useCacheOnly = imageWrapper.isUseCacheOnly();
    }

    private void saveScaledImage(File imageFile, Bitmap b) {
        loaderSettings.getFileManager().saveBitmap(imageFile.getAbsolutePath(), b, width, height);
    }

    private File getImageFile(ImageWrapper imageWrapper) {
        File imageFile = null;
        if (imageWrapper.isSaveThumbnail()) {
            imageFile = loaderSettings.getFileManager().getFile(url, width, height);
        }
        if (imageFile == null || !imageFile.exists()) {
            imageFile = loaderSettings.getFileManager().getFile(url);
            if (imageWrapper.isSaveThumbnail()) {
                saveScaledImage = true;
            }
        }
        return imageFile;
    }

    private boolean hasImageViewUrlChanged(ImageWrapper imageWrapper) {
        if (url == null) {
            return false;
        } else {
            return !url.equals(imageWrapper.getCurrentUrl());
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        if (isCancelled()) {
            bitmap = null;
            return;
        }

        if (validateImageView(imageWrapper)) {
            listenerCallback(imageWrapper);
            imageWrapper.setBitmap(bitmap);
        }
    }

    private boolean validateImageView(ImageWrapper imageWrapper) {
        if (imageWrapper == null || hasImageViewUrlChanged(imageWrapper) || imageWrapper.getLoaderTask() != this) {
            return false;
        }
        return true;
    }

    private void listenerCallback(ImageWrapper imageWrapper) {
        if (onImageLoadedListener != null && onImageLoadedListener.get() != null) {
            onImageLoadedListener.get().onImageLoaded(imageWrapper.getImageView());
        }
    }

    private Bitmap getNotFoundImage(Context c) {
        String key = "resource" + notFoundResourceId + width + height;
        Bitmap b = loaderSettings.getResCacheManager().get(key, width, height);
        if (b != null) {
            return b;
        }
        if (loaderSettings.isAlwaysUseOriginalSize()) {
            b = loaderSettings.getBitmapUtil().decodeResourceBitmap(c, width, height, notFoundResourceId);
        } else {
            b = loaderSettings.getBitmapUtil().decodeResourceBitmapAndScale(c, width, height, notFoundResourceId, loaderSettings.isAllowUpsampling());
        }
        loaderSettings.getResCacheManager().put(key, b);
        return b;
    }

    public String getUrl() {
        return url;
    }

}
