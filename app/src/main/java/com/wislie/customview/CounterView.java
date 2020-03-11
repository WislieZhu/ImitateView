package com.wislie.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-11 10:26
 * desc   : 计数器
 * version: 1.0
 */
public class CounterView extends View {

    private String TAG = "CounterView";

    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private float spSize = 23;

    private float textWidth = 100;

    /*** 起始坐标 */
    private float startX, startY;
    /*** 变化前的x坐标 */
    private float preStartX;

    /*** 可变的下标 */
    private float variantStartX;

    //之前的值
    private int oldValue;
    //当前的值
    private int curValue;

    //不变的部分
    private String invariantFaction;

    //变化前的部分
    private String variantPreFaction;

    //变化后的部分
    private String variantCurFaction;

    /*** 文字的高度 */
    private int textHeight;

    /*** 动画,用来y方向偏移 */
    private ObjectAnimator mAnimator;

    /*** y方向偏移量 */
    private float offsetTextY;

    private CounterListener mCounterListener;

    private Random rd = new Random();

    public CounterView(Context context) {
        this(context, null);
    }

    public CounterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        textWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textWidth, getResources().getDisplayMetrics());
        mTextPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, spSize, getResources().getDisplayMetrics()));
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setStyle(Paint.Style.FILL);


        /*** 初始化显示的数值*/
        reset();

        /**
         * 这一步的目的是为了获取数字的高度
         */
        String numberStr = "0123456789";
        Rect rect = new Rect();
        mTextPaint.getTextBounds(numberStr, 0, numberStr.length(), rect);
        textHeight = rect.height();

        mAnimator = ObjectAnimator.ofFloat(this, "offsetTextY", 0, textHeight);
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (curValue == 100) {
                    if (mCounterListener != null) {
                        mCounterListener.finishCount();
                    }
                }
            }
        });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = (int) textWidth;
        int calWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec));

        int height = textHeight * 3;
        int calHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));

        super.onMeasure(calWidthMeasureSpec, calHeightMeasureSpec);
    }

    public void setCounterListener(CounterListener counterListener) {
        this.mCounterListener = counterListener;
    }

    public void setOffsetTextY(float offsetTextY) {
        this.offsetTextY = offsetTextY;
        invalidate();
    }

    private void reset(){
        oldValue = rd.nextInt(100);
        curValue = oldValue;
        invariantFaction = String.valueOf(curValue);
    }

    public void changeValue(int delta) {

        if (curValue >= 100) {
            stopAnim();
            reset();
            return;
        }

        curValue += delta;

        /**
         * 如果不变
         */
        if (curValue == oldValue) return;
        if (curValue < oldValue) throw new IllegalArgumentException("curValue < oldValue");

        String oldValueStr = String.valueOf(oldValue);
        String curValueStr = String.valueOf(curValue);

        int oldValueLength = oldValueStr.length();
        int curValueLength = curValueStr.length();

        //当前起始点坐标
        PointF curPoint = calValXY(curValue);
        startX = curPoint.x;
        startY = curPoint.y;

        /**
         * 如果两者的长度不一致,说明值的所有部分都变了
         */
        if (oldValueLength != curValueLength) {
            invariantFaction = "";
            variantPreFaction = oldValueStr;
            variantCurFaction = curValueStr;

            //没有变动则值相等
            variantStartX = startX;

            //之前的坐标值
            PointF prePoint = calValXY(oldValue);
            preStartX = prePoint.x;

        } else {

            /**
             * 比较第几个开始发生变化
             */

            for (int i = 0; i < curValueLength; i++) {

                /**
                 * 两者如果不一样,说明从i开始发生变化
                 */
                if (oldValueStr.charAt(i) != curValueStr.charAt(i)) {
                    //下标0-i,为不变的部分
                    invariantFaction = curValueStr.substring(0, i);
                    //下标i-curValueLength,为变化的部分
                    variantPreFaction = oldValueStr.substring(i, curValueLength);
                    variantCurFaction = curValueStr.substring(i, curValueLength);
                    break;
                }

            }

            //变化数值的起始坐标
            if (variantCurFaction != null) {
                float invariantTextWidth = getTextWidth(invariantFaction);
                variantStartX = startX + invariantTextWidth;
            }
            //坐标x相等
            preStartX = variantStartX;

        }
        oldValue = curValue;

        stopAnim();
        //启动动画
        mAnimator.start();
    }

    /**
     * 停止动画
     */
    private void stopAnim() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
            mAnimator.end();
        }
    }

    /**
     * 获取text的宽度
     *
     * @param text
     * @return
     */
    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    /**
     * 计算当前值的坐标
     */
    private PointF calValXY(int value) {
        float textWidth = getTextWidth(String.valueOf(value));
        float x = getWidth() / 2 - textWidth / 2;
        float y = getHeight() / 2 + textHeight / 2;
        return new PointF(x, y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        PointF p = calValXY(curValue);
        startX = p.x;
        startY = p.y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //不可变的部分
        if (!TextUtils.isEmpty(invariantFaction)) {
            //透明度不变
            mTextPaint.setAlpha(255);
            canvas.drawText(invariantFaction, startX, startY, mTextPaint);
        }


        //变化前的值
        if (variantPreFaction != null) {
            // 透明度从 255-0
            mTextPaint.setAlpha((int) (255 * (1 - offsetTextY / textHeight)));
            canvas.drawText(variantPreFaction, preStartX, startY - offsetTextY * 2, mTextPaint);
        }


        //变化后的值
        if (variantCurFaction != null) {
            // 透明度从 0-255
            mTextPaint.setAlpha((int) (255 * offsetTextY / textHeight));
            canvas.drawText(variantCurFaction, variantStartX, startY + 2 * (textHeight - offsetTextY), mTextPaint);
        }
    }

    interface CounterListener {
        /**
         * 停止计数
         */
        void finishCount();
    }
}
