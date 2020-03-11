package com.wislie.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.wislie.customview.listener.OnOptimizeListener;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-11 10:06
 * desc   :
 * version: 1.0
 */
public class HWOptimizeLayout extends FrameLayout implements HWOptimizeView.OnUpdateListener, CounterView.CounterListener {

    private HWOptimizeView mHwOptimizeView;
    private CounterView mCounterView;

    private OnOptimizeListener mOnOptimizeListener;

    private int count = 0;

    public HWOptimizeLayout(Context context) {
        this(context, null);
    }

    public HWOptimizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        /**
         * 转动的圆环
         */
        mHwOptimizeView = new HWOptimizeView(context, attrs);
        mHwOptimizeView.setListener(this);
        addView(mHwOptimizeView, lp);

        /**
         * 计数
         */
        mCounterView = new CounterView(context, attrs);
        mCounterView.setCounterListener(this);
        addView(mCounterView, lp);
    }

    /**
     * 入口
     */
    public void start() {

        //如果是暂停状态,则继续优化
        if (mHwOptimizeView.isPause() && mHwOptimizeView.isRunning()) {
            if (mOnOptimizeListener != null) {
                mOnOptimizeListener.startOptimize();
            }
        } else {
            //如果未启动
            if (!mHwOptimizeView.isRunning()) {
                if (mOnOptimizeListener != null) {
                    mOnOptimizeListener.startOptimize();
                }
            } else {
                //如果已经启动了
                if (mOnOptimizeListener != null) {
                    mOnOptimizeListener.pauseOptimize();
                }
            }
        }
    }

    public void startAnim() {
        mHwOptimizeView.startAnim();
    }

    public void pauseAnim() {
        mHwOptimizeView.pauseAnim();
    }

    public void setOnOptimizeListener(OnOptimizeListener onOptimizeListener) {
        this.mOnOptimizeListener = onOptimizeListener;
    }

    @Override
    public void tryUpdate() { //更新计数
        if (mCounterView != null) {
            //模拟
            if (count++ % 15 == 0) {
                mCounterView.changeValue(1);
                count = count % 15;
            }
        }
    }

    @Override
    public void finishCount() { //结束计数
        if (mHwOptimizeView != null) {
            mHwOptimizeView.stop();
        }
        if (mOnOptimizeListener != null) {
            mOnOptimizeListener.completeTask();
        }
    }
}
