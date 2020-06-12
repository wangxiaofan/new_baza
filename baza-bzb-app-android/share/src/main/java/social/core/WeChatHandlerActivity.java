package social.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import social.core.presenter.WeChatPresenter;
import social.core.viewInterface.IWeChatView;

/**
 * Created by Vincent.Lei on 2016/12/9.
 * Title : 分享到微信中间过渡透明页
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class WeChatHandlerActivity extends Activity implements IWeChatView {
    private boolean mIsNotFirstShow;

    private WeChatPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mPresenter = new WeChatPresenter(this, this, getIntent());
        mPresenter.init();
        mPresenter.handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
        setIntent(intent);
        mPresenter.handleIntent(intent);
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
