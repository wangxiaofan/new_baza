package com.slib.utils;

import android.content.Context;
import android.view.View;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by LW on 2016/5/30.
 * Title :
 * Note :
 */
public class DialogUtil {

    public static MaterialDialog singleButtonShow(Context context, int title_id, int message_id, View.OnClickListener clickListener) {
        return singleButtonShow(context, title_id, message_id, 0, clickListener);
    }

    public static MaterialDialog singleButtonShow(Context context, String title, String message, View.OnClickListener clickListener) {
        return singleButtonShow(context, title, message, null, clickListener);
    }

    public static MaterialDialog singleButtonShow(Context context, int title_id, int message_id, int button_text_id, View.OnClickListener clickListener) {
        MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.buildButtonCount(1);
        materialDialog.buildTitle(title_id).buildMessage(message_id).buildSureButtonText(button_text_id).buildClickListener(null, clickListener);
        materialDialog.setCancelable(false);
        materialDialog.show();
        return materialDialog;
    }

    public static MaterialDialog singleButtonShow(Context context, String title, String message, String button_text, View.OnClickListener clickListener) {
        MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.buildButtonCount(1);
        materialDialog.buildTitle(title).buildMessage(message).buildSureButtonText(button_text).buildClickListener(null, clickListener);
        materialDialog.setCancelable(false);
        materialDialog.show();
        return materialDialog;
    }

    public static MaterialDialog singleButtonShow(Context context, String title, String message, String button_text, View.OnClickListener clickListener, boolean autoShow) {
        MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.buildButtonCount(1);
        materialDialog.buildTitle(title).buildMessage(message).buildSureButtonText(button_text).buildClickListener(null, clickListener);
        materialDialog.setCancelable(false);
        if (autoShow)
            materialDialog.show();
        return materialDialog;
    }


    public static MaterialDialog doubleButtonShow(Context context, int title_id, int message_id, int button_cancel_text_id, int button_sure_text_id, View.OnClickListener clickListener_sure, View.OnClickListener clickListener_cancel) {
        MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.buildTitle(title_id).buildMessage(message_id).buildCancelButtonText(button_cancel_text_id).buildSureButtonText(button_sure_text_id).buildClickListener(clickListener_cancel, clickListener_sure);
        materialDialog.setCancelable(false);
        materialDialog.show();
        return materialDialog;
    }

    public static MaterialDialog doubleButtonShow(Context context, CharSequence title, CharSequence message, String button_cancel_text, String button_sure_text, View.OnClickListener clickListener_sure, View.OnClickListener clickListener_cancel, boolean autoShow) {
        MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.buildTitle(title).buildMessage(message).buildCancelButtonText(button_cancel_text).buildSureButtonText(button_sure_text).buildClickListener(clickListener_cancel, clickListener_sure);
        materialDialog.setCancelable(false);
        if (autoShow)
            materialDialog.show();
        return materialDialog;
    }

    public static MaterialDialog doubleButtonShow(Context context, int title_id, int message_id, View.OnClickListener clickListener_sure, View.OnClickListener clickListener_cancel) {
        return doubleButtonShow(context, title_id, message_id, 0, 0, clickListener_sure, clickListener_cancel);
    }

    public static MaterialDialog doubleButtonShow(Context context, String title, String message, View.OnClickListener clickListener_sure, View.OnClickListener clickListener_cancel) {
        return doubleButtonShow(context, title, message, null, null, clickListener_sure, clickListener_cancel,true);
    }
}