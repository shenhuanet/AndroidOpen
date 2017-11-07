package com.shenhua.swipetorefreshlayout.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * 刷新时加载的动画
 * Created by shenhua on 8/26/2016.
 */
public class BaseRefreshLoadingView extends ImageView {

    private AlphaAnimation.AnimationListener listener;
    private int shadowRadius;// 阴影半径

    public BaseRefreshLoadingView(Context context) {
        super(context);
    }

    /**
     * BaseRefreshLoadingView 的构造方法
     *
     * @param context     上下文
     * @param circleColor 圆圈的填充颜色
     * @param radius      圆圈的半径
     */
    public BaseRefreshLoadingView(Context context, int circleColor, final float radius) {
        super(context);
        float density = context.getResources().getDisplayMetrics().density;// 密度
        int diameter = (int) (radius * density * 2);// 直径
        int xShadowOffset = (int) (density * 0f); // x方向阴影偏移量
        int yShadowOffset = (int) (density * 1.75f); // y方向阴影偏移量
        shadowRadius = (int) (density * 3.5f);
        ShapeDrawable circle;
        if (Build.VERSION.SDK_INT >= 21) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
        } else {
            CircleShape cs = new CircleShape(shadowRadius, diameter);
            circle = new ShapeDrawable(cs);
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(shadowRadius, xShadowOffset, yShadowOffset, 0x1E000000);
            int padding = shadowRadius;
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(circleColor);
        setBackgroundDrawable(circle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (Build.VERSION.SDK_INT < 21)
            setMeasuredDimension(getMeasuredWidth() + shadowRadius * 2, getMeasuredHeight() + shadowRadius * 2);
    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
        if (listener != null)
            listener.onAnimationStart(getAnimation());
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        if (listener != null)
            listener.onAnimationEnd(getAnimation());
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            Resources res = getResources();
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        this.listener = listener;
    }

    private class CircleShape extends OvalShape {

        int[] gradientColor = {0x3D000000, Color.TRANSPARENT};
        int shadowRadius, circleDiameter;
        Paint mShadowPaint;

        /**
         * @param shadowRadius   阴影半径
         * @param circleDiameter 圆圈直径
         */
        public CircleShape(int shadowRadius, int circleDiameter) {
            super();
            mShadowPaint = new Paint();
            this.shadowRadius = shadowRadius;
            this.circleDiameter = circleDiameter;
            RadialGradient rg = new RadialGradient(circleDiameter / 2,
                    circleDiameter / 2, shadowRadius, gradientColor,
                    null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(rg);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            int viewW = BaseRefreshLoadingView.this.getWidth();
            int viewH = BaseRefreshLoadingView.this.getHeight();
            canvas.drawCircle(viewW / 2, viewH / 2, (circleDiameter / 2 + shadowRadius), mShadowPaint);
            canvas.drawCircle(viewW / 2, viewH / 2, (circleDiameter / 2), paint);
        }
    }
}
