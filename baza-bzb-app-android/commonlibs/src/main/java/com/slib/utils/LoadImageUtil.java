package com.slib.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.baza.android.slib.R;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.slib.http.GlideApp;


/**
 * Created by LW on 2016/5/26.
 * Title :
 * Note :
 */
public class LoadImageUtil {
    public interface ILoadBitmapListener {
        void onResourceReady(Bitmap resource);
    }

    public interface ILoadListener {
        void onResourceReady(Bitmap resource);

        void onLoadFailed();
    }

    private static Application mApplication;

    private LoadImageUtil() {
    }

    public static void init(Application application) {
        mApplication = application;
    }

    public static void loadImage(String url, ImageView imageView) {
        loadImage(mApplication, url, R.drawable.default_empty_image, imageView);
    }

    public static void loadImage(String url, int placeHolderDrawableId, ImageView imageView) {
        loadImage(mApplication, url, placeHolderDrawableId, imageView);
    }

    public static void loadImage(Context context, String url, int placeHolderDrawableId, ImageView imageView) {
        if (placeHolderDrawableId > 0)
            GlideApp.with(context).asBitmap().placeholder(placeHolderDrawableId).error(placeHolderDrawableId).load(url).into(imageView);
        else
            GlideApp.with(context).asBitmap().load(url).into(imageView);
    }

    public static void loadImage(int drawableId, ImageView imageView) {
        loadImage(mApplication, drawableId, R.drawable.default_empty_image, imageView);
    }

    public static void loadImage(int drawableId, int placeHolderDrawableId, ImageView imageView) {
        loadImage(mApplication, drawableId, placeHolderDrawableId, imageView);
    }

    public static void loadImage(Context context, int drawableId, int placeHolderDrawableId, ImageView imageView) {
        GlideApp.with(context).asBitmap().placeholder(placeHolderDrawableId).error(placeHolderDrawableId).load(drawableId).into(imageView);
    }

    public static void loadImage(String url, int width, int height, final ILoadBitmapListener listener) {
        loadImage(mApplication, url, width, height, listener);
    }

    public static void loadImage(Context context, String url, int width, int height, final ILoadBitmapListener listener) {
        GlideApp.with(context).asBitmap().load(url).into(new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (listener != null)
                    listener.onResourceReady(resource);
            }
        });
    }

    public static void loadImage(String url, final ILoadListener loadListener) {
        loadImage(mApplication, url, loadListener);
    }

    public static void loadImage(Context context, String url, final ILoadListener loadListener) {
        GlideApp.with(context).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                if (loadListener != null)
                    loadListener.onLoadFailed();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                if (loadListener != null)
                    loadListener.onResourceReady(resource);
                return false;
            }
        }).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

            }
        });
    }

    public static void loadBase64Image(String base, int placeHolderDrawableId, ImageView imageView) {
        if (TextUtils.isEmpty(base)) {
            imageView.setImageResource(placeHolderDrawableId);
            return;
        }
        Bitmap bitmap = null;
        byte[] bitmapArray;
        bitmapArray = Base64.decode(base, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        imageView.setImageBitmap(bitmap);//显示图片
    }
}
