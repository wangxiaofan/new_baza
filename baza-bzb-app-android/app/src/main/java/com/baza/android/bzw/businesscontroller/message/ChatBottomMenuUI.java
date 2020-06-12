package com.baza.android.bzw.businesscontroller.message;

import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.businesscontroller.message.presenter.ChatPresenter;
import com.baza.android.bzw.businesscontroller.message.viewinterface.IChatView;
import com.baza.android.bzw.businesscontroller.publish.PickPhotosActivity;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.widget.emotion.EmoticonPickerView;
import com.baza.android.bzw.widget.emotion.IEmoticonSelectedListener;
import com.baza.android.bzw.widget.emotion.MoonUtil;
import com.slib.permission.PermissionsResultAction;
import com.slib.utils.KeyBoardHelper;
import com.slib.utils.string.StringUtil;
import com.bznet.android.rcbox.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/12/26.
 * Title：
 * Note：
 */

public class ChatBottomMenuUI implements View.OnClickListener, IEmoticonSelectedListener {
    private IChatView mChatView;
    private ChatPresenter mPresenter;
    private View mRootView;
    @BindView(R.id.et_msg)
    EditText editText_input;
    @BindView(R.id.iv_switch_chat_voice_or_text)
    ImageView imageView_switchChatToVoiceOrText;
    @BindView(R.id.iv_chose_emotion)
    ImageView imageView_emotion;
    @BindView(R.id.iv_chose_other)
    ImageView imageView_choseOther;
    @BindView(R.id.tv_voice_press_to_speak)
    TextView textView_pressToSpeak;
    @BindView(R.id.btn_send)
    Button button_send;
    @BindView(R.id.emoticonPickerView)
    EmoticonPickerView emoticonPickerView;
    @BindView(R.id.menu_send_image)
    View view_sendImage;
    @BindView(R.id.menu_send_file)
    View view_sendFile;
    @BindView(R.id.fl_menu_more)
    View view_extraMenu;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIsOpenKeyBoard;
    private Runnable mRunnableMessageListToBottom;

    ChatBottomMenuUI(final IChatView mChatView, ChatPresenter mPresenter, View mRootView) {
        this.mChatView = mChatView;
        this.mPresenter = mPresenter;
        this.mRootView = mRootView;
        ButterKnife.bind(this, mRootView);
        setUpEditText();
        imageView_switchChatToVoiceOrText.setOnClickListener(this);
        button_send.setOnClickListener(this);
        imageView_choseOther.setOnClickListener(this);
        imageView_emotion.setOnClickListener(this);
        view_sendImage.setOnClickListener(this);
        view_sendFile.setOnClickListener(this);
        emoticonPickerView.show(this);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                KeyBoardHelper.addKeyBoardOpenOrClosedListener(mChatView.callGetBindActivity(), new KeyBoardHelper.IKeyBoardListener() {
                    @Override
                    public void onKeyBoardOpen() {
                        mIsOpenKeyBoard = true;
                    }

                    @Override
                    public void onKeyBoardClosed() {
                        mIsOpenKeyBoard = false;
                    }
                });
            }
        });
    }

    public void destroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                mPresenter.sendTextMessage(editText_input.getText().toString().trim());
                editText_input.setText(null);
                break;
            case R.id.iv_chose_emotion:
                openEmotionMenu();
                break;
            case R.id.iv_chose_other:
                openMoreMenu();
                break;
            case R.id.menu_send_file:
                MaterialFilePicker.pickFileUseDefaultMode(mChatView.callGetBindActivity(), IMManager.getInstance(mChatView.callGetApplication()).getImRootPath() + "/file", RequestCodeConst.INT_REQUEST_PICK_FILE);
                break;
            case R.id.menu_send_image:
                PickPhotosActivity.launch(mChatView.callGetBindActivity(), RequestCodeConst.INT_REQUEST_PICK_PHOTO, 1, null);
                break;
            case R.id.iv_switch_chat_voice_or_text:
                mChatView.callGetBindActivity().requestPermission(Manifest.permission.RECORD_AUDIO, null, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        if (textView_pressToSpeak.getVisibility() == View.GONE) {
                            switchToAudioMessageMode();
                        } else {
                            switchToKeyBordMessageMode();
                        }
                    }

                    @Override
                    public void onDenied(String permission) {
                        //权限拒绝了
                        mChatView.callShowToastMessage(null, R.string.app_need_record_permission);
                    }
                });
                break;
        }
    }

    private void switchToAudioMessageMode() {
        if (mIsOpenKeyBoard)
            mChatView.callGetBindActivity().hideSoftInput();
        imageView_switchChatToVoiceOrText.setImageResource(R.drawable.icon_keyboard_chat);
        textView_pressToSpeak.setVisibility(View.VISIBLE);
        editText_input.setVisibility(View.GONE);
        button_send.setVisibility(View.GONE);
        imageView_choseOther.setVisibility(View.GONE);
        imageView_emotion.setVisibility(View.GONE);
        view_extraMenu.setVisibility(View.GONE);
        view_sendImage.setVisibility(View.GONE);
        view_sendFile.setVisibility(View.GONE);
        mChatView.callShowAudioRecordView(textView_pressToSpeak);
    }

    private void switchToKeyBordMessageMode() {
        imageView_switchChatToVoiceOrText.setImageResource(R.drawable.icon_voice_chat);
        textView_pressToSpeak.setVisibility(View.GONE);
        button_send.setVisibility(View.VISIBLE);
        editText_input.setVisibility(View.VISIBLE);
        imageView_choseOther.setVisibility(View.VISIBLE);
        imageView_emotion.setVisibility(View.VISIBLE);
        button_send.setVisibility(View.GONE);
        view_extraMenu.setVisibility(View.GONE);
        view_sendImage.setVisibility(View.GONE);
        view_sendFile.setVisibility(View.GONE);
        editText_input.setText(null);
    }

    private void openEmotionMenu() {
        if (emoticonPickerView.getVisibility() == View.VISIBLE) {
            view_extraMenu.setVisibility(View.GONE);
            emoticonPickerView.setVisibility(View.GONE);
            view_sendImage.setVisibility(View.GONE);
            view_sendFile.setVisibility(View.GONE);
            return;
        }
        if (mIsOpenKeyBoard) {
            mChatView.callGetBindActivity().hideSoftInput();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openEmotionMenu();
                }
            }, 200);
            return;
        }
        view_extraMenu.setVisibility(View.VISIBLE);
        emoticonPickerView.setVisibility(View.VISIBLE);
        view_sendImage.setVisibility(View.GONE);
        view_sendFile.setVisibility(View.GONE);
        delayedToMessageListBottom(100);
    }

    private void openMoreMenu() {
        if (view_sendImage.getVisibility() == View.VISIBLE) {
            view_extraMenu.setVisibility(View.GONE);
            emoticonPickerView.setVisibility(View.GONE);
            view_sendImage.setVisibility(View.GONE);
            view_sendFile.setVisibility(View.GONE);
            return;
        }
        if (mIsOpenKeyBoard) {
            mChatView.callGetBindActivity().hideSoftInput();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMoreMenu();
                }
            }, 200);
            return;
        }
        view_extraMenu.setVisibility(View.VISIBLE);
        emoticonPickerView.setVisibility(View.GONE);
        view_sendImage.setVisibility(View.VISIBLE);
        view_sendFile.setVisibility(View.VISIBLE);
        delayedToMessageListBottom(100);
    }

    private void openKeyBordInput() {
        if (!mIsOpenKeyBoard) {
            view_extraMenu.setVisibility(View.GONE);
            emoticonPickerView.setVisibility(View.GONE);
            view_sendImage.setVisibility(View.GONE);
            view_sendFile.setVisibility(View.GONE);
            editText_input.requestFocus();
            mChatView.callGetBindActivity().showSoftInput(editText_input);
            delayedToMessageListBottom(200);
        }
    }

    private void delayedToMessageListBottom(long delayTime) {
        if (mRunnableMessageListToBottom == null)
            mRunnableMessageListToBottom = new Runnable() {
                @Override
                public void run() {
                    mChatView.callSetMessageListToBottom();
                }
            };
        mHandler.postDelayed(mRunnableMessageListToBottom, delayTime == 0 ? 300 : delayTime);
    }

    @Override
    public void onEmojiSelected(String key) {
        Editable mEditable = editText_input.getText();
        if (key.equals("/DEL")) {
            editText_input.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = editText_input.getSelectionStart();
            int end = editText_input.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            mEditable.replace(start, end, key);
        }
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName) {

    }

    private void setUpEditText() {
        editText_input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    openKeyBordInput();
                    return false;
                }
                return false;
            }

        });
        editText_input.addTextChangedListener(new TextWatcher() {
            private int mStart;
            private int mCount;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.mStart = start;
                this.mCount = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean hasMessage = (s.length() > 0);
                button_send.setVisibility((hasMessage ? View.VISIBLE : View.GONE));
                imageView_choseOther.setVisibility((hasMessage ? View.GONE : View.VISIBLE));

                MoonUtil.replaceEmoticons(mChatView.callGetBindActivity(), s, mStart, mCount);
                int editEnd = editText_input.getSelectionEnd();
                editText_input.removeTextChangedListener(this);
                while (StringUtil.counterChars(s.toString()) > 500 && editEnd > 0) {
                    s.delete(editEnd - 1, editEnd);
                    editEnd--;
                }
                editText_input.setSelection(editEnd);
                editText_input.addTextChangedListener(this);
            }
        });
    }

    public void closeAllInput() {
        view_extraMenu.setVisibility(View.GONE);
        emoticonPickerView.setVisibility(View.GONE);
        view_sendImage.setVisibility(View.GONE);
        view_sendFile.setVisibility(View.GONE);
        mChatView.callGetBindActivity().hideSoftInput();
    }
}
