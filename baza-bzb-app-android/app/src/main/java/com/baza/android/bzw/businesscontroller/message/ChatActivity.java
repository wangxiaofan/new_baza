package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.baza.android.bzw.businesscontroller.audio.AudioRecordUI;
import com.baza.android.bzw.businesscontroller.audio.tools.AudioPlayerTool;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.message.adapter.ChatAdapter;
import com.baza.android.bzw.businesscontroller.message.presenter.ChatPresenter;
import com.baza.android.bzw.businesscontroller.message.viewinterface.IChatView;
import com.baza.android.bzw.businesscontroller.publish.LargeImageActivity;
import com.baza.android.bzw.businesscontroller.publish.PickPhotosActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.extra.AndroidBug5497Workaround;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.widget.dialog.AddFriendDialog;
import com.baza.android.bzw.widget.dialog.ListMenuDialog;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.slib.http.FileOpenHelper;
import com.slib.utils.AppUtil;
import com.slib.utils.ToastUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：聊天界面
 * Note：
 */

public class ChatActivity extends BaseActivity implements IChatView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    private static final long TIME_LATER_TO_REFRESH_VIEW = 1000;
    private static final String INTENT_PARAM = "param";

    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.tv_onLineStatus)
    TextView textView_onLineStatus;
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_right_click)
    TextView textView_right;
    ListView listView;
    private AudioRecordUI mAudioRecordUI;
    private ChatAdapter mAdapter;
    private ChatPresenter mPresenter;
    private ChatBottomMenuUI mChatBottomMenuUI;
    private Runnable mRunnableRefreshComplete;
    private String[] mMenuMessageLongClickItems;
    private ChatParam mChatParam;

    public static class ChatParam implements Serializable {
        private String account;
        private int sessionType;

        public String getAccount() {
            return account;
        }

        public int getSessionType() {
            return sessionType;
        }


        public ChatParam(String account, int sessionType) {
            this.account = account;
            this.sessionType = sessionType;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_chat);
    }

    @Override
    protected void initOverAll() {
        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {
        mChatParam = (ChatParam) intent.getSerializableExtra(INTENT_PARAM);
        if (mPresenter != null)
            mPresenter.refreshNewParam(mChatParam);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mChatBottomMenuUI.closeAllInput();
                return false;
            }
        });
        mPresenter = new ChatPresenter(this, mChatParam);
        mAdapter = new ChatAdapter(this, mPresenter.getMessages(), mPresenter.getFlagTimes(), mPresenter.getPassiveShareProcessedTagSet(), this);
        listView.setAdapter(mAdapter);
        textView_right.setText(R.string.add_friend);
        mChatBottomMenuUI = new ChatBottomMenuUI(this, mPresenter, findViewById(R.id.view_bottom_menu));
        mPresenter.initialize();
    }

    @Override
    protected boolean isHideInputMethodAutoWhenTouchWindow() {
        return false;
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
        AndroidBug5497Workaround.assistActivity(this, statusBarHeight);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null)
            mPresenter.onPause();
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mAudioRecordUI != null)
            mAudioRecordUI.onDestroy();
        if (mAdapter != null)
            mAdapter.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();
        if (mChatBottomMenuUI != null)
            mChatBottomMenuUI.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                new AddFriendDialog(this, new AddFriendDialog.IAddFriendEditListener() {
                    @Override
                    public void onReadyAddFriend(String hello) {
                        mPresenter.addFriend(hello);
                    }
                });
                break;
        }
    }

    public static void launch(Activity activity, ChatParam builder) {
        if (builder == null)
            throw new IllegalArgumentException("ChatParam can not bu null");
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(INTENT_PARAM, builder);
        activity.startActivity(intent);
    }

    @Override
    public void callRefreshMessageView(final int targetPosition, final int selection, final int lastCount, final boolean isNewMessageArrival) {
        pullToRefreshListView.post(new Runnable() {
            @Override
            public void run() {
                if (targetPosition < 0)
                    mAdapter.notifyDataSetChanged();
                else {
                    View view = AppUtil.getTargetVisibleView(targetPosition, listView);
                    if (view != null)
                        mAdapter.refreshTargetItemView(view, targetPosition);
                }
                if (targetPosition != CommonConst.LIST_POSITION_NONE)
                    return;
                if (isNewMessageArrival || lastCount == 0) {
                    listView.setSelection(mAdapter.getCount());
                    return;
                }
                int selectionNew = selection;
                int lastVisiblePosition = listView.getLastVisiblePosition();
                if (lastVisiblePosition >= (listView.getHeaderViewsCount() + lastCount - 1))
                    selectionNew = listView.getHeaderViewsCount() + mAdapter.getCount();
                if (selectionNew >= 0)
                    listView.setSelection(selectionNew);
            }
        });
    }

    @Override
    public void callOnLineStatusChanged(int statusCode) {
        switch (statusCode) {
            case IMConst.IM_ONLINE_STATUS_NO_NET:
                textView_onLineStatus.setText(R.string.im_status_net_broken);
                textView_onLineStatus.setVisibility(View.VISIBLE);
                break;
            case IMConst.IM_ONLINE_STATUS_CONNECTING:
                textView_onLineStatus.setText(R.string.im_status_connecting);
                textView_onLineStatus.setVisibility(View.VISIBLE);
                break;
            case IMConst.IM_ONLINE_STATUS_LOGINING:
                textView_onLineStatus.setText(R.string.im_status_logining);
                textView_onLineStatus.setVisibility(View.VISIBLE);
                break;
            case IMConst.IM_ONLINE_STATUS_UNLOGIN:
                textView_onLineStatus.setText(R.string.im_status_unlogining);
                textView_onLineStatus.setVisibility(View.VISIBLE);
                break;
            case IMConst.IM_ONLINE_STATUS_KICKOUT:
                textView_onLineStatus.setText(R.string.im_status_kickout);
                textView_onLineStatus.setVisibility(View.VISIBLE);
                AppGlobalManager.getInstance().forceLogoutForSinglePlatform();
                break;
            case IMConst.IM_ONLINE_STATUS_PWD_ERROR:
                textView_onLineStatus.setText(R.string.im_status_account_error);
                textView_onLineStatus.setVisibility(View.VISIBLE);
                break;
            default:
                textView_onLineStatus.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void callSetTitle(String title) {
        textView_title.setText(title);
    }

    @Override
    public void callFinishPlayVoice() {
        mAdapter.finishPlayVoice();
    }

    @Override
    public void callUpdateIsFriendView(boolean isFriend) {
        textView_right.setVisibility((isFriend ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callShowAudioRecordView(TextView touchView) {
        if (mAudioRecordUI == null) {
            ViewStub viewStub = findViewById(R.id.viewStub_voice_record_info_ui);
            View view = viewStub.inflate();
            mAudioRecordUI = new AudioRecordUI(touchView, view, new AudioRecordUI.IAudioRecordUIListener() {
                @Override
                public void onReadyToStartRecord() {
                    mPresenter.startRecord();
                }

                @Override
                public void onFinishRecord() {
                    mPresenter.finishRecord();
                }

                @Override
                public void onCancelRecord() {
                    mPresenter.cancelRecord();
                }
            });
        }
    }

    @Override
    public void callSetMessageListToBottom() {
        listView.setSelection(mAdapter.getCount());
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadImMessage(true);
        if (mRunnableRefreshComplete == null)
            mRunnableRefreshComplete = new Runnable() {
                @Override
                public void run() {
                    pullToRefreshListView.onRefreshComplete();
                }
            };
        pullToRefreshListView.postDelayed(mRunnableRefreshComplete, TIME_LATER_TO_REFRESH_VIEW);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.IM_MESSAGE_ADAPTER_EVENT_RESEND:
                mPresenter.resendMessage((BZWIMMessage) input);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam((String) input));
                break;
            case AdapterEventIdConst.EVENT_ID_RECORD_IS_NOT_EXIST:
                ToastUtil.showToast(this, R.string.audio_file_is_not_exist_is_delete);
                break;
            case AdapterEventIdConst.EVENT_ID_PLAY_RECORD:
            case AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE:
                mPresenter.playVoice((File) input, (adapterEventId == AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE));
                break;
            case AdapterEventIdConst.EVENT_ID_STOP_PLAY_RECORD:
                mPresenter.stopPlayRecord();
                break;
            case AdapterEventIdConst.EVENT_ID_SHOW_EDIT_VOICE_REMARK_MENU_DIALOG:
                showMessageLongClickListener((BZWIMMessage) input);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_CLICK_LINK_TEXT:
                if (input != null)
                    RemoteBrowserActivity.launch(this, null, false, input.toString());
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_FRIEND_HOME:
                mPresenter.watchFriendHome((BZWIMMessage) input);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_OPEN_FILE:
                FileAttachment fileAttachment = (FileAttachment) ((BZWIMMessage) input).imMessage.getAttachment();
                if (!TextUtils.isEmpty(fileAttachment.getPath())) {
                    LogUtil.d(fileAttachment.getPath());
                    FileOpenHelper.openFile(this, new File(fileAttachment.getPath()), FileOpenHelper.getMIMEType(fileAttachment.getDisplayName()));
                }
                break;
            case AdapterEventIdConst.EVENT_ID_ATTACHMENT_SHOW_FULL_IMAGE:
                hideSoftInput();
                mApplication.cacheTransformData(CommonConst.STR_TRANSFORM_IM_IMAGE_LOAD, input);
                LargeImageActivity.launch(this, v, null, 0, true);
                break;
        }
    }

    private void showMessageLongClickListener(final BZWIMMessage bzwimMessage) {
        if (mMenuMessageLongClickItems == null)
            mMenuMessageLongClickItems = new String[1];
        mMenuMessageLongClickItems[0] = mResources.getString((AudioPlayerTool.getInstance().isStreamMusic() ? R.string.audio_play_mode_communication : R.string.audio_play_mode_speaker));
        ListMenuDialog.showNewInstance(this, null, mMenuMessageLongClickItems, true, new ListMenuDialog.IOnChoseItemClickListener() {
            @Override
            public void onChoseItemClick(int position) {
                switch (position) {
                    case 0:
                        mPresenter.stopPlayRecord();
                        mAdapter.playTargetVoice(listView, bzwimMessage);
                        break;

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodeConst.INT_REQUEST_PICK_PHOTO:
                ArrayList<String> urls = PickPhotosActivity.parseSelectedPhotos(Activity.RESULT_OK, data);
                if (urls != null && urls.size() > 0)
                    mPresenter.sendImageMessage(urls.get(0));
                break;
            case RequestCodeConst.INT_REQUEST_PICK_FILE:
                String filePath = MaterialFilePicker.decodePickedFile(data, resultCode);
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists())
                        mPresenter.sendFileMessage(file);
                }
                break;
        }

    }
}
