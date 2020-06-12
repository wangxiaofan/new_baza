package social.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import social.core.presenter.QQPresenter;
import social.core.viewInterface.IQQView;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2016/12/8.
 * Title : 分享到QQ中间过渡透明页
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class ShareToQQActivity extends Activity implements IQQView {

    private ShareData mShareData;
    private QQPresenter mPresenter;
    private boolean mIsNotFirstShow;

    public static void launch(Context context, ShareData shareData) {
        Intent intent = new Intent(context, ShareToQQActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("shareData", shareData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mShareData = (ShareData) getIntent().getSerializableExtra("shareData");
        mPresenter = new QQPresenter(mShareData, this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsNotFirstShow) {
            callClosePage();
            return;
        }
        mIsNotFirstShow = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onTencentResult(requestCode, resultCode, data);
    }

    @Override
    public void callClosePage() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.setResultBack();
        super.onDestroy();
    }
}
