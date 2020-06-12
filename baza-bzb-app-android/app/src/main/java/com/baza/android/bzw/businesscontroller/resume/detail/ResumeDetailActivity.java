package com.baza.android.bzw.businesscontroller.resume.detail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.businesscontroller.audio.tools.AudioPlayerTool;
import com.baza.android.bzw.businesscontroller.email.BindEmailActivity;
import com.baza.android.bzw.businesscontroller.email.EmailShareActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdateCardActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.UpdateLogActivity;
import com.baza.android.bzw.businesscontroller.publish.LargeImageActivity;
import com.baza.android.bzw.businesscontroller.resume.create.CreateResumeActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.adapter.remark.RemarkListAdapter;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.extra.AndroidBug5497Workaround;
import com.baza.android.bzw.widget.ListPopupWindow;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.AddRemarkDialog;
import com.bznet.android.rcbox.R;
import com.slib.permission.PermissionsResultAction;
import com.slib.utils.AppUtil;
import com.slib.utils.DialogUtil;
import com.slib.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：简历详情
 * Note：
 */

public class ResumeDetailActivity extends BaseActivity implements IResumeDetailView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.ibtn_right_click)
    ImageButton imageButton_rightClick;
    @BindView(R.id.listView_remark)
    ListView listView_remark;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_title)
    TextView textView_title;
    private RemarkListAdapter mAdapter;
    private ResumeDetailPresenter mPresenter;
    private DetailBottomMenuUI mDetailBottomMenuUI;
    private HeadViewMainInfoUI mHeadViewMainInfoUI;
    private HeadViewDetailInfoUI mHeadViewDetailInfoUI;
    private FootViewExtraInfoUI mFootViewExtraInfoUI;
    private RemarkTipsPopupWindow mRemarkTipsPopupWindow;
    private ListPopupWindow listPopupWindow_moreOperation;
    private VoiceRecordUI mVoiceRecordUI;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_candidate_detail;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_candidate_detail);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new ResumeDetailPresenter(this, getIntent());
        imageButton_rightClick.setImageResource(R.drawable.icon_more_black);
        //底部菜单
        mDetailBottomMenuUI = new DetailBottomMenuUI(this, mPresenter);
        //主要信息headView
        mHeadViewMainInfoUI = new HeadViewMainInfoUI(this, mPresenter);
        listView_remark.addHeaderView(mHeadViewMainInfoUI.getHeadView());
        //教育、工作经历、自我评价、添加寻访记录
        mHeadViewDetailInfoUI = new HeadViewDetailInfoUI(this, mPresenter);
        listView_remark.addFooterView(mHeadViewDetailInfoUI.getHeadView());
        //简历原文 添加备注按钮
        mFootViewExtraInfoUI = new FootViewExtraInfoUI(this, mPresenter);
        listView_remark.addFooterView(mFootViewExtraInfoUI.getFootView());

        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadInitData();
            }
        });

        mAdapter = new RemarkListAdapter(this, false, mPresenter.getRemarkListData(), this);
        listView_remark.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //当前打开录音面板时 触摸到面板上方区域自动关闭录音面板
        return ((mVoiceRecordUI != null && ev.getAction() == MotionEvent.ACTION_DOWN && mVoiceRecordUI.checkHideWhenTouchWindow(ev)) || super.dispatchTouchEvent(ev));
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
        AndroidBug5497Workaround.assistActivity(this, statusBarHeight);
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mVoiceRecordUI != null)
            mVoiceRecordUI.onDestroy();
        if (mDetailBottomMenuUI != null)
            mDetailBottomMenuUI.destroy();
        if (mHeadViewMainInfoUI != null)
            mHeadViewMainInfoUI.destroy();
        if (mPresenter != null)
            mPresenter.onDestroy();
        if (mAdapter != null)
            mAdapter.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.ibtn_right_click:
                //更多编辑菜单
                showMoreEditMenu();
                break;
        }
    }

    public static void launch(Activity activity, IResumeDetailView.IntentParam intentParam) {
        if (intentParam == null)
            return;
        Intent intent = new Intent(activity, ResumeDetailActivity.class);
        intent.putExtra("intentParam", intentParam);
        activity.startActivity(intent);
    }


    @Override
    public void updateViewForCurrentMode() {
//        frameLayout_bottomMenu.removeAllViews();
//        //底部菜单
//        frameLayout_bottomMenu.addView(mDetailBottomMenuUI.getView());
//        imageButton_rightClick.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateViewForUpdateHistory() {
        imageButton_rightClick.setVisibility(View.GONE);
        mDetailBottomMenuUI.updateViewForUpdateHistory();
        mHeadViewMainInfoUI.updateViewForUpdateHistory();
        mHeadViewDetailInfoUI.updateViewForUpdateHistory();
    }

    @Override
    public void callShowSpecialToastMsg(int type, String msg, int msgId) {
        switch (type) {
            case ResumeDetailPresenter.SELF_TOAST_RECORD:
                View view_hint = getLayoutInflater().inflate(R.layout.toast_record_hint, null);
                ((TextView) view_hint.findViewById(R.id.tv_text_hint)).setText(msg == null ? mResources.getString(msgId) : msg);
                ToastUtil.selfToast(mApplication, view_hint);
                break;
            case ResumeDetailPresenter.SELF_TOAST_COLLECTION:
                ImageView imageView = new ImageView(mApplication);
                imageView.setImageResource((R.string.un_collection_success == msgId ? R.drawable.image_uncollection : R.drawable.image_collection));
                ToastUtil.selfToast(mApplication, imageView);
                break;
        }

    }

    @Override
    public void callFinishPlayVoice() {
        mAdapter.stopAudioAnimate();
    }

    @Override
    public void callUpdateHandleResumeDataViews() {
        ResumeDetailBean candidateDetailBean = mPresenter.getCurrentResumeData();
        textView_title.setText(mResources.getString(R.string.candidate_detail_title, candidateDetailBean != null ? candidateDetailBean.realName : null));
        mDetailBottomMenuUI.setData();
        mHeadViewMainInfoUI.setData();
        mHeadViewDetailInfoUI.setData();
        mFootViewExtraInfoUI.setData();
    }

    @Override
    public void callUpdateRemarkViews(int targetPosition) {
        if (targetPosition < 0) {
            mAdapter.refreshTargetView(targetPosition, null, false);
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView_remark);
        if (view == null)
            return;
        mAdapter.refreshTargetView(targetPosition, view, false);
    }

    @Override
    public void callShowAddRemarkView() {
//        ListMenuDialog.showNewInstance(this, null, mResources.getStringArray(R.array.remark_selector), true, new ListMenuDialog.IOnChoseItemClickListener() {
//            @Override
//            public void onChoseItemClick(int position) {
//                switch (position) {
//                    case 0:
//                        showAddVoiceRemarkPane();
//                        break;
//                    case 1:
//                        AddTextRemarkActivity.launch(ResumeDetailActivity.this, mPresenter.getCurrentResumeData(), null, RequestCodeConst.INT_REQUEST_NEW_ADD_REMARK, false);
//                        break;
//                }
//            }
//        });
        AddRemarkDialog addRemarkDialog = new AddRemarkDialog(this);
        addRemarkDialog.setData(mPresenter.getCurrentResumeData(), null);
        addRemarkDialog.setRemarkListener(new AddRemarkDialog.updateRemarkListener() {
            @Override
            public void updateRemark(RemarkBean remarkBean) {
                mPresenter.addOrUpdateRemark(remarkBean);
            }
        });
        addRemarkDialog.show();
    }

    /**
     * 显示录音UI
     */
    private void showAddVoiceRemarkPane() {
        requestPermission(Manifest.permission.RECORD_AUDIO, null, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                if (mVoiceRecordUI == null) {
                    ViewStub viewStub = findViewById(R.id.viewStub_voice_record_touch_ui);
                    View view_touchUI = viewStub.inflate();
                    viewStub = findViewById(R.id.viewStub_voice_record_info_ui);
                    View view_infoUI = viewStub.inflate();
                    mVoiceRecordUI = new VoiceRecordUI(view_touchUI, view_infoUI, ResumeDetailActivity.this, mPresenter);
                }
                mVoiceRecordUI.openRecordPane();
            }

            @Override
            public void onDenied(String permission) {
                //权限拒绝了
                ToastUtil.showToast(mApplication, R.string.app_need_record_permission);
            }
        });
    }

    /**
     * 右上角更多编辑菜单
     */
    private void showMoreEditMenu() {
        String[] moreEditMenu = mPresenter.getMoreEditMenu();
        if (moreEditMenu == null)
            return;
        if (listPopupWindow_moreOperation == null)
            listPopupWindow_moreOperation = new ListPopupWindow(this, null, new ListPopupWindow.IMenuClickListener() {
                @Override
                public void onMenuClick(int position) {
                    switch (position) {
                        case 0:
                            ResumeBean resumeBean = new ResumeBean();
                            resumeBean.copyFromOld(mPresenter.getCurrentResumeData());
                            CreateResumeActivity.launch(ResumeDetailActivity.this, resumeBean, mPresenter.getmIntentParam().pageCode);
                            break;
                        case 1:
                            DialogUtil.doubleButtonShow(ResumeDetailActivity.this, 0, R.string.confirm_delete_self_built_candidate, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //删除简历
                                    mPresenter.deleteResume();
                                }
                            }, null);
                            break;
                        case 2:
                            ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
                            if (resumeDetailBean == null)
                                return;
                            UpdateLogActivity.launch(ResumeDetailActivity.this, resumeDetailBean.realName, resumeDetailBean.candidateId);
                            break;
                    }
                }
            });
        listPopupWindow_moreOperation.updateMenus(moreEditMenu);
        listPopupWindow_moreOperation.show(imageButton_rightClick);
    }

    @Override
    public void callEmailShare(boolean hasBindEmail, boolean isShareContact, boolean isShareRemark) {
        if (!hasBindEmail)
            BindEmailActivity.launch(this);
        else
            EmailShareActivity.launch(ResumeDetailActivity.this, mPresenter.getCurrentResumeData().candidateId, isShareContact, isShareRemark, mPresenter.getCurrentResumeData().isHasOriginalFile == ResumeDetailBean.ORIGINAL_FILE_EXIST);

    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        if (!loadingView.isShownVisibility())
            return;
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
    }

    @Override
    public void callShowLoadingView() {
        if (loadingView.isShownVisibility())
            return;
        loadingView.startLoading(null);
    }

    @Override
    public void callUpdateCollectionStatus() {
        mDetailBottomMenuUI.updateCollectionInfo();
    }

    @Override
    public void callUpdateResumeMainInfo() {
        ResumeDetailBean candidateDetailBean = mPresenter.getCurrentResumeData();
        textView_title.setText(mResources.getString(R.string.candidate_detail_title, candidateDetailBean != null ? candidateDetailBean.realName : null));
        mHeadViewMainInfoUI.setData();
    }

    @Override
    public void updateToTargetResume() {
        ResumeUpdateCardActivity.launch(this, mPresenter.getCurrentResumeData().candidateId, RequestCodeConst.INT_REQUEST_UPDATE_RESUME_BY_ENGINE);
    }

    @Override
    public void callUpdateDurationChangedView(long curPosition) {
        mAdapter.updateDurationChanged((int) (curPosition / 1000));
    }

    @Override
    public void callShowRecommendView() {
        new RecommendUI(this, new RecommendUI.IRecommendSetListener() {
            @Override
            public void onRecommendSet(String content, Date date) {
                mPresenter.addRecommend(content, date);
            }
        }).show();
    }

    @Override
    public void callUpdateMobileOrEmailValidView() {
        mHeadViewMainInfoUI.updateMobileOrEmailValidView();
    }

    @Override
    public void callUpdateMobileOrEmailNum(int type, String content) {

    }

    private String[] audioMenuMine;
    private String[] phoneMenuMine;
    private String[] voiceMenuOther;
    private String[] textMenuMine;

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
            audioMenuMine = mResources.getStringArray(R.array.remark_edit_menu_audio_mine);
            voiceMenuOther = new String[1];
        }

        String[] menuItems = (!isVoice ? textMenuMine : (isSelfCreated ? audioMenuMine : voiceMenuOther));
        if (menuItems != textMenuMine)
            menuItems[0] = mResources.getString((AudioPlayerTool.getInstance().isStreamMusic() ? R.string.audio_play_mode_communication : R.string.audio_play_mode_speaker));

        if (mRemarkTipsPopupWindow == null)
            mRemarkTipsPopupWindow = new RemarkTipsPopupWindow(this, null);
        mRemarkTipsPopupWindow.setMenuClickListener(new RemarkTipsPopupWindow.ITipsMenuClickListener() {
            @Override
            public void onTipClick(int menuPosition) {
                switch (menuPosition) {
                    case 0:
                        if (isVoice) {
                            mAdapter.performChangeAudioPlayMode();
                        } else {
                            AddRemarkDialog addRemarkDialog = new AddRemarkDialog(ResumeDetailActivity.this);
                            addRemarkDialog.setData(mPresenter.getCurrentResumeData(), remarkBean);
                            addRemarkDialog.setRemarkListener(new AddRemarkDialog.updateRemarkListener() {
                                @Override
                                public void updateRemark(RemarkBean remarkBean) {
                                    mPresenter.addOrUpdateRemark(remarkBean);
                                }
                            });
                            addRemarkDialog.show();
                        }
//                            AddTextRemarkActivity.launch(ResumeDetailActivity.this, mPresenter.getCurrentResumeData(), remarkBean, RequestCodeConst.INT_REQUEST_NEW_ADD_REMARK, false);
                        break;
                    case 1:
                        if (isVoice && mAdapter.isCurrentVoiceIsPlaying(remarkBean))
                            mPresenter.stopPlayRecord();
                        if (isVoice) {
                            AddRemarkDialog addRemarkDialog = new AddRemarkDialog(ResumeDetailActivity.this);
                            addRemarkDialog.setData(mPresenter.getCurrentResumeData(), remarkBean);
                            addRemarkDialog.setRemarkListener(new AddRemarkDialog.updateRemarkListener() {
                                @Override
                                public void updateRemark(RemarkBean remarkBean) {
                                    mPresenter.addOrUpdateRemark(remarkBean);
                                }
                            });
                            addRemarkDialog.show();
//                            AddTextRemarkActivity.launch(ResumeDetailActivity.this, mPresenter.getCurrentResumeData(), remarkBean, RequestCodeConst.INT_REQUEST_NEW_ADD_REMARK, false);
                            return;
                        }
                        mPresenter.deleteRemark(remarkBean);
                        break;
                    case 2:
                        mPresenter.deleteRemark(remarkBean);
                        break;
                }
            }
        });
        mRemarkTipsPopupWindow.setUpMenus(menuItems);
        mRemarkTipsPopupWindow.show(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodeConst.INT_REQUEST_UPDATE_RESUME_BY_ENGINE:
                if (resultCode == Activity.RESULT_OK)
                    mPresenter.highLightUpdatedByEngineContent();
                break;
            case RequestCodeConst.INT_REQUEST_NEW_ADD_REMARK:
                if (resultCode == Activity.RESULT_OK && data != null)
                    mPresenter.addOrUpdateRemark((RemarkBean) data.getSerializableExtra("remark"));
                break;
        }
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
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
            case AdapterEventIdConst.EVENT_ID_SHOW_EDIT_REMARK_TIPS_MENU:
                //显示编辑文本备注的对话框
                showRemarkTipsMenu(position, v, (RemarkBean) input);
                break;
            case AdapterEventIdConst.EVENT_ID_UPDATE_TARGET_ITEM:
                callUpdateRemarkViews(position);
                break;
            case AdapterEventIdConst.EVENT_ID_ATTACHMENT_SHOW_FULL_IMAGE:
                LargeImageActivity.launch(this, v, (ArrayList<String>) input, position);
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

}
