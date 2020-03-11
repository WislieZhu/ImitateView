package com.wislie.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import androidx.annotation.RequiresApi;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-09 19:22
 * desc   : 华为一键优化
 * version: 1.0
 */
public class HWOptimizeView extends View {

    private String TAG = "HWOptimizeView";

    /*** 上层path */
    private Path mUpperPath = new Path();
    /*** 上层count的数量 */
    private final int UPPER_COUNT = 30;
    /*** 上层count的数量 */
    private Paint mUpperPaint = new Paint();
    /*** 上层颜色 */
    private int[] mUpperColors = {
            Color.parseColor("#d7d8dc"), Color.parseColor("#5966f7")
    };
    /*** 位置 */
    private float[] positions = {0, 0.25f};

    /*** 被覆盖的path */
    private Path mInnerPath = new Path();
    /*** 被覆盖count的数量 */
    private final int INNER_COUNT = 120;
    /*** 被覆盖的paint */
    private Paint mInnerPaint = new Paint();
    /*** 被覆盖的颜色 */
    private int mInnerColor = Color.parseColor("#e0e0e0");

    /*** 画笔宽度 */
    private float strokeWidth = 10;
    /*** 用来确定绘制的区域 */
    private RectF mCircleRect = new RectF();

    /*** 底层颜色 */
    private int mBaseColor = Color.parseColor("#f6f6f6");
    /*** 底层画笔宽度 */
    private float baseStrokeWidth = 18;
    /*** 底层 */
    private Paint mBasePaint = new Paint();
    /*** 最右侧圆圈与base层的间隔 */
    private float mSpacing = 5;
    /*** base层的半径 */
    private float baseRadius;
    /*** 最外层的画笔宽度 */
    private float mOuterStrokeWidth = 2;
    /*** 最外层的半径 */
    private float outRadius;

    /*** 动画 */
    private ValueAnimator animator;
    /*** 进度 */
    private int progress = 0;
    /*** 动画的最大值 */
    private int maxValue = 120;

    /*** 默认的半径 */
    private final int REQUEST_RADIUS = 200;
    /*** 请求的半径 */
    private int requestRadius;

    private OnUpdateListener mListener;

    public HWOptimizeView(Context context) {
        super(context);
    }

    public HWOptimizeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        animator = ValueAnimator.ofInt(0, maxValue);
        //设置paint属性
        mUpperPaint.setStyle(Paint.Style.STROKE);
        mUpperPaint.setAntiAlias(true);
        mUpperPaint.setDither(true);
        strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth,
                getResources().getDisplayMetrics());
        mUpperPaint.setStrokeWidth(strokeWidth);
        mInnerPaint.set(mUpperPaint);
        mBasePaint.set(mUpperPaint);
        mBasePaint.setColor(mBaseColor);

        baseStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, baseStrokeWidth,
                getResources().getDisplayMetrics());
        mBasePaint.setStrokeWidth(baseStrokeWidth);
        mSpacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mSpacing,
                getResources().getDisplayMetrics());
        mOuterStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mOuterStrokeWidth,
                getResources().getDisplayMetrics());
        requestRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, REQUEST_RADIUS,
                getResources().getDisplayMetrics());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                //设置位置和大小

                int width = getWidth();
                int height = getHeight();

                float left = mOuterStrokeWidth + mSpacing + baseStrokeWidth - strokeWidth + strokeWidth / 2;
                float top = left;
                float right = width - left;
                float bottom = height - top;
                mCircleRect.set(left, top, right, bottom);

                baseRadius = getWidth() / 2 - mOuterStrokeWidth - mSpacing - baseStrokeWidth / 2;

                outRadius = getWidth() / 2 - mOuterStrokeWidth / 2;

                initOuterPath();
                initInnerPath();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue() * 360 / maxValue;
                if (mListener != null) {
                    mListener.tryUpdate();
                }
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(requestRadius, widthMeasureSpec);
        int height = getSize(requestRadius, heightMeasureSpec);

        int radius = Math.min(width, height);
        setMeasuredDimension(radius, radius);
    }

    public void setListener(OnUpdateListener listener) {
        this.mListener = listener;
    }

    /**
     * 测量宽高
     *
     * @param size
     * @param measureSpec
     * @return
     */
    public static int getSize(int size, int measureSpec) {
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        final int result;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = size;
        }
        return result;
    }

    private void initOuterPath() {
        //将rect添加到path中
        mUpperPath.addArc(mCircleRect, 0, 90);
        //获取PathEffect
        DashPathEffect dashPathEffect = getPathEffect(mUpperPath, UPPER_COUNT);
        mUpperPaint.setPathEffect(dashPathEffect);
        //设置paint的渐变色
        Shader shader = new SweepGradient(getWidth() / 2, getHeight() / 2, mUpperColors, positions);
        mUpperPaint.setShader(shader);
    }

    private void initInnerPath() {
        //将rect添加到path中
        mInnerPath.addArc(mCircleRect, 0, 360);
        //获取PathEffect
        DashPathEffect dashPathEffect = getPathEffect(mInnerPath, INNER_COUNT);
        mInnerPaint.setPathEffect(dashPathEffect);
        mInnerPaint.setColor(mInnerColor);
    }

    private DashPathEffect getPathEffect(Path path, int count) {
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        float step = length / count;
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{step / 3, step * 2 / 3}, 0);
        return dashPathEffect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBasePaint.setStrokeWidth(mOuterStrokeWidth);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, outRadius, mBasePaint);
        mBasePaint.setStrokeWidth(baseStrokeWidth);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, baseRadius, mBasePaint);

        //灰色的圆
        canvas.drawPath(mInnerPath, mInnerPaint);
        //蓝色渐变的1/4圆
        canvas.save();
        canvas.rotate(progress, getWidth() / 2, getHeight() / 2);
        canvas.drawPath(mUpperPath, mUpperPaint);
        canvas.restore();
    }

    /**
     * 启动动画
     */
    public void startAnim() {
        if (!isPause()) {
            animator.start();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.resume();
            }
        }

    }

    /**
     * 暂停动画
     */
    public void pauseAnim() {
        if (!isPause()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.pause();
            }
        }
    }

    /**
     * 判断是否暂停
     *
     * @return
     */
    public boolean isPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return animator.isPaused();
        }
        return false;
    }

    public boolean isRunning() {
        return animator.isRunning();
    }

    /**
     * 停止
     */
    public void stop() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator.end();
        }
    }

    public interface OnUpdateListener {
        /**
         * 尝试更新数值
         */
        void tryUpdate();
    }

}
