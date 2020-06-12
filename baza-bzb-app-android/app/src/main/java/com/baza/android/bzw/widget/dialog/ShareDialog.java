package com.baza.android.bzw.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vincent.Lei on 2017/1/13.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class ShareDialog extends Dialog implements View.OnClickListener {

    public static final int SHARE_TYPE_WE_CHAT_CONTACT = 1;
    public static final int SHARE_TYPE_QQ_CONTACT = 2;
    public static final int SHARE_TYPE_SMS = 3;
    public static final int SHARE_TYPE_WE_CHAT_FRIEND_CIRCLE = 4;
    public static final int SHARE_TYPE_QZONE = 5;
    public static final int SHARE_TYPE_EMAIL = 6;
    public static final int SHARE_TYPE_APP_FRIEND = 7;


    public interface IShareMenuSelectedListener {
        void onSharePlatformSelected(int platformType);
    }

    private IShareMenuSelectedListener mListener;
    private List<MenuType> mMenuTypes = new ArrayList<>(5);
    private MenuAdapter mMenuAdapter;

    public ShareDialog(Context context) {
        this(context, R.style.customerDialog_bottom);
    }

    private ShareDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_share_menu);
        View mainView = findViewById(R.id.main_view);
        ViewGroup.LayoutParams lp = mainView.getLayoutParams();
        lp.width = ScreenUtil.screenWidth;
        mainView.setLayoutParams(lp);
        mainView.setOnClickListener(this);
        initMenus();
        GridView gridView = mainView.findViewById(R.id.grid_menus);
        mMenuAdapter = new MenuAdapter(getContext(), mMenuTypes, ScreenUtil.screenWidth / 4);
        gridView.setAdapter(mMenuAdapter);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    private void initMenus() {
        if (mMenuTypes.isEmpty()) {
            addWeChatContactMenu();
            addWeChatFriendCircleMenu();
            addQQContactMenu();
            addQZoneMenu();
            addSmsMenu();
        }
    }

    public ShareDialog addShareMenuSelectedListener(IShareMenuSelectedListener listener) {
        this.mListener = listener;
        return this;
    }

    public ShareDialog addEmailMenu() {
        mMenuTypes.add(new MenuType(SHARE_TYPE_EMAIL, R.drawable.icon_share_email, R.string.email));
        return this;
    }

    public ShareDialog addAppFriendMenu() {
        mMenuTypes.add(new MenuType(SHARE_TYPE_APP_FRIEND, R.drawable.icon_share_app_friend, R.string.title_friend));
        return this;
    }

    public ShareDialog addWeChatContactMenu() {
        mMenuTypes.add(new MenuType(SHARE_TYPE_WE_CHAT_CONTACT, R.drawable.icon_wechat, R.string.share_menu_wechat));
        return this;
    }

    public ShareDialog addWeChatFriendCircleMenu() {
        mMenuTypes.add(new MenuType(SHARE_TYPE_WE_CHAT_FRIEND_CIRCLE, R.drawable.icon_wecht_fc, R.string.share_menu_wechat_fc));
        return this;
    }

    public ShareDialog addQQContactMenu() {
        mMenuTypes.add(new MenuType(SHARE_TYPE_QQ_CONTACT, R.drawable.icon_qq, R.string.share_menu_qq));
        return this;
    }

    public ShareDialog addQZoneMenu() {
        mMenuTypes.add(new MenuType(SHARE_TYPE_QZONE, R.drawable.icon_qzone, R.string.share_menu_qzone));
        return this;
    }

    public ShareDialog addSmsMenu() {
        mMenuTypes.add(new MenuType(SHARE_TYPE_SMS, R.drawable.icon_sms, R.string.share_menu_sms));
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mMenuAdapter != null)
            mMenuAdapter.destroy();
    }

    private void setBack(int type) {
        dismiss();
        if (mListener != null)
            mListener.onSharePlatformSelected(type);
    }

    private final class MenuAdapter extends BaseAdapter implements View.OnClickListener {
        private Context context;
        private List<MenuType> menuTypes;
        private int itemWidth;

        private LayoutInflater layoutInflater;
        private Drawable[] mDrawables;

        MenuAdapter(Context context, List<MenuType> menuTypes, int itemWidth) {
            this.context = context;
            this.menuTypes = menuTypes;
            this.itemWidth = itemWidth;
            this.layoutInflater = LayoutInflater.from(context);
            this.mDrawables = new Drawable[mMenuTypes.size()];
        }

        @Override
        public int getCount() {
            return menuTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //这里面GridView全部显示 无需考虑复用问题
            convertView = layoutInflater.inflate(R.layout.layout_share_menu_item, null);
            convertView.setLayoutParams(new AbsListView.LayoutParams(itemWidth, itemWidth));
            TextView textView = (TextView) ((FrameLayout) convertView).getChildAt(0);
            MenuType menuType = menuTypes.get(position);
            textView.setText(menuType.textId);
            Drawable drawable = context.getResources().getDrawable(menuType.drawableId);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mDrawables[position] = drawable;
            textView.setCompoundDrawables(null, drawable, null, null);
            convertView.setOnClickListener(this);
            convertView.setTag(position);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            setBack(menuTypes.get((int) v.getTag()).type);
        }

        public void destroy() {
            if (mDrawables != null)
                for (int i = 0; i < mDrawables.length; i++) {
                    mDrawables[i].setCallback(null);
                }
        }
    }

    private static class MenuType {
        public int type;
        public int drawableId;
        public int textId;

        MenuType(int type, int drawableId, int textId) {
            this.type = type;
            this.drawableId = drawableId;
            this.textId = textId;
        }
    }
}
