package com.baza.android.bzw.widget.emotion;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.slib.utils.LoadImageUtil;
import com.bznet.android.rcbox.R;


/**
 * 每屏显示的贴图
 */
public class StickerAdapter extends BaseAdapter {

    private Context context;
    private StickerCategory category;
    private int startIndex;

    public StickerAdapter(Context mContext, StickerCategory category, int startIndex) {
        this.context = mContext;
        this.category = category;
        this.startIndex = startIndex;
    }

    public int getCount() {//获取每一页的数量
        int count = category.getStickers().size() - startIndex;
        count = Math.min(count, EmoticonView.STICKER_PER_PAGE);
        return count;
    }

    @Override
    public Object getItem(int position) {
        return category.getStickers().get(startIndex + position);
    }

    @Override
    public long getItemId(int position) {
        return startIndex + position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        StickerViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.nim_sticker_picker_view, null);
            viewHolder = new StickerViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.sticker_thumb_image);
            viewHolder.descLabel = convertView.findViewById(R.id.sticker_desc_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StickerViewHolder) convertView.getTag();
        }

        int index = startIndex + position;
        if (index >= category.getStickers().size()) {
            return convertView;
        }

        StickerItem sticker = category.getStickers().get(index);
        if (sticker == null) {
            return convertView;
        }
        LoadImageUtil.loadImage(StickerManager.getInstance().getStickerBitmapUri(sticker.getCategory(),sticker.getName()),viewHolder.imageView);
//        ImageLoader.getInstance().displayImage(
//                , sticker.getName()), viewHolder.imageView, StickerManager.getInstance().getStickerImageOptions(ScreenUtil.dip2px(64)));

        viewHolder.descLabel.setVisibility(View.GONE);

        return convertView;
    }

    class StickerViewHolder {
        public ImageView imageView;
        public TextView descLabel;
    }
}