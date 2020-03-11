package com.wislie.customview.listener;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-11 21:09
 * desc   : 优化监听器
 * version: 1.0
 */
public interface OnOptimizeListener {
    /**
     * 开始优化
     */
    void startOptimize();

    /**
     * 取消优化
     */
    void pauseOptimize();

    /**
     * 结束任务
     */
    void completeTask();
}
