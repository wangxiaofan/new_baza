package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.widget.ClipImageView;
import com.slib.storage.file.FileManager;
import com.slib.storage.file.ImageManager;
import com.slib.storage.file.StorageType;
import com.slib.utils.SampleSizeUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/27.
 * Title：
 * Note：
 */

public class CropAvatarActivity extends BaseActivity implements View.OnClickListener {
    private ClipImageView clipImageView_avatar;

    private String mPath;
    private int mCropType;
    private Bitmap mSource;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_crop_avatar;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_corp_avatar);
    }

    @Override
    protected void initWhenCallOnCreate() {
        Intent intent = getIntent();
        mPath = intent.getStringExtra("mPath");
        mCropType = intent.getIntExtra("mCropType", ClipImageView.TYPE_CIRCLE);

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.corp_avatar);
        TextView tvRightClick = findViewById(R.id.tv_right_click);
        tvRightClick.setText(R.string.sure);

        clipImageView_avatar = findViewById(R.id.ClipImageView);
        clipImageView_avatar.setInterceptType(mCropType);
        clipImageView_avatar.setOutput(400, 400);
        clipImageView_avatar.post(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mPath, options);

                int inSampleSize = SampleSizeUtil.calculateSampleSize(options.outWidth, options.outHeight, 300, 300);
                options.inJustDecodeBounds = false;
                options.inSampleSize = inSampleSize;


                mSource = BitmapFactory.decodeFile(mPath, options);
                clipImageView_avatar.setImageBitmap(mSource);
            }
        });
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {


    }

    @Override
    protected void onActivityDeadForApp() {
        if (mSource != null && !mSource.isRecycled()) {
            mSource.recycle();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                Bitmap bitmapCorp = clipImageView_avatar.getInterceptImage();
                String cropPath = ImageManager.saveBitmapToLocal(bitmapCorp, FileManager.getFile("corp_avatar.jpg", StorageType.TYPE_CACHE, true).getAbsolutePath(), true);
                if (cropPath != null) {
                    Intent intent = new Intent();
                    intent.putExtra("cropPath", cropPath);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                break;
        }
    }

    public static String parseCropResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            return data.getStringExtra("cropPath");
        }
        return null;
    }

    public static void launch(Activity activity, String imagePath, int cropType, int requestCode) {
        Intent intent = new Intent(activity, CropAvatarActivity.class);
        intent.putExtra("mPath", imagePath);
        intent.putExtra("mCropType", cropType);
        activity.startActivityForResult(intent, requestCode);
    }

}
