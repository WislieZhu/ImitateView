package com.wislie.customview.vivo.helper;

import android.animation.ArgbEvaluator;
import android.graphics.Canvas;

import com.wislie.customview.vivo.VivoLoadingView;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020/3/21 8:55 PM
 * desc   : 平移策略
 * version: 1.0
 */
public class TranslateStrategy extends LoadingStrategy {

    private int dotCount;
    private float interval;
    private float dotRadius;
    /*** x轴方向的偏移量 */
    private float translateX;

    public TranslateStrategy(int dotCount, float interval, float dotRadius) {
        this.dotCount = dotCount;
        this.interval = interval;
        this.dotRadius = dotRadius;
        requiredWidth = (int) (dotRadius * 2 * dotCount + interval * (dotCount - 1));
        requiredHeight = (int) (dotRadius * 2);
        translateX = interval + dotRadius;
    }

    @Override
    public List<VivoLoadingView.Dot> generateDots(int startColor, int endColor) {
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        //点集合
        List<VivoLoadingView.Dot> dotList = new ArrayList<>();
        for (int i = 0; i < dotCount; i++) {
            float x = dotRadius * (i + 1) + i * (interval + dotRadius);
            float y = dotRadius;
            //计算当前的颜色
            float fraction = i * 1.0f / dotCount;
            //当前颜色
            int color = (int) argbEvaluator.evaluate(fraction, endColor, startColor);
            VivoLoadingView.Dot dot = new VivoLoadingView.Dot(x, y, dotRadius, color);
            dotList.add(dot);
        }
        return dotList;
    }

    @Override
    public void calculateShift(List<VivoLoadingView.Dot> dotList) {
        for (int i = 0; i < dotList.size(); i++) {
            VivoLoadingView.Dot dot = dotList.get(i);
            //设置每个点的x坐标
            dot.x = dot.x + translateX;
            if (dot.x >= requiredWidth) {
                dot.x = dotRadius;
            }
        }
    }

    @Override
    public void shiftCanvas(Canvas canvas) {

    }
}
