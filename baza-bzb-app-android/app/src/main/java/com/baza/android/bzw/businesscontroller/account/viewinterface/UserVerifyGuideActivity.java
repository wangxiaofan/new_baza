package com.baza.android.bzw.businesscontroller.account.viewinterface;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2019/2/27.
 * Title：
 * Note：
 */
public class UserVerifyGuideActivity extends BaseActivity implements View.OnClickListener {
    private Bitmap mBitmap;

    @Override
    protected int getLayoutId() {
        return R.layout.account_activity_user_verify_guide;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_user_verify_guide);
    }

    @Override
    protected void initWhenCallOnCreate() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_check_id);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.account_verify_guide, options);

        final ImageView imageView = findViewById(R.id.iv);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width = ScreenUtil.screenWidth;
        float ratio = options.outHeight * 1.0f / options.outWidth;
        lp.height = (int) (ratio * lp.width);
        imageView.setLayoutParams(lp);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.account_verify_guide);
                imageView.setImageBitmap(mBitmap);
            }
        });
    }

    @Override
    protected void onActivityDeadForApp() {
        super.onActivityDeadForApp();
        if (mBitmap != null)
            mBitmap.recycle();
        mBitmap = null;
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, UserVerifyGuideActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
