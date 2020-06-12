package com.baza.android.bzw.businesscontroller.login.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bznet.android.rcbox.R;

public class FirstOpenAppGuideAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    private int[] mPageContentImageIds = {R.drawable.open_guide_page_image_1, R.drawable.open_guide_page_image_2, R.drawable.open_guide_page_image_3, R.drawable.open_guide_page_image_4};

    public FirstOpenAppGuideAdapter(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPageContentImageIds == null ? 0 : mPageContentImageIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View viewContent = mLayoutInflater.inflate(R.layout.open_guide_item_viewpager, null);
        ImageView imageView = viewContent.findViewById(R.id.iv_guide_image);
        imageView.setImageResource(mPageContentImageIds[position]);
        container.addView(viewContent);
        return viewContent;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

//    public static class OpenAppGuidePageTransformer implements ViewPager.PageTransformer {
//        @Override
//        public void transformPage(@NonNull View page, float position) {
//            ImageView imageView_page = page.findViewById(R.id.iv_guide_image);
//            float percent = 1 - Math.abs(position);
//            imageView_page.setScaleX(percent);
//            imageView_page.setScaleY(percent);
//            imageView_page.setTranslationX(-imageView_page.getMeasuredWidth() * position);
//            imageView_page.setAlpha(percent);
////            ImageView imageView_title = page.findViewById(R.id.iv_guide_title);
////            if (position == -1.0f || position == 1.0f) {
////                imageView_title.setTranslationY(-imageView_title.getMeasuredHeight());
////                imageView_title.setAlpha(0f);
////                return;
////            }
////            if (position == 0.0f) {
////                ViewCompat.animate(imageView_title).setDuration(300).translationY(0).setInterpolator(new DecelerateInterpolator()).start();
////                ViewCompat.animate(imageView_title).setDuration(300).alpha(1f).setInterpolator(new DecelerateInterpolator()).start();
////            }
//        }
//    }
}
