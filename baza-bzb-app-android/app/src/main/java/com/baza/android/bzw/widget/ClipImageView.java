package com.baza.android.bzw.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by LW on 2016/07/06
 * Title : 截取图片
 * Note :
 */
public class ClipImageView extends MultiTouchZoomableImageView {

    public static final int TYPE_CIRCLE = 1, TYPE_SQUARE = 2;

    private Paint mPaint;

    private Path path;

    private int type = TYPE_CIRCLE;

    private int width, height, targetWidth;

    private RectF oval;


    private Rect selection;
    protected static final int MARGIN = 50;

    private int outputX;
    private int outputY;

    public ClipImageView(Context context) {
        this(context, null);
    }

    public ClipImageView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
        oval = new RectF(0, 0, 0, 0);
        transIgnoreScale = true;
        selection = new Rect(0, 0, 0, 0);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        targetWidth = Math.min(width, height) - 2 * MARGIN;
        preparePath();

        mPaint.setColor(Color.parseColor("#AF000000"));
        canvas.drawPath(path, mPaint);

    }


    private void preparePath() {
        path.reset();

        int middleWidth = width / 2;
        int left = middleWidth - targetWidth / 2;
        int top = height / 2 - targetWidth / 2;
        int right = left + targetWidth;
        int bottom = top + targetWidth;


        if (type == TYPE_CIRCLE) {
            path.moveTo(0, 0);
            path.lineTo(middleWidth, 0);
            path.lineTo(middleWidth, top);
            oval.set(left, top, right, bottom);
            path.addArc(oval, -90, -180);
            path.lineTo(middleWidth, bottom);
            path.lineTo(middleWidth, height);
            path.lineTo(0, height);
            path.lineTo(0, 0);
//
            path.moveTo(middleWidth, 0);
            path.lineTo(middleWidth, top);
            path.addArc(oval, -90, 180);
            path.lineTo(middleWidth, bottom);
            path.lineTo(middleWidth, height);
            path.lineTo(width, height);
            path.lineTo(width, 0);
            path.lineTo(middleWidth, 0);
            path.close();
        } else {
            path.moveTo(0, 0);
            path.lineTo(middleWidth, 0);
            path.lineTo(middleWidth, top);
            path.lineTo(left, top);
            path.lineTo(left, bottom);
            path.lineTo(middleWidth, bottom);
            path.lineTo(middleWidth, height);
            path.lineTo(0, height);
            path.lineTo(0, 0);

            path.moveTo(middleWidth, 0);
            path.lineTo(middleWidth, top);
            path.lineTo(right, top);
            path.lineTo(right, bottom);
            path.lineTo(middleWidth, bottom);
            path.lineTo(middleWidth, height);
            path.lineTo(width, height);
            path.lineTo(width, 0);
            path.lineTo(middleWidth, 0);
            path.close();
        }

    }


    public Bitmap getInterceptImage() {

        Bitmap outer;
        setDrawingCacheEnabled(true);
        Bitmap source = getDrawingCache();

        mPaint.setAlpha(255);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        int middleWidth = width / 2;
        int middleHeight = height / 2;
        int radius = targetWidth / 2;

        int count = canvas.saveLayer(0, 0, width, height, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.save();

        if (type == TYPE_CIRCLE)
            canvas.drawCircle(middleWidth, middleHeight, radius, mPaint);
        else
            canvas.drawRect(middleWidth - radius, middleHeight - radius, middleWidth + radius, middleHeight + radius, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(count);

        setDrawingCacheEnabled(false);
        source.recycle();

        outer = Bitmap.createBitmap(bitmap, middleWidth - radius, middleHeight - radius, targetWidth, targetWidth);
        bitmap.recycle();

        return outer;
    }


    public void setInterceptType(int type) {
        if ((type == TYPE_CIRCLE || type == TYPE_SQUARE) && this.type != type) {
            this.type = type;
            invalidate();
        }
    }


    //////////////////////////////////


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            updateSelection();
        }
    }

    public void setOutput(int outputX, int outputY) {
        this.outputX = outputX;
        this.outputY = outputY;
    }

//

    @Override
    protected Rect updateSelection() {
        if (outputX <= 0 || outputY <= 0) {
            return null;
        }

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float outputRatio = ((float) outputY) / outputX;
        float screenRatio = ((float) viewHeight) / viewWidth;
        if (outputRatio < screenRatio) {
            int width = viewWidth - MARGIN * 2;
            int height = outputY * width / outputX;
            int x = MARGIN;
            int y = (viewHeight - height) / 2;
            selection.set(x, y, x + width, y + height);
        } else {
            int height = viewHeight - MARGIN * 2;
            int width = outputX * height / outputY;
            int y = MARGIN;
            int x = (viewWidth - width) / 2;
            selection.set(x, y, x + width, y + height);
        }
        return selection;
    }

//

    @Override
    protected void center(boolean vertical, boolean horizontal, boolean animate) {
        if (mBitmap == null)
            return;
        if (selection == null) {
            invalidate();
            return;
        }

        Matrix m = getImageViewMatrix();

        float[] topLeft = new float[]{0, 0};
        float[] botRight = new float[]{mBitmap.getWidth(), mBitmap.getHeight()};

        translatePoint(m, topLeft);
        translatePoint(m, botRight);

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            if (topLeft[1] > selection.bottom) {
                deltaY = selection.bottom - topLeft[1];
            } else if (botRight[1] < selection.top) {
                deltaY = selection.top - botRight[1];
            }
        }

        if (horizontal) {
            if (topLeft[0] > selection.right) {
                deltaX = selection.right - topLeft[0];
            } else if (botRight[0] < selection.left) {
                deltaX = selection.left - botRight[0];
            }
        }

        postTranslate(deltaX, deltaY);
        if (animate) {
            Animation a = new TranslateAnimation(-deltaX, 0, -deltaY, 0);
            a.setStartTime(SystemClock.elapsedRealtime());
            a.setDuration(250);
            setAnimation(a);
        }
        setImageMatrix(getImageViewMatrix());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);

        if (mBitmap != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (fling()) {
                    if (checkImagePosition(false)) {
                        stopFling();
                    }


                } else {
                    checkImagePosition(true);
                }
            }
        }

        return handled;
    }


    // Sets the bitmap for the image and resets the base
    public void setImageBitmap(final Bitmap bitmap) {
        super.setImageBitmap(bitmap, selection);
    }

    /**
     * 把图片移动回到截屏的矩形框内，保证图片能完全覆盖截屏矩形
     *
     * @return
     * @date 2014年5月22日
     */
    protected boolean checkImagePosition(boolean scroll) {

        boolean translate = false;
        if (mBitmap == null || selection == null) {
            return translate;
        }
        Matrix m = getImageViewMatrix();

        float[] topLeft = new float[]{0, 0};
        float[] botRight = new float[]{mBitmap.getWidth(), mBitmap.getHeight()};

        translatePoint(m, topLeft);
        translatePoint(m, botRight);
        float transX = 0.0f;
        float transY = 0.0f;

        if (topLeft[0] > selection.left) {
            transX = selection.left - topLeft[0];
            translate = true;
        } else if (botRight[0] < selection.right) {
            transX = selection.right - botRight[0];
            translate = true;
        }

        if (topLeft[1] > selection.top) {
            transY = selection.top - topLeft[1];
            translate = true;
        } else if (botRight[1] < selection.bottom) {
            transY = selection.bottom - botRight[1];
            translate = true;
        }

        if (scroll && translate) {
            //直线动画的效果移回
            scrollBy(transX, transY, 200);
        }

        return translate;
    }

    @Override
    protected void onScrollFinish() {
        checkImagePosition(true);
    }
}
