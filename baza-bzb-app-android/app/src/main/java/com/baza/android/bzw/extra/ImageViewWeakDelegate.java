package com.baza.android.bzw.extra;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Vincent.Lei on 2017/11/1.
 * Title：
 * Note：
 */

public class ImageViewWeakDelegate {
    public WeakReference<ImageView> mImageViewWeakReference;
    public String mImageUrl;

    public ImageViewWeakDelegate(ImageView imageView, String imageUrl) {
        mImageViewWeakReference = new WeakReference<>(imageView);
        mImageUrl = imageUrl;
    }
}
