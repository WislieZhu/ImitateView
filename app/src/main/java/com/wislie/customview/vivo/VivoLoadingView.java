package com.wislie.customview.vivo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.wislie.customview.vivo.helper.RotateStrategy;
import com.wislie.customview.vivo.helper.LoadingStrategy;
import com.wislie.customview.vivo.helper.TranslateStrategy;
import com.wislie.hwoptimizelayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020/3/21 9:18 AM
 * desc   : 加载
 * version: 1.0
 * 参考 https://www.jianshu.com/p/cd7a2a24ef23
 */
public class VivoLoadingView extends View {

    private String TAG = "VivoLoadingView";

    /*** 开始颜色 */
    private int startColor;
    /*** 结束颜色 */
    private int endColor;
    /*** 点半径 */
    private float dotRadius = 5;
    /*** 旋转的半径 */
    private float circleRadius = 25;
    /*** 点数量 */
    private int dotCount;

    /*** 点集合 */
    private List<Dot> mDotList = new ArrayList<>();

    private Paint mPaint = new Paint();
    /*** 刷下的间隔 */
    private int refreshDuration;
    /*** 点是否缩放 */
    private boolean isScaled;
    /*** 是否旋转 */
    private boolean isRotated;
    /*** 水平间隔 */
    private float interval;

    private Handler mHandler = new Handler();

    private LoadingStrategy mStrategy;

    public VivoLoadingView(Context context) {
        this(context, null);
    }

    public VivoLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VivoLoadingView);
        //是否缩放
        isScaled = ta.getBoolean(R.styleable.VivoLoadingView_isScaled, false);
        //点的数量
        dotCount = ta.getInt(R.styleable.VivoLoadingView_dotCount, 8);
        //旋转
        isRotated = ta.getBoolean(R.styleable.VivoLoadingView_isRotated, false);
        //刷下的时间间隔
        refreshDuration = ta.getInt(R.styleable.VivoLoadingView_refreshDuration, 65);
        //水平间隔
        interval = ta.getFloat(R.styleable.VivoLoadingView_interval, 25);
        ta.recycle();

        //开始颜色和结束颜色
        startColor = Color.argb(255, 180, 180, 180);
        endColor = Color.argb(100,
                Color.red(startColor), Color.green(startColor), Color.blue(startColor));

        dotRadius = dpToPx(dotRadius);
        circleRadius = dpToPx(circleRadius);
        interval = dpToPx(interval);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);

        if (isRotated) {
            mStrategy = new RotateStrategy(circleRadius, dotRadius, dotCount, isScaled);
        } else {
            mStrategy = new TranslateStrategy(dotCount, interval, dotRadius);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getSize(widthMeasureSpec, mStrategy.getRequiredWidth()),
                getSize(heightMeasureSpec, mStrategy.getRequiredHeight()));
    }

    private int getSize(int measureSpec, int requestSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            //确定大小
            case MeasureSpec.EXACTLY:
                if (specSize < requestSize) {
                    result = requestSize;
                }
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < requestSize) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = requestSize;
                }
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = requestSize;
        }
        return result;
    }


    /**
     * dp转换为px
     *
     * @param value
     * @return
     */
    private float dpToPx(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //计算变换后的位置
            mStrategy.calculateShift(mDotList);
            invalidate();
            mHandler.postDelayed(this, refreshDuration);
        }
    };

    /*** 开始加载 */
    public void startLoading() {
        if (mDotList.size() <= 0) {
            //添加点
            mDotList = mStrategy.generateDots(startColor, endColor);
        }
        mHandler.postDelayed(mRunnable, refreshDuration);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoading();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //变换画布
        mStrategy.shiftCanvas(canvas);
        for (int i = 0; i < mDotList.size(); i++) {
            Dot dot = mDotList.get(i);
            mPaint.setColor(dot.color);
            canvas.drawCircle(dot.x, dot.y, dot.radius, mPaint);
        }
    }

    public static class Dot {
        //点坐标
        public float x, y;
        //点半径
        public float radius;
        //点颜色
        public int color;

        public Dot(float x, float y, float radius, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }
    }
}
