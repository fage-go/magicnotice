package com.zhangshuo.magicnotice.led.factory;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

import com.zhangshuo.magicnotice.led.LightView;
import com.zhangshuo.magicnotice.led.model.CutoutInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;

public class CutoutLight implements LightFactory {
    private LightView lightView;
    private Path drawPath;
    private PathMeasure drawPathMeasure;
    private Path drawProgressPath;
    private float drawLength;

    @Override
    public void onInit(LightView lightView) {
        this.lightView = lightView;
        initDrawPath();
        lightView.setShadow(lightView.getPaint(), lightView.getData().getStrokeWidth());
        lightView.getPaint().setStyle(Paint.Style.STROKE);
        lightView.getPaint().setAlpha(255);
    }

    @Override
    public void drawLight(Canvas canvas, float progress) {
        if (lightView == null) return;
        progress = Math.max(0, progress);
        progress = Math.min(progress, 1);
        int animType = lightView.getData().getAnimType();
        if (animType == LightModel.ANIM_FILL) {
            Path path = calculateDrawPath(progress);
            if (path != null) {
                canvas.drawPath(path, lightView.getPaint());
            }
        } else if (animType == LightModel.ANIM_FADE) {
            int alpha = (int) (255 * progress);
            lightView.getPaint().setAlpha(alpha);
            canvas.drawPath(drawPath, lightView.getPaint());
        } else if (animType == LightModel.ANIM_MOVE) {
            Path path = calculateMoveDrawPath(progress);
            if (path != null) {
                canvas.drawPath(path, lightView.getPaint());
            }
        } else if (animType == LightModel.ANIM_BREATH) {
            int radius = (int) (lightView.getData().getStrokeWidth() * 4 * progress);
            int stroke = (int) (lightView.getData().getStrokeWidth() + lightView.getData().getStrokeWidth() * progress);
            lightView.getPaint().setStrokeWidth(stroke);
            lightView.setShadow(lightView.getPaint(), radius);
            canvas.drawPath(drawPath, lightView.getPaint());
        }
    }

    @Override
    public long getAnimDuration(int speed) {
        if (speed == 0) {
            return 3000;
        } else if (speed == 1) {
            return 2000;
        } else if (speed == 2) {
            return 1000;
        }
        return 2000;
    }

    @Override
    public void onDestory() {
        lightView = null;
        drawPath = null;
        drawPathMeasure = null;
    }

    public Path calculateDrawPath(float progress) {
        drawProgressPath.reset();
        if (drawPathMeasure == null) return drawProgressPath;

        if (progress < 0.9) {
            float newProgress = progress / 0.9f;
            float endPoint = drawLength * newProgress;
            drawPathMeasure.getSegment(0, endPoint, drawProgressPath, true);
        } else {
            drawPathMeasure.getSegment(0, drawLength, drawProgressPath, true);
        }
        return drawProgressPath;
    }

    public Path calculateMoveDrawPath(float progress) {
        drawProgressPath.reset();
        if (drawPathMeasure == null) return drawProgressPath;

        float newProgress = progress / 0.8f;
        float startPoint = drawLength * (newProgress - 0.2f);
        float endPoint = drawLength * newProgress;
        drawPathMeasure.getSegment(startPoint, endPoint, drawProgressPath, true);
        return drawProgressPath;
    }

    private void initDrawPath() {
        //添加摄像头位置
        if (drawPath == null) {
            drawPath = new Path();
        }
        CutoutInfo cutoutInfo = lightView.getCutoutInfo();
        drawPath.reset();
        drawPath.addRoundRect(cutoutInfo.getLeft(), cutoutInfo.getTop(), cutoutInfo.getRight(), cutoutInfo.getBottom(), cutoutInfo.getRadius(), cutoutInfo.getRadius(), Path.Direction.CW);
        drawPathMeasure = new PathMeasure(drawPath, false);
        drawLength = (int) drawPathMeasure.getLength();
        drawProgressPath = new Path();
    }

}
