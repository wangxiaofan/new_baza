package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.message.extra.MessageImageLoader;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.LogUtil;
import com.slib.storage.file.ImageManager;
import com.slib.storage.file.RootStorage;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ToastUtil;
import com.bm.library.PhotoView;
import com.bznet.android.rcbox.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2018/7/23.
 * Title：
 * Note：
 */
public class LargeImageActivity extends BaseActivity {

    public static final String TRANSITION_NAME = "photoView";
    private int mPositionInit;
    private boolean mIsImMessageType;
    private ArrayList<String> mImageUrls;
    private MessageImageLoader mMessageImageLoader;
    private ImageListAdapter mAdapter;
    ViewPager viewPager;
    TextView textView_save;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_large_imgae;
    }

    @Override
    protected String getPageTitle() {
        return "浏览大图UI";
    }

    @Override
    protected void onActivityDeadForApp() {
        super.onActivityDeadForApp();
        if (mMessageImageLoader != null)
            mMessageImageLoader.destroy();
    }

    @Override
    protected void initWhenCallOnCreate() {
        Intent intent = getIntent();
        mImageUrls = (ArrayList<String>) intent.getSerializableExtra("imageUrls");
        mPositionInit = intent.getIntExtra("position", 0);
        mIsImMessageType = intent.getBooleanExtra("imMessageType", false);
        viewPager = findViewById(R.id.view_pager);

        if (mIsImMessageType) {
            textView_save = findViewById(R.id.tv_save);
            mImageUrls = new ArrayList<>(1);
            mImageUrls.add(null);
            mMessageImageLoader = new MessageImageLoader(this, (BZWIMMessage) mApplication.getCachedTransformData(CommonConst.STR_TRANSFORM_IM_IMAGE_LOAD), new MessageImageLoader.IMessageImageLoadListener() {
                @Override
                public void onMessageImageLoaded(boolean success, String path) {
                    if (success) {
                        mImageUrls.set(0, path);
                        mAdapter.refresh(viewPager.getChildAt(0), 0);
                        textView_save.setVisibility(View.VISIBLE);
                    }
                }
            });
            textView_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SaveImageTask(mApplication, mMessageImageLoader.getPath()).execute();
                }
            });

            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    mMessageImageLoader.download();
                }
            });
        }

        mAdapter = new ImageListAdapter(mImageUrls, this);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(mPositionInit, false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            viewPager.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public boolean onPreDraw() {
                            //启动动画
                            viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                            startPostponedEnterTransition();

                            return true;
                        }
                    });
        }
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    public static void launch(Activity activity, View v, final String imageUrl) {
        launch(activity, v, new ArrayList<String>(1) {{
            add(imageUrl);
        }}, 0);
    }

    public static void launch(Activity activity, View v, ArrayList<String> imageUrls, int position) {
        launch(activity, v, imageUrls, position, false);
    }

    public static void launch(Activity activity, View v, ArrayList<String> imageUrls, int position, boolean imMessageType) {
        Intent intent = new Intent(activity, LargeImageActivity.class);
        intent.putExtra("imageUrls", imageUrls);
        intent.putExtra("position", position);
        intent.putExtra("imMessageType", imMessageType);
        activity.overridePendingTransition(0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, TRANSITION_NAME);
            activity.startActivity(intent, activityOptionsCompat.toBundle());
        } else
            activity.startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.anim_large_image_out);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class ImageListAdapter extends PagerAdapter implements View.OnClickListener {
        private ArrayList<String> mImageUrls;
        private Context mContext;

        private ImageListAdapter(ArrayList<String> imageUrls, Context mContext) {
            this.mImageUrls = imageUrls;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mImageUrls == null ? 0 : mImageUrls.size();
        }

        @Override
        public void onClick(View view) {
            onBackPressed();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View convertView = View.inflate(mContext, R.layout.large_image_item, null);
            refresh(convertView, position);
            container.addView(convertView);
            return convertView;
        }

        public void refresh(View convertView, int position) {
            final ImageView imageViewBg = convertView.findViewById(R.id.iv_bg);
            final PhotoView mPhotoView = convertView.findViewById(R.id.photoView);
            final ProgressBar mProgressBar = convertView.findViewById(R.id.loading);
            final String url = mImageUrls.get(position);
            LogUtil.d(url);
            mPhotoView.enable();
            mPhotoView.setOnClickListener(this);
            mPhotoView.setTag(R.id.hold_tag_id_one, url);
            if (mPositionInit == position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    imageViewBg.setTransitionName(TRANSITION_NAME);
                if (mPhotoView.getVisibility() != View.VISIBLE)
                    imageViewBg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageViewBg.setVisibility(View.GONE);
                            mPhotoView.setVisibility(View.VISIBLE);
                        }
                    }, 500);
            } else {
                imageViewBg.setVisibility(View.GONE);
                mPhotoView.setVisibility(View.VISIBLE);
            }
            if (url != null) {
                LoadImageUtil.loadImage(mContext.getApplicationContext(), url, new LoadImageUtil.ILoadListener() {
                    @Override
                    public void onResourceReady(Bitmap resource) {
                        if (url.equals(mPhotoView.getTag(R.id.hold_tag_id_one).toString())) {
                            mProgressBar.setVisibility(View.GONE);
                            mPhotoView.setImageBitmap(resource);
                            imageViewBg.setImageBitmap(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    private static class SaveImageTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private String path;
        private String savePath;

        private SaveImageTask(Context mContext, String path) {
            this.mContext = mContext;
            this.path = path;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File file = new File(path);
            if (file.exists()) {
                String name = file.getName();
                if (!name.contains("."))
                    name = name + ".jpg";
                boolean hasExtraStorage = RootStorage.hasExtraStorage();
                File filePath = new File((hasExtraStorage ? Environment.getExternalStorageDirectory().getPath() : Environment.getDataDirectory().getPath()) + File.separator + "Picture");
                if (!filePath.exists())
                    filePath.mkdirs();
                File fileSave = null;
                FileOutputStream fileOutputStream = null;
                try {
                    fileSave = new File(filePath.getAbsolutePath() + File.separator + name);
                    if (fileSave.exists()) {
                        savePath = fileSave.getAbsolutePath();
                        return null;
                    }
                    fileOutputStream = new FileOutputStream(fileSave);
                    Bitmap bitmap = ImageManager.decodeBitmap(path, 600, 800);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileOutputStream = null;
                    bitmap.recycle();
                    savePath = fileSave.getAbsolutePath();
                } catch (Exception e) {
                    if (fileSave != null && fileSave.exists())
                        fileSave.delete();
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null)
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (savePath != null) {
                LogUtil.d(savePath);
                ToastUtil.showToast(mContext, R.string.save_image_to_alum_success);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(savePath)));
                mContext.sendBroadcast(intent);
            }
        }
    }
}
