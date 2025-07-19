package in.ecomexpress.sruti.utils.common_files.progress_bar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class CustomProgressBar extends SeekBar {

    private ArrayList<ProgressItem> mProgressItemsList = new ArrayList<ProgressItem>();
    int totalListitem;

    public CustomProgressBar(Context context) {
        super(context);
        mProgressItemsList = new ArrayList<ProgressItem>();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initData(ArrayList<ProgressItem> progressItemsList, int totalListitem) {
        this.mProgressItemsList = progressItemsList;
        this.totalListitem = totalListitem;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onDraw(Canvas canvas) {
        if (mProgressItemsList.size() > 0) {
            int progressBarWidth = getWidth();
            int progressBarHeight = getHeight();
            int thumboffset = getThumbOffset();
            int lastProgressX = 0;
            int progressItemWidth, progressItemRight;
            for (int i = 0; i < mProgressItemsList.size(); i++) {
                ProgressItem progressItem = mProgressItemsList.get(i);
                Paint progressPaint = new Paint();
                progressPaint.setColor(getResources().getColor(progressItem.color));
                progressItemWidth = (int) (progressItem.progressItemPercentage
                        * progressBarWidth / totalListitem);

                progressItemRight = lastProgressX + progressItemWidth;

                // for last item give right to progress item to the width
                if (i == mProgressItemsList.size() - 1 && progressItemRight != progressBarWidth) {
                    progressItemRight = progressBarWidth;
                }
                // Rect progressRect = new Rect();
                RectF rect = new RectF(0, 0, progressItemWidth, progressBarHeight);
                //  Paint mPaint = new Paint();
                rect.set(lastProgressX, thumboffset / 8, progressItemRight, progressBarHeight - thumboffset / 8);
                canvas.drawRoundRect(rect, 5, 5, progressPaint);
//                progressRect.set(lastProgressX, thumboffset/* / 2*/, progressItemRight, progressBarHeight - thumboffset/* / 2*/);
//                canvas.drawRect(progressRect, progressPaint);
                lastProgressX = progressItemRight;
            }
            super.onDraw(canvas);
        }

    }

}
