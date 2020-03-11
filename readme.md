![hw_optimize](https://tva1.sinaimg.cn/large/00831rSTly1gcqew8jaaug305k09w79v.gif)

可以看到有1/4的圆在不停的转动; 每隔一段时间,中间的数字在增大，类似点赞的动画(将录频转成gif帧变化加快了很多)

功能实现比较简单的, 我这里拆分成 自定义圆环和自定义的数字,分别添加到FrameLayout中

**一. 自定义圆环**

1.绘制圆环的虚线, PathEffect有个子类 DashPathEffect,会将path截成虚线

```java
PathMeasure pathMeasure = new PathMeasure(path, false);
float length = pathMeasure.getLength();
float step = length / count;
DashPathEffect dashPathEffect = new DashPathEffect(new float[]{step / 3, step * 2 / 3}, 0);
paint.setPathEffect(dashPathEffect);
```

2.1/4圆环有一个渐变,沿着圆弧从0度到90度蓝色越来越深,这里使用SweepGradient实现

```java
SweepGradient(float cx, float cy, @ColorInt int[] colors, float[] positions)
```

cx, cy 分别对应 圆心的x坐标和y坐标

colors: 分布在圆环上各个position的颜色, position之间的颜色渐变

positions:范围在0-1.0

因为是1/4圆, 所以colors有2个元素,positions为[0,0.25f]

```java
//将rect添加到path中
mUpperPath.addArc(mCircleRect, 0, 90);
//获取PathEffect
DashPathEffect dashPathEffect = getPathEffect(mUpperPath, UPPER_COUNT);
mUpperPaint.setPathEffect(dashPathEffect);
//设置paint的渐变色
Shader shader = new SweepGradient(getWidth() / 2, getHeight() / 2, mUpperColors, positions);
mUpperPaint.setShader(shader);
```

由于只有蓝色的1/4圆在旋转, 灰色的圆一动不动; 那么先绘制灰色的圆, 然后绘制蓝色的1/4圆, 覆盖在灰色的圆弧上

```java
//灰色的圆
canvas.drawPath(mInnerPath, mInnerPaint);
//蓝色渐变的1/4圆
canvas.save();
canvas.rotate(progress, getWidth() / 2, getHeight() / 2);
canvas.drawPath(mUpperPath, mUpperPaint);
canvas.restore();
```

通过ValueAnimator不断更新, 来改变progress的旋转角度值, 这里要注意蓝色的1/4圆虚线要保持和灰色的圆弧重合,因此progress的变化也和1/4圆虚线的数量有关

**二.跳动的数字**

1.类似很多app上点赞的动画, 不过这里只考虑数字变大; 数字的变化有2种, 如0->1, 和 9->10, 因此要考虑数字的哪几位没变,哪几位变了（变之前的和变之后的)

```java
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
```

二.绘制的时候,变化的那几位会有一个y方向的位移, 位移的变化可通过属性动画实现

```java
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
```

最后添加2个自定义视图到FrameLayout时要注意先后顺序, 由于FrameLayout采用的是wrap_content,  对两个自定义View大小的测量选型关注下

代码链接:https://github.com/A18767101271/HWOptimizeView