package com.zhangshuo.magicnotice.led;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.magicnotice.led.factory.BorderLight;
import com.zhangshuo.magicnotice.led.factory.CutoutLight;
import com.zhangshuo.magicnotice.led.factory.LightFactory;
import com.zhangshuo.magicnotice.led.factory.SlideLight;
import com.zhangshuo.magicnotice.led.model.CutoutInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;
import com.zhangshuo.magicnotice.util.ColorUtil;

/**
 * 基础的控制器
 */
public class LightView extends View {

    protected Paint paint;
    protected LightModel data;
    protected CutoutInfo cutoutInfo;

    protected ValueAnimator valueAnimator;
    protected float progressValue = 0;
    protected int width;
    protected int height;
    private boolean isMock = false;
    private boolean autoAnim = false;

    private LightFactory lightFactory;
    private OnDismissListener onDismissListener;

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                progressValue = 0f;
                postInvalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                progressValue = 1f;
                postInvalidate();
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        });
    }

    public void setMock(boolean mock) {
        isMock = mock;
        valueAnimator.setRepeatCount(-1);
    }

    public void setAutoAnim(boolean autoAnim) {
        this.autoAnim = autoAnim;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setColorArray(int[] colorArray) {
        setColorArray(paint, colorArray);
    }

    public void setColorArray(Paint paint, int[] colorArray) {
        if (colorArray == null) return;
        paint.setAlpha(255);
        if (colorArray.length == 1) {
            paint.setColor(colorArray[0]);
            paint.setShader(null);
        } else {
            LinearGradient gradient = new LinearGradient(0, 0, width, height,
                    colorArray, null, Shader.TileMode.CLAMP);
            paint.setShader(gradient);
        }
    }


    public void setShadow(Paint paint, int radius) {
        paint.setShadowLayer(radius, 0, 0, ColorUtil.getColor(data.getColorShadow())); // 设置阴影
    }

    public void setStrokeWidth(int strokeWidth) {
        float rate = width * 1f / ScreenUtils.getScreenWidth() * 2;
        this.paint.setStrokeWidth(strokeWidth * rate);
    }

    public void setAnima(int repeatCount, int animType) {
        if (animType == LightModel.ANIM_FADE || animType == LightModel.ANIM_BREATH || data.getViewType() == LightModel.TYPE_SLIDE) {
            valueAnimator.setRepeatCount(isMock ? -1 : (repeatCount - 1) * 2);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        } else {
            valueAnimator.setRepeatCount(isMock ? -1 : repeatCount - 1);
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
    }

    public void setLightViewType(int viewType) {
        if (lightFactory != null) {
            lightFactory.onDestory();
        }
        if (viewType == LightModel.TYPE_BORDER) {
            lightFactory = new BorderLight();
        } else if (viewType == LightModel.TYPE_CUTOUT) {
            lightFactory = new CutoutLight();
        } else if (viewType == LightModel.TYPE_SLIDE) {
            lightFactory = new SlideLight();
        }
        if (lightFactory != null) {
            lightFactory.onInit(this);
            long animDuration = lightFactory.getAnimDuration(data.getSpeed());
            if (valueAnimator != null)
                valueAnimator.setDuration(animDuration);
        }
    }

    public void initView() {
        float rate = width * 1f / ScreenUtils.getScreenWidth();
        CutoutInfo tmpInfo = LightUtil.getInstance().getCutoutInfo();
        if (tmpInfo != null) {
            cutoutInfo = tmpInfo.copy(rate);
        } else {
            cutoutInfo = new CutoutInfo();
        }
        stopAnim();
        //设置颜色
        String colorArray = data.getColorArray();
        setColorArray(ColorUtil.getColorArray(colorArray));
        //设置阴影
        setShadow(paint, data.getStrokeWidth());
        //设置粗细
        setStrokeWidth(data.getStrokeWidth());
        //设置动画时间
        setAnima(data.getRepeatCount(), data.getAnimType());
        //设置View类型
        setLightViewType(data.getViewType());
        if (autoAnim) {
            startAnim();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) return;
        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();
            initView();
        }

        if (lightFactory != null) {
            lightFactory.drawLight(canvas, progressValue);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
        if (lightFactory != null) {
            lightFactory.onDestory();
        }
        lightFactory = null;
    }

    public CutoutInfo getCutoutInfo() {
        return cutoutInfo;
    }

    public LightModel getData() {
        return data;
    }

    public Paint getPaint() {
        return paint;
    }

    public ValueAnimator getValueAnimator() {
        return valueAnimator;
    }

    public void bindData(LightModel data) {
        this.data = data;
        this.width = 0;
        this.height = 0;
        postInvalidate();
    }

    public void setProgress(float progress) {
        this.progressValue = progress;
    }

    public void startAnim() {
        if (valueAnimator == null || valueAnimator.isRunning()) return;
        valueAnimator.start();
    }

    public void stopAnim() {
        if (valueAnimator == null) return;
        valueAnimator.cancel();
    }


    public LightView(Context context) {
        super(context);
        init();
    }

    public LightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
