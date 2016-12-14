package com.zhuoxin.phone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.icu.util.Measure;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.zhuoxin.phone.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hd on 2016/11/18.
 */

public class CleanCircleView extends View {
    int currentAngle = 90;
    int width;
    int height;
    RectF oval = new RectF();
    boolean isback;
    public static boolean isRunning;
    public CleanCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (width < height) {
            height = width;
        } else {
            width = height;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        if (Build.VERSION.SDK_INT >= 23) {
            paint.setColor(getResources().getColor(R.color.bluePrimaryColor, null));
            oval = new RectF(0, 0, getWidth(), getHeight());
            canvas.drawArc(oval, -90, currentAngle, true, paint);
        }

    }

    public void setTargetAngle(final int targetAngle) {
        //先倒退,定义计时器来执行
        isback = true;

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (isback) {
                    if (currentAngle > 0) {
                        currentAngle -= 4;
                    } else {
                        currentAngle = 0;
                        isback = false;
                    }
                    postInvalidate();
                } else {
                    if (targetAngle > currentAngle) {
                        currentAngle += 4;
                    } else {
                        currentAngle = targetAngle;
                        isRunning = false;
                        timer.cancel();
                    }
                    postInvalidate();
                }
            }
        };
        timer.schedule(timerTask, 40, 40);
    }
}
