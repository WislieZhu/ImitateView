package com.wislie.customview.vivo.helper;

import android.graphics.Canvas;

import com.wislie.customview.vivo.VivoLoadingView;

import java.util.List;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020/3/21 8:39 PM
 * desc   : 加载策略抽象类
 * version: 1.0
 */
public abstract class LoadingStrategy {

    /**
     * 自定义view所需要的宽高
     */
    protected int requiredWidth, requiredHeight;

    /**
     * 生成dots点集合
     */
    public abstract List<VivoLoadingView.Dot> generateDots(int startColor, int endColor);

    /**
     * 计算位移
     */
    public abstract void calculateShift(List<VivoLoadingView.Dot> dotList);

    /**
     * 画布位移
     */
   public abstract void shiftCanvas(Canvas canvas);

    public int getRequiredWidth() {
        return requiredWidth;
    }

    public int getRequiredHeight() {
        return requiredHeight;
    }
}
