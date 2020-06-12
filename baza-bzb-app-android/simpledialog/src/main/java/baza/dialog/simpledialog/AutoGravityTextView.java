package baza.dialog.simpledialog;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by Vincent.Lei on 2018/8/31.
 * Title：
 * Note：
 */
public class AutoGravityTextView extends TextView {
    public AutoGravityTextView(Context context) {
        this(context, null);
    }

    public AutoGravityTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoGravityTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getLineCount() > 1 && getGravity() != Gravity.LEFT) {
            setGravity(Gravity.LEFT);
            invalidate();
            return;
        }
        if (getLineCount() == 1 && getGravity() != Gravity.CENTER) {
            setGravity(Gravity.CENTER);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }
}
