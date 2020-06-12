package com.baza.android.bzw.businesscontroller.publish;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.baza.android.bzw.base.BaseActivity;
import com.bznet.android.rcbox.R;
import com.slib.permission.PermissionsResultAction;
import com.slib.storage.file.FileManager;
import com.slib.storage.file.StorageType;
import com.slib.utils.AppUtil;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ScreenUtil;
import com.slib.utils.ToastUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by LW on 2016/5/26.
 * Title : 选择本地图片 或者拍照
 * Note :
 */
public class PickPhotosActivity extends BaseActivity implements View.OnClickListener {

    private static final int CODE_TAKE_PHOTO = 9878;
    private static final int RESULT_CODE_NO_DATA = 20160526;
    private static final String RESULT_PARAM = "data";

    private TextView textView_sure;
    private GridView gridView;

    private int mMaxChoseCount;
    private ArrayList<String> mOldData;
    private ArrayList<ImageBean> mImageList = null;
    private ImageAdapter mImageAdapter;
    private String mFileTakePhoto;

    @Override

    protected int getLayoutId() {
        return R.layout.activity_pick_photos;
    }

    @Override
    protected void initWhenCallOnCreate() {
        Intent intent = getIntent();
        mMaxChoseCount = intent.getIntExtra("mMaxChoseCount", 1);
        mOldData = (ArrayList<String>) intent.getSerializableExtra("mOldData");
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.chose_photo);

        textView_sure = findViewById(R.id.tv_right_click);
        textView_sure.setVisibility(View.VISIBLE);
        textView_sure.setOnClickListener(this);

        gridView = findViewById(R.id.gv_images);


        new FindImageTask(this).execute(mOldData);
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
    }


    public void findLocalImageBack(ArrayList<ImageBean> imageList, int hasSelectedCount) {

        this.mImageList = imageList;
        if (this.mImageList == null)
            this.mImageList = new ArrayList<>();
        this.mImageList.add(0, null);
        mImageAdapter = new ImageAdapter(this.mImageList, this, hasSelectedCount);
        gridView.setAdapter(mImageAdapter);
        updateSureBtnStatus(hasSelectedCount);
    }


    private void updateSureBtnStatus(int selectedCount) {
        textView_sure.setText(getString(R.string.sure_with_count, String.valueOf(selectedCount), String.valueOf(mMaxChoseCount)));
        boolean clickable = selectedCount > 0;
        textView_sure.setTextColor(getResources().getColor(clickable ? R.color.text_color_blue_0D315C : R.color.text_color_grey_9E9E9E));
        textView_sure.setClickable(clickable);
    }

    /**
     * 拍照
     */

    private void doTakePhotos() {
        requestPermission(Manifest.permission.CAMERA, null, new PermissionsResultAction() {
            @Override
            public void onGranted() {

                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = FileManager.getFile(System.currentTimeMillis() + ".jpg", StorageType.TYPE_CACHE, true);
                if (file != null) {
                    Uri imageUri;
                    mFileTakePhoto = file.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(PickPhotosActivity.this, AppUtil.getPackageName(PickPhotosActivity.this) + ".fileprovider", new File(mFileTakePhoto));
                    } else {
                        imageUri = Uri.fromFile(file);
                    }
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    try {
                        startActivityForResult(openCameraIntent, CODE_TAKE_PHOTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (file.exists())
                            file.delete();
                        ToastUtil.showToast(PickPhotosActivity.this, R.string.take_photo_failed);
                    }
                }

            }

            @Override
            public void onDenied(String permission) {
                ToastUtil.showToast(PickPhotosActivity.this, R.string.require_camera_denied);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_TAKE_PHOTO) {
            /**
             * 拍照成功
             */
            if (resultCode == RESULT_OK) {
                Intent dataBack = new Intent();
                ArrayList<String> list = new ArrayList<>();
                list.add(mFileTakePhoto);
                dataBack.putExtra(RESULT_PARAM, list);
                setResult(RESULT_OK, dataBack);
                finish();
            } else {
                if (mFileTakePhoto != null) {
                    File file = new File(mFileTakePhoto);
                    if (file.exists())
                        file.delete();
                }
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                int resultCode = RESULT_CODE_NO_DATA;
                Intent dataBack = null;
                if (mImageAdapter != null) {
                    ArrayList<String> list = mImageAdapter.getSelected();
                    if (list != null && !list.isEmpty()) {
                        dataBack = new Intent();
                        dataBack.putExtra(RESULT_PARAM, list);
                        resultCode = RESULT_OK;
                    }
                }
                setResult(resultCode, dataBack);
                finish();
                break;
        }
    }

    public static ArrayList<String> parseSelectedPhotos(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            return (ArrayList<String>) data.getSerializableExtra(RESULT_PARAM);
        }
        return null;
    }

    public static void launch(Activity activity, int requestCode, int maxChoseCount, ArrayList<String> oldData) {
        Intent in = new Intent(activity, PickPhotosActivity.class);
        in.putExtra("mMaxChoseCount", maxChoseCount);
        if (oldData != null)
            in.putExtra("mOldData", oldData);
        activity.startActivityForResult(in, requestCode);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        System.gc();
        System.gc();
    }

    private class ImageAdapter extends BaseAdapter implements View.OnClickListener {
        private ArrayList<ImageBean> imageList = null;
        private Context context;
        private int hasSelectedCount;
        private int itemWidth;

        public ImageAdapter(ArrayList<ImageBean> imageList, Context context, int hasSelectedCount) {
            this.imageList = imageList;
            this.context = context;
            this.hasSelectedCount = hasSelectedCount;
            this.itemWidth = (ScreenUtil.screenWidth - ScreenUtil.dip2px(8)) / 3;
        }


        public ArrayList<String> getSelected() {
            if (hasSelectedCount == 0)
                return null;
            ArrayList<String> selected = null;
            if (imageList != null && !imageList.isEmpty()) {
                selected = new ArrayList<>(8);
                //第一个位置为拍照预留 为null 直接跳过
                for (int i = 1, size = imageList.size(); i < size; i++) {
                    ImageBean imageBean = imageList.get(i);
                    if (imageBean.isSelected)
                        selected.add(imageBean.imagePath);
                }
            }

            return selected;
        }


        @Override
        public int getCount() {
            return imageList == null ? 0 : imageList.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.griditem_pick_photo, null);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(itemWidth, itemWidth);
                convertView.setLayoutParams(lp);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

                convertView.setOnClickListener(this);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageBean imageBean = imageList.get(position);
            holder.imageview_cover.setVisibility(imageBean == null || !imageBean.isSelected ? View.GONE : View.VISIBLE);
            holder.imageview_checked.setVisibility(imageBean == null || !imageBean.isSelected ? View.GONE : View.VISIBLE);
            holder.imageview_cover.setTag(imageBean);
            if (imageBean != null) {
                holder.imageview_show.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LoadImageUtil.loadImage(imageBean.imagePath, R.drawable.default_empty_image, holder.imageview_show);
            } else {
                holder.imageview_show.setScaleType(ImageView.ScaleType.FIT_XY);
                LoadImageUtil.loadImage(R.drawable.icon_take_photo, holder.imageview_show);
            }


            return convertView;
        }

        @Override
        public void onClick(View v) {
            ViewHolder holder = (ViewHolder) v.getTag();
            ImageBean imageBean = (ImageBean) holder.imageview_cover.getTag();
            if (imageBean == null) {
                //拍照片
                doTakePhotos();
                return;
            }
            if (!imageBean.isSelected && hasSelectedCount >= mMaxChoseCount) {
                ToastUtil.showToast(context, context.getString(R.string.most_chose_count_with_value, String.valueOf(mMaxChoseCount)));
                return;
            }
            imageBean.isSelected = !imageBean.isSelected;
            holder.imageview_cover.setVisibility(imageBean.isSelected ? View.VISIBLE : View.GONE);
            holder.imageview_checked.setVisibility(imageBean.isSelected ? View.VISIBLE : View.GONE);

            hasSelectedCount += (imageBean.isSelected ? 1 : -1);
            updateSureBtnStatus(hasSelectedCount);
        }


        private class ViewHolder {
            ImageView imageview_show, imageview_cover, imageview_checked;

            public ViewHolder(View itemView) {
                this.imageview_show = itemView.findViewById(R.id.iv_image_show);
                this.imageview_cover = itemView.findViewById(R.id.iv_cover);
                this.imageview_checked = itemView.findViewById(R.id.iv_checked);
            }
        }

    }


    private class FindImageTask extends AsyncTask<ArrayList<String>, Void, ArrayList<ImageBean>> {


        private WeakReference<PickPhotosActivity> activity;
        private int oldSelectedCount;

        public FindImageTask(PickPhotosActivity mActivity) {
            this.activity = new WeakReference<>(mActivity);
        }

        @Override
        protected ArrayList<ImageBean> doInBackground(ArrayList<String>... params) {
            ArrayList<String> oldData = params[0];
            ArrayList<ImageBean> finds = null;
            try {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = mApplication.getContentResolver();
                Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                if (mCursor != null && mCursor.getCount() > 0) {

                    int minFileLength = 20 * 1024;

                    finds = new ArrayList<>(mCursor.getCount() / 2);
                    while (mCursor.moveToNext()) {
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        int length = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        if (path != null && length >= minFileLength) {
                            boolean hasSelected = oldData != null && oldData.contains(path);
                            if (hasSelected)
                                oldSelectedCount++;
                            finds.add(new ImageBean(hasSelected, path));
                        }

                    }
                    mCursor.close();
                    Collections.reverse(finds);
                }

            } catch (Exception e) {
            }

            return finds;
        }

        @Override
        protected void onPostExecute(ArrayList<ImageBean> imageBeenList) {
            if (activity != null && activity.get() != null && !activity.get().isFinishing()) {
                try {
                    activity.get().findLocalImageBack(imageBeenList, oldSelectedCount);
                } catch (Exception e) {

                }
            }
        }
    }


    private static class ImageBean {
        boolean isSelected;
        String imagePath;

        ImageBean(boolean isSelected, String imagePath) {
            this.isSelected = isSelected;
            this.imagePath = imagePath;
        }
    }

    @Override
    public String getPageTitle() {
        return mResources.getString(R.string.page_pick_photo);
    }
}
