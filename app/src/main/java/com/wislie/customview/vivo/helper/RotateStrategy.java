package com.wislie.customview.vivo.helper;

import android.animation.ArgbEvaluator;
import android.graphics.Canvas;

import com.wislie.customview.util.DegreeUtil;
import com.wislie.customview.vivo.VivoLoadingView;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020/3/21 8:53 PM
 * desc   : 旋转加载
 * version: 1.0
 */
public class RotateStrategy extends LoadingStrategy {
    /*** 圆的半径 */
    private float circleRadius;
    private float dotRadius;
    private int dotCount;
    private boolean isScaled;
    /*** 平均角度 */
    private int averageAngle;
    /*** 旋转的角度 */
    private float rotateAngle;

    private static final int TOTAL_ANGLE = 360;

    public RotateStrategy(float circleRadius, float dotRadius, int dotCount, boolean isScaled) {
        this.circleRadius = circleRadius;
        this.dotRadius = dotRadius;
        this.dotCount = dotCount;
        this.isScaled = isScaled;
        averageAngle = TOTAL_ANGLE / dotCount;
        requiredWidth = requiredHeight = (int) ((circleRadius + dotRadius) * 2);
    }

    @Override
    public List<VivoLoadingView.Dot> generateDots(int startColor, int endColor) {
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        //点集合
        List<VivoLoadingView.Dot> dotList = new ArrayList<>();
        for (int i = 0; i < dotCount; i++) {
            //当前的角度
            int curAngle = i * averageAngle;
            //当前的弧度
            double curRad = DegreeUtil.toRadians(curAngle);
            //计算当前的坐标
            float x = (float) DegreeUtil.getCosSideLength(circleRadius, curRad);
            float y = (float) DegreeUtil.getSinSideLength(circleRadius, curRad);
            //计算当前的颜色
            float fraction = curAngle * 1.0f / TOTAL_ANGLE;
            //当前颜色
            int color = (int) argbEvaluator.evaluate(fraction, endColor, startColor);

            float radius = dotRadius;
            if (isScaled) {
                radius = fraction * dotRadius;
            }
            VivoLoadingView.Dot dot = new VivoLoadingView.Dot(x, y, radius, color);
            dotList.add(dot);
        }

        return dotList;
    }

    @Override
    public void calculateShift(List<VivoLoadingView.Dot> dotList) {
        if (rotateAngle >= TOTAL_ANGLE) {
            rotateAngle = rotateAngle - TOTAL_ANGLE;
        } else {
            //每次叠加一个圆点的角度，就不会觉得在圆圈转动，而是点在切换
            rotateAngle += averageAngle;
        }
    }

    @Override
    public void shiftCanvas(Canvas canvas) {
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.rotate(rotateAngle);
    }
}
