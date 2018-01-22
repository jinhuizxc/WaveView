package com.example.jh.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 水波纹特效
 * Created by fbchen2 on 2016/5/25.
 * <p>
 * 关于View的onMeasure()、onSizeChanged()、onLayout()、onDraw()调用顺序
 * <p>
 * 继承与View和继承与现有控件都是下面的顺序，但是控件的大小是生成之后就固定的，不会再次改变。
 * onMeasure()→onSizeChanged()→onLayout()→onMeasure()→onLayout()→onDraw()
 */
public class WaveView extends View {

    private static final String TAG = "WaveView";
    private float mInitialRadius;   // 初始波纹半径
    private float mMaxRadius;   // 最大波纹半径
    private long mDuration = 2000; // 一个波纹从创建到消失的持续时间
    private int mSpeed = 500;   // 波纹的创建速度，每500ms创建一个
    private float mMaxRadiusRate = 0.85f;
    private boolean mMaxRadiusSet;

    private boolean mIsRunning;
    private long mLastCreateTime;  //
    // 链表存储产生的圆
    private List<Circle> mCircleList = new ArrayList<Circle>();

    private Runnable mCreateCircle = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                newCircle();
                // 不加postDelayed的话只能绘制一个圆
                // 要做的事情，这里再次调用此Runnable对象，
                // 以实现每两秒实现一次的定时器操作
                postDelayed(mCreateCircle, mSpeed);
            }
        }
    };

    private Interpolator mInterpolator = new LinearInterpolator();

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStyle(Paint.Style style) {
        mPaint.setStyle(style);
    }

    /**
     * onSizeChanged()方法是系统自动调用的。
     * w，h是view当前的宽和高;oldw ,oldh是改变之前的宽和高。
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //   w, h, oldw, oldh = 1080,1750,0,0
        Log.e(TAG, "w, h, oldw, oldh = " + w + "," + h + "," + oldw + "," + oldh);
        if (!mMaxRadiusSet) {
            mMaxRadius = Math.min(w, h) * mMaxRadiusRate / 2.0f;
            Log.e(TAG, "Math.min(w, h) =" + Math.min(w, h)); // 返回最小的那个数字
            Log.e(TAG, "mMaxRadius =" + mMaxRadius);  // mMaxRadius =459.0 = 1080*0.85/2.0
        }
    }

    public void setMaxRadiusRate(float maxRadiusRate) {
        mMaxRadiusRate = maxRadiusRate;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 开始
     */
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            // 线程启动，产生圆圈
            mCreateCircle.run();
        }
    }

    /**
     * 缓慢停止
     */
    public void stop() {
        mIsRunning = false;
    }

    /**
     * 立即停止
     */
    public void stopImmediately() {
        mIsRunning = false;
        mCircleList.clear();
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        Iterator<Circle> iterator = mCircleList.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            float radius = circle.getCurrentRadius();
            // 当前时间 - 创建时间 < 持续时间，则置为透明
            if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);
            } else {
                iterator.remove();
            }
        }

        if (mCircleList.size() > 0) {
            postInvalidateDelayed(10);
        }
    }

    public void setInitialRadius(float radius) {
        mInitialRadius = radius;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public void setMaxRadius(float maxRadius) {
        mMaxRadius = maxRadius;
        mMaxRadiusSet = true;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    /**
     * 产生圆圈
     */
    private void newCircle() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - mLastCreateTime < mSpeed) {
            return;
        }
        Circle circle = new Circle();
        mCircleList.add(circle);
        invalidate();
        mLastCreateTime = currentTime;
    }

    private class Circle {

        private long mCreateTime;

        Circle() {
            mCreateTime = System.currentTimeMillis();
        }

        // 关于这里getAlpha、getCurrentRadius的方法计算不是那么的明白
        int getAlpha() {
            float percent = (getCurrentRadius() - mInitialRadius) / (mMaxRadius - mInitialRadius);
            return (int) (255 - mInterpolator.getInterpolation(percent) * 255);
        }

        float getCurrentRadius() {
            // percent: 百分比
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }
}
