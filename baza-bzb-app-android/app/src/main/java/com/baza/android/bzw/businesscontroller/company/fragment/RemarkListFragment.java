package com.baza.android.bzw.businesscontroller.company.fragment;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.businesscontroller.audio.tools.AudioPlayerTool;
import com.baza.android.bzw.businesscontroller.company.adapter.RemarkListAdapter;
import com.baza.android.bzw.businesscontroller.resume.detail.RemarkTipsPopupWindow;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.dialog.AddRemarkDialog;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;

import java.io.File;

/**
 * 寻访列表
 */
public class RemarkListFragment extends BaseFragment implements BaseBZWAdapter.IAdapterEventsListener {

    private ResumeDetailPresenter mPresenter;

    private IResumeDetailView mResumeDetailView;

    private ListView listView;

    private TextView textView;

    private RemarkListAdapter listAdapter;

    private String[] audioMenuMine;

    private String[] voiceMenuOther;

    private String[] textMenuMine;

    private String[] textMenuWuxiao;

    private RemarkTipsPopupWindow mRemarkTipsPopupWindow;

    public RemarkListFragment(IResumeDetailView resumeDetailView, ResumeDetailPresenter presenter) {
        this.mResumeDetailView = resumeDetailView;
        this.mPresenter = presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_remark_list;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        textView = mRootView.findViewById(R.id.tv_nodata);
        listView = mRootView.findViewById(R.id.lv_remark_list);
        listAdapter = new RemarkListAdapter(getActivity(), false, mPresenter.getRemarkListData(), this);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void initWhenOnActivityCreated() {

    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    public void updateData(int targetPosition) {
        if (mPresenter.getRemarkListData().size() > 0) {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        if (targetPosition < 0) {
            listAdapter.refreshTargetView(targetPosition, null, false);
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view == null)
            return;
        listAdapter.refreshTargetView(targetPosition, view, false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_SHOW_EDIT_REMARK_TIPS_MENU:
                //显示编辑文本备注的对话框
                showRemarkTipsMenu(position, v, (RemarkBean) input);
                break;
            case AdapterEventIdConst.EVENT_ID_PLAY_RECORD:
            case AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE:
                mPresenter.playVoice((File) input, adapterEventId == AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE);
                mPresenter.getResumeLogger().sendPlayAudioRemark(this, mPresenter.getCurrentResumeData().candidateId, mPresenter.getRemarkListData().get(position).inquiryId);
                break;
            case AdapterEventIdConst.EVENT_ID_REMARK_AUDIO_LOCAL_SOURCE_NOT_EXIST:
                mPresenter.loadRemarkAudioSourceFile((RemarkBean) input);
                break;
            case AdapterEventIdConst.EVENT_ID_STOP_PLAY_RECORD:
                mPresenter.stopPlayRecord();
                break;
            case AdapterEventIdConst.EVENT_ID_PAUSE_PLAY_RECORD:
                mPresenter.pausePlayRecord();
                break;
            case AdapterEventIdConst.EVENT_ID_CONTINUE_PLAY_RECORD:
                mPresenter.continuePlayRecord();
                break;
            case AdapterEventIdConst.EVENT_ID_SEEK_PLAY_RECORD:
                mPresenter.seekPlayRecord((int) input);
                break;
        }
    }

    /**
     * 备注tips菜单
     */
    public void showRemarkTipsMenu(final int targetPosition, View v, final RemarkBean remarkBean) {
        final boolean isVoice = remarkBean.isVoice();
        final boolean isSelfCreated = remarkBean.isSelfCreated();
        if (!isVoice && !isSelfCreated)
            return;
        if (textMenuMine == null) {
            textMenuMine = mResources.getStringArray(R.array.remark_edit_menu_default);
            audioMenuMine = mResources.getStringArray(R.array.remark_edit_menu_audio_company);
            textMenuWuxiao = mResources.getStringArray(R.array.remark_edit_menu_default_wuxiao);
            voiceMenuOther = new String[1];
        }

        String[] menuItems;
        if (remarkBean.isValidInquiry == 0) {
            menuItems = textMenuWuxiao;
        } else {
            menuItems = (!isVoice ? textMenuMine : (isSelfCreated ? audioMenuMine : voiceMenuOther));
            if (menuItems != textMenuMine)
                menuItems[0] = mResources.getString((AudioPlayerTool.getInstance().isStreamMusic() ? R.string.audio_play_mode_communication : R.string.audio_play_mode_speaker));
        }

        if (mRemarkTipsPopupWindow == null)
            mRemarkTipsPopupWindow = new RemarkTipsPopupWindow(getActivity(), null);
        mRemarkTipsPopupWindow.setMenuClickListener(new RemarkTipsPopupWindow.ITipsMenuClickListener() {
            @Override
            public void onTipClick(int menuPosition) {
                switch (menuPosition) {
                    case 0:
                        if (isVoice) {
                            listAdapter.performChangeAudioPlayMode();
                        } else if (remarkBean.isValidInquiry == 1) {
                            AddRemarkDialog addRemarkDialog = new AddRemarkDialog(getActivity());
                            addRemarkDialog.setData(mPresenter.getCurrentResumeData(), remarkBean);
                            addRemarkDialog.setRemarkListener(new AddRemarkDialog.updateRemarkListener() {
                                @Override
                                public void updateRemark(RemarkBean remarkBean) {
                                    mPresenter.addOrUpdateRemark(remarkBean);
                                }
                            });
                            addRemarkDialog.show();
                        } else {
                            mPresenter.deleteRemark(remarkBean);
                        }
                        break;
                    case 1:
                        mPresenter.deleteRemark(remarkBean);
                        break;
                }
            }
        });
        mRemarkTipsPopupWindow.setUpMenus(menuItems);
        mRemarkTipsPopupWindow.show(v);
    }

    public void updateDurationChangedView(long curPosition) {
        listAdapter.updateDurationChanged((int) (curPosition / 1000));
    }

    public void callFinishPlayVoice() {
        listAdapter.stopAudioAnimate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listAdapter != null)
            listAdapter.onDestroy();
    }
}
